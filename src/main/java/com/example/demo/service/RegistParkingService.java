package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.dao.RangeDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.entity.RangeEntity;
import com.example.demo.entity.RatesEntity;
import com.example.demo.form.RatesRangeDto;
import com.example.demo.form.RegistParkingForm;

@Service
public class RegistParkingService {
	@Autowired
	ParkinglotDao parkinglotDao;
	@Autowired
	RatesDao ratesDao;
	@Autowired
	RangeDao rangeDao;

	public void insert(RegistParkingForm form) {
		// 1. 駐車場情報の更新
		ParkinglotEntity parkinglotEnt = form.toParkinglotEntity();
		//  1時間あたり料金の計算・Form反映
		int hourlyRate = calculate(form);
		parkinglotEnt.setHourlyRate(hourlyRate);
		// paringlot DAO登録処理
		int parkinglotId = parkinglotDao.insert(parkinglotEnt);

		// 2. フォームから料金情報取得
		List<RatesEntity> ratesEntList = form.toRatesEntityList();

		// 3. 料金計算
		//  終日固定ならrangeはnullに
		Integer rangeId;
		if (form.getBaseFeeRadio() == 0) {
			rangeId = null;
			RatesEntity ratesEnt = ratesEntList.get(0);
			ratesDao.insert(ratesEnt, parkinglotId, rangeId);
		} else if (form.getBaseFeeRadio() == 1) {
			List<RangeEntity> rangeEntList = form.toRangeEntityList();
			for (int i = 0; i < form.getDailyList().size(); i++) {
				RatesEntity ratesEnt = ratesEntList.get(i);
				RangeEntity rangeEnt = rangeEntList.get(i);
				rangeId = rangeDao.insert(rangeEnt);
				ratesDao.insert(ratesEnt, parkinglotId, rangeId);
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
    public int calculate(RegistParkingForm form) {
        // まず「最大料金オプション」があれば、それに従って24時間で割った値を返す
        Integer max24 = form.getMaxRate24h();
        Integer maxDay = form.getMaxRateDaily();
        if (Boolean.TRUE.equals(form.getOptionRadio() == null)) {}
        else if (Boolean.TRUE.equals(form.getOptionRadio() == 0) && max24 != null) {
            return max24 / 24;
        }
        else if (Boolean.TRUE.equals(form.getOptionRadio() == 1) && maxDay != null) {
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
    private int calculationFixedFee(RegistParkingForm form) {
        double ratePerHour =
            form.getAmountDaily() * 60.0
            / form.getTimeDailly();
        return (int) Math.floor(ratePerHour);
    }

    /**
     * 基本料金パターンの「1時間あたり料金」を計算
     * 各RatesRangeDtoのhourlyRateを時間幅で重み付けて平均
     */
    private int calculationBasicFee(RegistParkingForm form) {
        List<RatesRangeDto> list = form.getDailyList();
        if (list == null || list.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (RatesRangeDto dto : list) {
            // 各パターンの1時間あたり料金 = amount / time * 60
            double hrRate = dto.getAmount() * 60.0 / dto.getTime();
            sum += hrRate;
        }
        // 単純平均し、小数点以下切り捨て
        return (int) Math.floor(sum / list.size());
    }

    /**
     * RatesRangeDto の startTime/endTime から継続時間を「時間単位」で返却
     * - s==e → 24時間
     * - s<e  → 同日 (e-s)
     * - s>e  → 深夜越え (start→24h＋0→end)
     */
//    private double computeRangeHours(RatesRangeDto dto) {
//        int s = dto.getStartTime();
//        int e = dto.getEndTime();
//        LocalTime start = LocalTime.of(s / 100, s % 100);
//        LocalTime end   = LocalTime.of(e / 100, e % 100);
//
//        Duration duration;
//        if (s == e) {
//            // 終日
//            duration = Duration.ofHours(24);
//        } else if (s < e) {
//            // 同日
//            duration = Duration.between(start, end);
//        } else {
//            // 深夜越え
//            Duration toMidnight = Duration.between(start, LocalTime.MAX).plusSeconds(1);
//            Duration fromMid   = Duration.between(LocalTime.MIN, end);
//            duration = toMidnight.plus(fromMid);
//        }
//        return duration.toMinutes() / 60.0;
//    }
}