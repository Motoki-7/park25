package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.dao.RangeDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.entity.RangeEntity;
import com.example.demo.entity.RatesEntity;
import com.example.demo.form.EditParkingForm;
import com.example.demo.form.RatesRangeDto;
import com.example.demo.util.HolidayUtils;

@Service
public class EditParkingService {
	@Autowired ParkinglotDao parkinglotDao;
	@Autowired RatesDao ratesDao;
	@Autowired RangeDao rangeDao;
	
	
//	基本の設計：
//	①2重ループ文を作り、外側でRatesリストを1つずつ取り出し、内側でidがDBに存在するか確認する
//	②2重ループの外側でフラグ(boolean)を定義し、特定の動作を行ったかどうかブロックをまたいで処理する
	public void update(EditParkingForm form) {
	    // 1. 駐車場情報の更新
	    ParkinglotEntity parkinglotEnt = form.toParkinglotEntity();
	    //  1時間あたり料金の計算・Form反映
	    int hourlyRate = calculate(form);
	    parkinglotEnt.setHourlyRate(hourlyRate);
	    // paringlot DAO登録処理
	    parkinglotDao.update(parkinglotEnt);
	    int parkinglotId = parkinglotEnt.getId();

	    // 2. フォームから新しい料金情報と時間帯情報を取得
	    List<RatesEntity> newRatesList = form.toRatesEntityList();

	    // 3. 既存の料金情報を取得（rates, range idリスト）
	    List<RatesEntity> existingRatesList = ratesDao.selectIdListByParkinglotId(parkinglotId);

	    // 4. 新しいフォームデータに対する処理
	    for (int i = 0; i < newRatesList.size(); i++) {
	        RatesEntity newRates = newRatesList.get(i);
	        boolean isUpdate = false;		// update処理フラグ
	        for (RatesEntity existing : existingRatesList) {
	            if (newRates.getRatesId() == existing.getRatesId()) {
	                isUpdate = true;
	                if (form.getBaseFeeRadio() == 0) {
	                    // → 終日固定に変更：range削除 + rates更新 (rangeId=null)
	                    if (existing.getRangeId() != null) {
	                        rangeDao.delete(existing.getRangeId());
	                    }
	                    newRates.setRangeId(null);
	                    ratesDao.update(newRates);
	                } else {
	                    // → 時間帯指定：range更新 + rates更新
	                	List<RangeEntity> newRangeList = form.toRangeEntityList();
	                    RangeEntity updatedRange = newRangeList.get(i);
	                    updatedRange.setRangeId(existing.getRangeId());
	                    rangeDao.update(updatedRange);
	                    newRates.setRangeId(existing.getRangeId());
	                    ratesDao.update(newRates);
	                }
	                break;
	            }
	        }
	        // update実行してないなら、insert 実行
	        if (!isUpdate) {
	            if (form.getBaseFeeRadio() == 0) {
	                newRates.setRangeId(null);
	                ratesDao.insert(newRates, parkinglotId, null);
	            } else {
	            	List<RangeEntity> newRangeList = form.toRangeEntityList();
	                RangeEntity newRange = newRangeList.get(i);
	                int newRangeId = rangeDao.insert(newRange);
	                newRates.setRangeId(newRangeId);
	                ratesDao.insert(newRates, parkinglotId, newRangeId);
	            }
	        }
	    }
	    // 5. 削除処理：新しいフォームに存在しないデータへの対処
	    for (RatesEntity existing : existingRatesList) {
	        boolean stillExists = false;		// 存在するかのフラグ
	        for (RatesEntity newRates : newRatesList) {
	            if (newRates.getRatesId() == existing.getRatesId()) {
	                stillExists = true;
	                break;
	            }
	        }
	        if (!stillExists) {
	            if (existing.getRangeId() != null) {
	                rangeDao.delete(existing.getRangeId());
	            }
	            ratesDao.delete(existing.getRatesId());
	        }
	    }
	}
	
	
	// 料金計算
	public int calculate(EditParkingForm form) {
        // まず「1日あたりの料金」を求める
        int dailyFee;
        if (form.getBaseFeeRadio() == 0) {
            // 終日固定パターン
            dailyFee = calculationFixedFee(form);
        } else {
            // 基本料金パターン
            // maxRate24h があれば計算省略
            if (form.getMaxRate24h() != null) {
                return form.getMaxRate24h() / 24;
            }
            // パターンごとの1日料⾦を平均
            dailyFee = calculationBasicAverageDailyFee(form);
        }
        // 1日の平均を24で割って「1時間あたり」に
        return dailyFee / 24;
    }

    /**
     * 終日固定パターンの「1日あたり料金」を計算する。
     */
    private int calculationFixedFee(EditParkingForm form) {
        // 今日を基準に24時間（00:00 ～ 翌00:00）でシミュレーション
        LocalDate today     = LocalDate.now();
        LocalDateTime in    = today.atTime(LocalTime.MIN);
        LocalDateTime out   = today.plusDays(1).atTime(LocalTime.MIN);

        int amount    = form.getAmountDaily();   // 終日料金
        int timeUnit  = form.getTimeDailly();    // 単位時間 (分)
        Integer max24   = form.getMaxRate24h();   // 24時間上限
        Integer maxDay  = form.getMaxRateDaily(); // 日付締め上限

        if (Boolean.TRUE.equals(form.getOptionRadio() == 0) && max24 != null) {
            return calc24hCapFee(in, out, amount, timeUnit, max24);
        } else if (Boolean.TRUE.equals(form.getOptionRadio() == 1) && maxDay != null) {
            return calcDailyCapFee(in, out, amount, timeUnit, maxDay);
        } else {
            return calcFee(in, out, amount, timeUnit);
        }
    }
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
    private int calcFee(
            LocalDateTime in, LocalDateTime out,
            int amount, int timePer
    ) {
        long min = Duration.between(in, out).toMinutes();
        int units = (int) Math.ceil((double) min / timePer);
        return units * amount;
    }

    /**
     * 基本料金パターンの「1日あたり料金」を、
     * dailyList の各パターンで計算し、その平均を返す。
     */
    private int calculationBasicAverageDailyFee(EditParkingForm form) {
        List<RatesRangeDto> list = form.getDailyList();
        if (list == null || list.isEmpty()) {
            return 0;
        }
        // 今日の00:00～翌00:00を想定
        LocalDate     today = LocalDate.now();
        LocalDateTime in    = today.atTime(LocalTime.MIN);
        LocalDateTime out   = today.plusDays(1).atTime(LocalTime.MIN);

        int sum = 0;
        for (RatesRangeDto dto : list) {
            // １パターンだけをリスト化して、その日の基本料金を計算
            int dayFee = calcDailyBasicFee(
                Collections.singletonList(dto),
                today, in, out
            );
            sum += dayFee;
        }
        // 平均化（切り捨て）
        return sum / list.size();
    }
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
}
/*
アイデア
・max24hがあればそれを24で割った値にする
・maxDailyも同様
・optionRadioがnullかつ終日なら単位時間(hour)あたりの料金を算出。
・上同かつ基本ありなら、各条件での1日あたりの料金算出、最大あれば適用して24で除算
*/