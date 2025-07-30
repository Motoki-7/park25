package com.example.demo.rest;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.dao.RangeDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.entity.RangeEntity;
import com.example.demo.entity.RatesEntity;
import com.example.demo.form.ParkingDetailForm;
import com.example.demo.form.RatesRangeDto;
import com.example.demo.util.HolidayUtils;

@Service
public class ParkingRestService {
	@Autowired ParkinglotDao parkinglotDao;
	@Autowired RatesDao ratesDao;
	@Autowired RangeDao rangeDao;
	
	
	
	//条件検索
	public List<ParkinglotEntity> select(String name, String address1){
		List<ParkinglotEntity> parkinglotList;
		boolean isNameEmpty = (name == null || name.isEmpty());
	    boolean isAddressEmpty = (address1 == null || address1.isEmpty());
		
		//両方空欄の場合に全件表示
		if(isNameEmpty && isAddressEmpty) {
			parkinglotList = parkinglotDao.selectAll();
		}	
		//nameが空欄で、address1が入力された場合　→　address1で検索
		else if(isNameEmpty && !isAddressEmpty) {
			parkinglotList = parkinglotDao.selectByAddress1(address1);
		}
		//address1が空欄で、nameが入力された場合　→　nameで検索
		else if(!isNameEmpty && isAddressEmpty) {
			parkinglotList = parkinglotDao.selectByName(name);
		}
		//両方が入力された場合　→　両方で検索
		else {
			parkinglotList = parkinglotDao.selectByNameAndAddress1(name,address1);
		}
		return parkinglotList;
	}	
	
	//削除処理
	public void delete(int parkinglotId) {
		parkinglotDao.delete(parkinglotId);
		// IDリスト取得
		List<RatesEntity> ratesEntList = ratesDao.selectIdListByParkinglotId(parkinglotId);
		// 削除処理
		for (RatesEntity ratesEnt : ratesEntList) {
			if (ratesEnt.getRangeId() != null) {
				rangeDao.delete(ratesEnt.getRangeId());
			}
			ratesDao.delete(ratesEnt.getRatesId());
		}
	}

    // 料金シミュレーション
	public List<Integer> calculate(
            int id,
            LocalDate entryDate, LocalDate exitDate,
            LocalTime entryTime, LocalTime exitTime
    ) {
		/* 日時入力のバリエーションはViewで実装しているため省略
        // 1) 入退庫日時チェック
        LocalDateTime in  = LocalDateTime.of(entryDate, entryTime);
        LocalDateTime out = LocalDateTime.of(exitDate,  exitTime);
        if (out.isBefore(in)) {
            throw new IllegalArgumentException("退庫日時が入庫日時より前です");
        }
        */

        // 2) View→Form, 入出庫日の準備
        ParkingDetailForm form = selectById(id);
        LocalDateTime in  = LocalDateTime.of(entryDate, entryTime);
        LocalDateTime out = LocalDateTime.of(exitDate,  exitTime);

        // 3) 総分数
        int totalMinutes = (int) Duration.between(in, out).toMinutes();

        // 4) 料金計算
        int totalFee;
        if (form.getBaseFeeRadio() == 0) {
            // 終日固定パターン
            totalFee = calculateFixedFee(form, in, out);
        } else {
            // 基本料金パターン（複数の RatesRangeDto に対応）
            totalFee = calculateBasicFee(form, in, out);
        }

        return Arrays.asList(totalFee, totalMinutes);
    }

