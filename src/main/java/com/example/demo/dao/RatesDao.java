package com.example.demo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RatesEntity;

@Repository
public class RatesDao {
	@Autowired
	JdbcTemplate jdbcTemplate;

	
	//登録
	public void insert(RatesEntity ent, int parkinglotId, int rangeId) {
		String query = 
				"INSERT INTO rates (parkinglotId,rangeId,amount,time,maxRateTimely,maxRate24h,maxRateDaily) values (?,?,?,?,?,?,?)";
		jdbcTemplate.update(query, 
				parkinglotId, rangeId, ent.getAmount(), ent.getTime(), ent.getMaxRateTimely(), ent.getMaxRate24h(), ent.getMaxRateDaily());
	}

	// 検索：parkinglotIdをキーにRatesEntityを全件return
	public List<RatesEntity> selectByParkinglotId(int parkinglotId) {
		List<RatesEntity> resultList = new ArrayList<>();
		String query = "SELECT * FROM rates WHERE parkinglotId = ?";
		List<Map<String, Object>> searchResultList = jdbcTemplate.queryForList(query, parkinglotId);
		
		for (Map<String, Object> resultMap : searchResultList) {
			RatesEntity ent = new RatesEntity();
			ent.setRatesId((Integer) resultMap.get("ratesId"));
			ent.setParkinglotId((Integer) resultMap.get("parkinglotId"));
			ent.setRangeId((Integer) resultMap.get("rangeId"));
			ent.setAmount((Integer) resultMap.get("amount"));
			ent.setTime((Integer) resultMap.get("time"));
			ent.setMaxRateTimely((Integer) resultMap.get("maxRateTimely"));
			ent.setMaxRate24h((Integer) resultMap.get("maxRate24h"));
			ent.setMaxRateDaily((Integer) resultMap.get("maxRateDaily"));

			resultList.add(ent);
		}
		return resultList;
	}
	
	// 検索：parkinglotIdをキーにrengeIdをreturn
	public List<RatesEntity> selectIdListByParkinglotId (int parkinglotId) {
		List<RatesEntity> resultList = new ArrayList<>();
		String query = "SELECT ratesId, rangeId FROM rates WHERE parkinglotId = ?";	// 2つのIdのみ取得
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, parkinglotId);
		
		for (Map<String, Object> row : rows) {
			RatesEntity ent = new RatesEntity();
			ent.setRatesId((Integer) row.get("ratesId"));
			ent.setRangeId((Integer) row.get("rangeId"));
			ent.setParkinglotId(parkinglotId);
			resultList.add(ent);
		}
		
		return resultList;
	}

	// 更新：rates を更新
	public void update(RatesEntity ent) {
		String query = "UPDATE rates SET amount=?, time=?, maxRateTimely=?, maxRate24h=?, maxRateDaily=? WHERE ratesId = ?";
		jdbcTemplate.update(query, 
			ent.getAmount(), ent.getTime(), ent.getMaxRateTimely(), ent.getMaxRate24h(), ent.getMaxRateDaily(), ent.getRatesId());
	}

	// 削除：rates を削除
	public void delete(int id) {
		String query = "DELETE FROM rates WHERE ratesId = ?";
		jdbcTemplate.update(query, id);
	}
}
