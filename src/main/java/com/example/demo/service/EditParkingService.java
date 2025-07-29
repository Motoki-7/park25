package com.example.demo.service;

import java.time.Duration;
import java.time.LocalTime;
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
	
	
	/**
     * 1時間あたりの料金を計算します。
     * 要件:
     *  1. 単位料金(amount)と単位時間(time)から1時間当たりの料金を計算
     *  2. 終日固定の場合：そのまま1の方法
     *  3. 基本料金の場合：1に加え各料金テーブルを時間幅ごとの重み付け平均
     *  4. 最大料金オプションがある場合はそちらを優先
     *
     * @param form RegistParkingForm (ParkingDetailForm と同等の getter を持つ)
     * @return hourlyRate 1時間当たりの料金（円）
     */
    public int calculate(EditParkingForm form) {
        // まず「最大料金オプション」があれば、それに従って24時間で割った値を返す
        Integer max24 = form.getMaxRate24h();
        Integer maxDay = form.getMaxRateDaily();
        if (Boolean.TRUE.equals(form.getOptionRadio() == 0) && max24 != null) {
            return max24 / 24;
        }
        if (Boolean.TRUE.equals(form.getOptionRadio() == 1) && maxDay != null) {
            return maxDay / 24;
        }

        // 次に baseFeeRadio で終日固定 or 基本料金を振り分け
        if (form.getBaseFeeRadio() == 0) {
            // 終日固定
            return calculationFixedFee(form);
        } else {
            // 基本料金
            return calculationBasicFee(form);
        }
    }

    /**
     * 終日固定パターンの「1時間あたり料金」を計算
     * ＝ amountDaily / timeDailly * 60 分
     */
    private int calculationFixedFee(EditParkingForm form) {
        double ratePerHour =
            form.getAmountDaily() * 60.0
            / form.getTimeDailly();
        return (int) Math.floor(ratePerHour);
    }

    /**
     * 基本料金パターンの「1時間あたり料金」を計算
     * 各RatesRangeDtoのhourlyRateを時間幅で重み付けて平均
     */
    private int calculationBasicFee(EditParkingForm form) {
        List<RatesRangeDto> list = form.getDailyList();
        if (list == null || list.isEmpty()) {
            return 0;
        }

        double weightedSum = 0;
        double totalHours  = 0;
        for (RatesRangeDto dto : list) {
            // 1時間あたり料金
            double hrRate = dto.getAmount() * 60.0 / dto.getTime();
            // このレンジの継続時間（時間単位、小数可）
            double hrs = computeRangeHours(dto);
            weightedSum += hrRate * hrs;
            totalHours  += hrs;
        }
        if (totalHours <= 0) {
            return 0;
        }
        return (int) Math.floor(weightedSum / totalHours);
    }

    /**
     * RatesRangeDto の startTime/endTime から継続時間を「時間単位」で返却
     * - s==e → 24時間
     * - s<e  → 同日 (e-s)
     * - s>e  → 深夜越え (start→24h＋0→end)
     */
    private double computeRangeHours(RatesRangeDto dto) {
        int s = dto.getStartTime();
        int e = dto.getEndTime();
        LocalTime start = LocalTime.of(s / 100, s % 100);
        LocalTime end   = LocalTime.of(e / 100, e % 100);

        Duration duration;
        if (s == e) {
            // 終日
            duration = Duration.ofHours(24);
        } else if (s < e) {
            // 同日
            duration = Duration.between(start, end);
        } else {
            // 深夜越え
            Duration toMidnight = Duration.between(start, LocalTime.MAX).plusSeconds(1);
            Duration fromMid   = Duration.between(LocalTime.MIN, end);
            duration = toMidnight.plus(fromMid);
        }
        return duration.toMinutes() / 60.0;
    }
}
/*
アイデア
・max24hがあればそれを24で割った値にする
・maxDailyも同様
・optionRadioがnullかつ終日なら単位時間(hour)あたりの料金を算出。
・上同かつ基本ありなら、各条件での1日あたりの料金算出、最大あれば適用して24で除算
*/