    // 終日固定[baseFeeRadio = 0] 終日固定(=fixed)の料金パターンを扱う
    private int calculateFixedFee(
            ParkingDetailForm form,
            LocalDateTime in, LocalDateTime out
    ) {
        int amount   = form.getAmountDaily();
        int timeUnit = form.getTimeDailly();
        Integer max24   = form.getMaxRate24h();
        Integer maxDay  = form.getMaxRateDaily();
        
     // [optionRadio = null] 最大料金ナシ
     //    最初にnullチェックを行わないとnull参照でエラーになる
      if (Boolean.TRUE.equals(form.getOptionRadio() == null)) {
    	  return calcFee(in, out, amount, timeUnit);
      }
      // [optionRadio = 0] 24時間ごとの最大料金アリ
      else if (Boolean.TRUE.equals(form.getOptionRadio() == 1) && maxDay != null) {
    	  return calc24hCapFee(in, out, amount, timeUnit, max24);
      }
      // [optionRadio = 1] 日付締めによる最大料金アリ
      else {
    	  return calcDailyCapFee(in, out, amount, timeUnit, maxDay);
      }
    }
    // 終日固定：24時間ごとの最大料金アリ
    private int calc24hCapFee(
            LocalDateTime in, LocalDateTime out,
            int amount, int timePer, int max24
    ) {
    	// 駐車時間から24時間の回数分feeに加算
        long totalMin = Duration.between(in, out).toMinutes();
        long days     = totalMin / (24 * 60);
        int fee = (int) (days * max24);
        // 余り分数を単位料金と乗算，最大料金と比較し低い方をfeeに加算
        long rem = totalMin % (24 * 60);
        if (rem > 0) {
            int units  = (int) Math.ceil((double) rem / timePer);
            int part   = units * amount;
            fee += Math.min(part, max24);
        }
        return fee;
    }
    // 終日固定：日付締めによる最大料金アリ
    private int calcDailyCapFee(
            LocalDateTime in, LocalDateTime out,
            int amount, int timePer, int maxDaily
    ) {
        int fee = 0;
        LocalDateTime cursor = in;
        while (!cursor.isAfter(out)) {
        	// 日付ごとの料金を計算，最大料金と比較し低い方をfeeに加算
            LocalDate   day    = cursor.toLocalDate();
            LocalDateTime dayEnd = day.atTime(LocalTime.MAX);	// 該当日の終了日時
            LocalDateTime segEnd = out.isBefore(dayEnd) ? out : dayEnd;	// outがdayEndよりあと前ならoutを終了日付に

            long min = Duration.between(cursor, segEnd).toMinutes(); // 
            int units = (int) Math.ceil((double) min / timePer);
            fee += Math.min(units * amount, maxDaily);
            
            cursor = segEnd.plusSeconds(1); // 終了時刻の1秒後を次ループ開始位置に。翌日もしくはループ終了位置を意味する。
        }
        return fee;
    }
    // 終日固定：最大料金ナシ
    private int calcFee(
            LocalDateTime in, LocalDateTime out,
            int amount, int timePer
    ) {
        long min = Duration.between(in, out).toMinutes();
        int units = (int) Math.ceil((double) min / timePer);
        return units * amount;
    }

