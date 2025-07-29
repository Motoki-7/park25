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
	
	
//	基本の設計：
//	①2重ループ文を作り、外側でRatesリストを1つずつ取り出し、内側でidがDBに存在するか確認する
//	②2重ループの外側でフラグ(boolean)を定義し、特定の動作を行ったかどうかブロックをまたいで処理する
	public void update(EditParkingForm form) {
	    // 1. 駐車場情報の更新
	    ParkinglotEntity parkinglotEnt = form.toParkinglotEntity();
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
}