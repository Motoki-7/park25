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
	
	
	
	// 計算シミュレーション
	public List<Integer> calculate(
			int id, 
			LocalDate entryDate, LocalDate exitDate, 
			LocalTime entryTime, LocalTime exitTime
	){
		// 1. 入退庫日時の準備
        LocalDateTime in  = LocalDateTime.of(entryDate, entryTime);
        LocalDateTime out = LocalDateTime.of(exitDate,  exitTime);
        if (out.isBefore(in)) {
            throw new IllegalArgumentException("退庫日時が入庫日時より前です");
        }

        // 2. 駐車場・料金パターン情報取得
        ParkingDetailForm form = selectById(id);  // 料金情報一式をDTOに詰める :contentReference[oaicite:4]{index=4}

        long totalMinutes = Duration.between(in, out).toMinutes();
        int totalFee = 0;

        // 3. 日単位でループ（跨がる場合に対応）
        LocalDateTime cursor = in;
        while (!cursor.isAfter(out)) {
            LocalDate today = cursor.toLocalDate();
            LocalDateTime dayEnd = today.atTime(LocalTime.MAX);
            LocalDateTime segmentEnd = out.isBefore(dayEnd) ? out : dayEnd;

            // 4. 当日の料金計算
            int dayFee = calcDailyFee(form, cursor, segmentEnd);

            // 5. 24時間／日付締めの最大料金適用
            if (form.getOptionRadio() != null) {
                if (form.getOptionRadio() == 0 && form.getMaxRate24h() != null) {
                    dayFee = Math.min(dayFee, form.getMaxRate24h());
                } else if (form.getOptionRadio() == 1 && form.getMaxRateDaily() != null) {
                    dayFee = Math.min(dayFee, form.getMaxRateDaily());
                }
            }
            totalFee += dayFee;

            // 翌日の00:00に移動
            cursor = segmentEnd.plusSeconds(1);
        }

        return Arrays.asList(totalFee, (int) totalMinutes);
	}
	 /** 指定区間内で、各時間帯レンジごとの料金を計算し合算 */
	private int calcDailyFee(ParkingDetailForm form, LocalDateTime in, LocalDateTime out) {
        int fee = 0;
        LocalDate date = in.toLocalDate();
        DayOfWeek dow = date.getDayOfWeek();

        for (RatesRangeDto dto : form.getDailyList()) {
            // 1) 曜日＋祝日チェック
            if (HolidayUtils.isHoliday(date)) {
                if (!dto.isHoliday()) {
                    continue;  // 祝日レンジでなければスキップ
                }
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

            // 2) 時間帯オーバーラップ判定
            LocalTime rStart = LocalTime.of(dto.getStartTime() / 100, dto.getStartTime() % 100);
            LocalTime rEnd   = LocalTime.of(dto.getEndTime()   / 100, dto.getEndTime()   % 100);
            LocalDateTime segStart = in.isAfter(date.atTime(rStart)) ? in : date.atTime(rStart);
            LocalDateTime segEnd   = out.isBefore(date.atTime(rEnd))  ? out : date.atTime(rEnd);

            if (!segStart.isBefore(segEnd)) {
                continue;  // 重ならなければスキップ
            }

            // 3) 単位時間チャージ
            int segmentFee = calcFee(segStart, segEnd, dto.getAmount(), dto.getTime());

            // 4) 範囲最大料金適用
            if (dto.getMaxRateTimely() != null) {
                segmentFee = Math.min(segmentFee, dto.getMaxRateTimely());
            }

            fee += segmentFee;
        }

        return fee;
    }
    /** 既存：in→out の時間を timePer 分単位で feePer 円ずつ課金 */
    private int calcFee(LocalDateTime in, LocalDateTime out, int feePer, int timePer) {
        long minutes = Duration.between(in, out).toMinutes();
        int units = (int) Math.ceil((double) minutes / timePer);
        return units * feePer;  // :contentReference[oaicite:7]{index=7}
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