    // 基本料金[baseFeeRadio = 1] 基本料金の料金パターンを扱う
    private int calculateBasicFee(
            ParkingDetailForm form,
            LocalDateTime in, LocalDateTime out
    ) {
        int fee = 0;
        LocalDateTime cursor = in;
        while (!cursor.isAfter(out)) {
        	// カレンダー日ごとに料金を計算，segEnd(終了日時) >= out(出庫日時) になるまでループ
        	// 類似：calclateFixedFee / calcDailyCapFee
            LocalDate   day    = cursor.toLocalDate();
            LocalDateTime dayEnd = day.atTime(LocalTime.MAX);
            LocalDateTime segEnd = out.isBefore(dayEnd) ? out : dayEnd;

            fee += calcDailyBasicFee(form.getDailyList(), day, cursor, segEnd);
            cursor = segEnd.plusSeconds(1);
        }
        return fee;
    }
    // 基本料金：1日ごとの料金計算
    private int calcDailyBasicFee(
            List<RatesRangeDto> dailyList,
            LocalDate date,
            LocalDateTime in, LocalDateTime out
    ) {
        int fee = 0;
        DayOfWeek dow   = date.getDayOfWeek();	// dateの曜日をDayOfWeek「列挙型」で取得
        boolean  hol   = HolidayUtils.isHoliday(date); //祝日判定 HolidayUtils : utils内クラスファイル

        // date(該当日)が基本料金パターンにあてはまるまでループ
        // dailyList[i]が曜日含む→日時の整形→
        for (RatesRangeDto dto : dailyList) {
            // 1. 曜日／祝日フィルタ
            // 祝日ならば次のdailyListへ，falseならば次の処理へ
        	if (hol) {
                if (!dto.isHoliday()) continue;
            }
        	// いずれかfalse(=当てはまる)ならば次の処理へ
        	else {
                switch (dow) {
                    case MONDAY:    if (!dto.isMonday())    continue; break;
                    case TUESDAY:   if (!dto.isTuesday())   continue; break;
                    case WEDNESDAY: if (!dto.isWednesday()) continue; break;
                    case THURSDAY:  if (!dto.isThursday())  continue; break;
                    case FRIDAY:    if (!dto.isFriday())    continue; break;
                    case SATURDAY:  if (!dto.isSaturday())  continue; break;
                    case SUNDAY:    if (!dto.isSunday())    continue; break;
                }
            }

            // 2. 時間帯のDateTime化
        	// 計算処理前にdailyListの開始時刻・終了時刻を終日か判定，DateTimeに設定
            LocalDateTime startDT, endDT;
            int  s = dto.getStartTime(), e = dto.getEndTime();
            if (s == e) {
                // 終日(start:00:00,end:00:00 を終日とする)
                startDT = date.atTime(LocalTime.MIN);
                endDT   = date.plusDays(1).atTime(LocalTime.MIN);
            } else {
                LocalTime tStart = LocalTime.of(s / 100, s % 100);
                LocalTime tEnd   = LocalTime.of(e / 100, e % 100);
                startDT = date.atTime(tStart);
                endDT   = (s < e)	// 例：s:22:00, e:08:00 の場合 → e を翌日に
                          ? date.atTime(tEnd)
                          : date.plusDays(1).atTime(tEnd);  // 深夜越え
            }

            // 3. 重なり判定
            //  2と比較し計算の開始・終了時刻を決定
            	// dailyListでstart/endDTを計算しているためユーザ入力値で実際の計算値を上書き
            LocalDateTime segStart = in.isAfter(startDT) ? in : startDT; // inがstartDTより後ならtrue
            LocalDateTime segEnd   = out.isBefore(endDT)  ? out : endDT; // outがendDTより前ならtrue
            if (!segStart.isBefore(segEnd)) continue;	// 既に該当日の計算終了の場合ループを終了

            // 4. 単位時間チャージ＋範囲上限
            long     mins  = Duration.between(segStart, segEnd).toMinutes(); 
            int      units = (int) Math.ceil((double) mins / dto.getTime());
            int      part  = units * dto.getAmount();
            if (dto.getMaxRateTimely() != null) {
                part = Math.min(part, dto.getMaxRateTimely());
            }
            fee += part;
        }
        return fee;
    }
    
	// データの取得 <ParkingDetailForm.java>
	private ParkingDetailForm selectById(int parkinglotId){
		ParkingDetailForm form = new ParkingDetailForm();
		// parkinglot, rates データをDao経由取得
		List<ParkinglotEntity> resultParkinglotList = parkinglotDao.selectById(parkinglotId);
		ParkinglotEntity parkinglotEnt = resultParkinglotList.get(0);
		List<RatesEntity> resultRatesEntList = ratesDao.selectByParkinglotId(parkinglotId);
		// ratesからrangeが存在するか確認、なければｆｒｏｍＥｎｔｉｔｙ叩いてリターン
		for(RatesEntity ent : resultRatesEntList) {
			if(ent.getRangeId() == null) return form.fromEntity(parkinglotEnt, resultRatesEntList);
		}
		// range データをDao経由取得
		List<RangeEntity> resultRangeEntList = new ArrayList<RangeEntity>();
		for (RatesEntity ratesEnt : resultRatesEntList) {
			RangeEntity rangeEnt = rangeDao.selectById((Integer)ratesEnt.getRangeId()).get(0);
			resultRangeEntList.add(rangeEnt);
		}
		return form.fromEntity(parkinglotEnt, resultRatesEntList, resultRangeEntList);
	}
}
