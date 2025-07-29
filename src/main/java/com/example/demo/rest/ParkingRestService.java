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
        // 1) 入退庫日時チェック
        LocalDateTime in  = LocalDateTime.of(entryDate, entryTime);
        LocalDateTime out = LocalDateTime.of(exitDate,  exitTime);
        if (out.isBefore(in)) {
            throw new IllegalArgumentException("退庫日時が入庫日時より前です");
        }

        // 2) View→Form
        ParkingDetailForm form = selectById(id);

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

    // --- 終日固定パターン ---
    private int calculateFixedFee(
            ParkingDetailForm form,
            LocalDateTime in, LocalDateTime out
    ) {
        int amount   = form.getAmountDaily();
        int timeUnit = form.getTimeDailly();
        Integer max24   = form.getMaxRate24h();
        Integer maxDay  = form.getMaxRateDaily();

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
        long totalMin = Duration.between(in, out).toMinutes();
        long days     = totalMin / (24 * 60);
        int fee = (int) (days * max24);

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
            LocalDate   day    = cursor.toLocalDate();
            LocalDateTime dayEnd = day.atTime(LocalTime.MAX);
            LocalDateTime segEnd = out.isBefore(dayEnd) ? out : dayEnd;

            long min = Duration.between(cursor, segEnd).toMinutes();
            int units = (int) Math.ceil((double) min / timePer);
            fee += Math.min(units * amount, maxDaily);

            cursor = segEnd.plusSeconds(1);
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

    // --- 基本料金パターン ---
    private int calculateBasicFee(
            ParkingDetailForm form,
            LocalDateTime in, LocalDateTime out
    ) {
        int fee = 0;
        LocalDateTime cursor = in;
        while (!cursor.isAfter(out)) {
            LocalDate   day    = cursor.toLocalDate();
            LocalDateTime dayEnd = day.atTime(LocalTime.MAX);
            LocalDateTime segEnd = out.isBefore(dayEnd) ? out : dayEnd;

            fee += calcDailyBasicFee(form.getDailyList(), day, cursor, segEnd);
            cursor = segEnd.plusSeconds(1);
        }
        return fee;
    }

    private int calcDailyBasicFee(
            List<RatesRangeDto> dailyList,
            LocalDate date,
            LocalDateTime in, LocalDateTime out
    ) {
        int fee = 0;
        DayOfWeek dow   = date.getDayOfWeek();
        boolean  hol   = HolidayUtils.isHoliday(date);

        for (RatesRangeDto dto : dailyList) {
            // 1) 曜日／祝日フィルタ
            if (hol) {
                if (!dto.isHoliday()) continue;
            } else {
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

            // 2) 時間帯のDateTime化
            LocalDateTime startDT, endDT;
            int  s = dto.getStartTime(), e = dto.getEndTime();
            if (s == e) {
                // 終日
                startDT = date.atTime(LocalTime.MIN);
                endDT   = date.plusDays(1).atTime(LocalTime.MIN);
            } else {
                LocalTime tStart = LocalTime.of(s / 100, s % 100);
                LocalTime tEnd   = LocalTime.of(e / 100, e % 100);
                startDT = date.atTime(tStart);
                endDT   = (s < e)
                          ? date.atTime(tEnd)
                          : date.plusDays(1).atTime(tEnd);  // 深夜越え
            }

            // 3) 重なり判定
            LocalDateTime segStart = in.isAfter(startDT) ? in : startDT;
            LocalDateTime segEnd   = out.isBefore(endDT)  ? out : endDT;
            if (!segStart.isBefore(segEnd)) continue;

            // 4) 単位時間チャージ＋範囲上限
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
