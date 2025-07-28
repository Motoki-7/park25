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
import com.example.demo.form.EditParkingForm;

@Service
public class EditParkingService {
	@Autowired ParkinglotDao parkinglotDao;
	@Autowired RatesDao ratesDao;
	@Autowired RangeDao rangeDao;
	
	
	/*public void update(EditParkingForm form) {
		ParkinglotEntity parkinglotEnt = form.toParkinglotEntity();
		parkinglotDao.update(parkinglotEnt);
		int parkinglotId = parkinglotEnt.getId();
		
//		Ratesからidリストを取得
//		→　dailyList と idリスト のid比較
//		→　すでにあればRatesからupdate、なければRangeからinsertを実行
//		同時に、baseFeeRadioを比較し、 基本料金から終日固定になった場合はrangeの削除が必要
		
		// idList 取得
		List<RatesEntity> idList = ratesDao.selectIdListByParkinglotId(parkinglotId);
		// DB処理：idが存在するならupdate, しないならinsertを実行
		List<RatesEntity> ratesEntList = form.toRatesEntityList();
		outer : 
		for (RatesEntity ent : ratesEntList) {
			for (RatesEntity idEnt : idList) {
				if (ent.getRatesId() == idEnt.getRatesId()) {
					Integer rangeId;
					if (form.getBaseFeeRadio() == 0) {
						rangeId = null;
						ratesDao.insert(ent, parkinglotId, rangeId);
						break outer;	// 外のループも抜け出す
					}
					else if (form.getBaseFeeRadio() == 1) {
						List<RangeEntity> rangeEntList = form.toRangeEntityList();
						for (int i=0; i<form.getDailyList().size(); i++) {
							RatesEntity ratesEnt = ratesEntList.get(i);
							RangeEntity rangeEnt = rangeEntList.get(i);
							rangeId = rangeDao.insert(rangeEnt);
							ratesDao.insert(ratesEnt, parkinglotId, rangeId);
						}
					}
					continue outer;
				}
			}
		}
	}*/
	
	public void update(EditParkingForm form) {
	    // 1. 駐車場情報の更新
	    ParkinglotEntity parkinglotEnt = form.toParkinglotEntity();
	    parkinglotDao.update(parkinglotEnt);
	    int parkinglotId = parkinglotEnt.getId();

	    // 2. フォームから新しい料金情報と時間帯情報を取得
	    List<RatesEntity> newRatesList = form.toRatesEntityList();
	    List<RangeEntity> newRangeList = form.toRangeEntityList();

	    // 3. 既存の料金情報を取得（駐車場IDで）
	    List<RatesEntity> existingRatesList = ratesDao.selectIdListByParkinglotId(parkinglotId);

	    // 4. 新しいフォームデータに対する処理
	    for (int i = 0; i < newRatesList.size(); i++) {
	        RatesEntity newRates = newRatesList.get(i);

	        boolean isUpdate = false;
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
	                    RangeEntity updatedRange = newRangeList.get(i);
	                    updatedRange.setRangeId(existing.getRangeId());
	                    rangeDao.update(updatedRange);
	                    newRates.setRangeId(existing.getRangeId());
	                    ratesDao.update(newRates);
	                }

	                break;
	            }
	        }

	        if (!isUpdate) {
	            // 既存IDに存在しない → 新規insert
	            if (form.getBaseFeeRadio() == 0) {
	                newRates.setRangeId(null);
	                ratesDao.insert(newRates, parkinglotId, null);
	            } else {
	                RangeEntity newRange = newRangeList.get(i);
	                int newRangeId = rangeDao.insert(newRange);
	                newRates.setRangeId(newRangeId);
	                ratesDao.insert(newRates, parkinglotId, newRangeId);
	            }
	        }
	    }

	    // 5. 削除処理：フォームに存在しないratesは削除する
	    for (RatesEntity existing : existingRatesList) {
	        boolean stillExists = false;
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
}