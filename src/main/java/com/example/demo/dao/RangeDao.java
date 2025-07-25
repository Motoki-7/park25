package com.example.demo.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RangeEntity;

@Repository
public class RangeDao {
	@Autowired
	JdbcTemplate jdbcTemplate;

	//登録
	public int insert(RangeEntity ent) {
		String query = "INSERT INTO `range` (startTime,endTime,monday,tuesday,wednesday,thursday,friday,saturday,sunday,holiday) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, ent.getStartTime());
			ps.setInt(2, ent.getEndTime());
			ps.setBoolean(3, ent.isMonday());
			ps.setBoolean(4, ent.isTuesday());
			ps.setBoolean(5, ent.isWednesday());
			ps.setBoolean(6, ent.isThursday());
			ps.setBoolean(7, ent.isFriday());
			ps.setBoolean(8, ent.isSaturday());
			ps.setBoolean(9, ent.isSunday());
			ps.setBoolean(10, ent.isHoliday());
			return ps;
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	//検索
	public List<RangeEntity> selectById(int id) {

		List<RangeEntity> resultList = new ArrayList<RangeEntity>();
		String query = "SELECT * FROM `range` WHERE id = ?";

		List<Map<String, Object>> searchResultList = jdbcTemplate.queryForList(query, id);
		System.out.println(searchResultList);
		//１件だけ取得
		for (Map<String, Object> resultMap : searchResultList) {
			RangeEntity ent = new RangeEntity();
			ent.setRangeId((Integer) resultMap.get("RangeId"));
			ent.setStartTime((Integer) resultMap.get("startTime"));
			ent.setEndTime((Integer) resultMap.get("endTime"));
			ent.setMonday((Boolean) resultMap.get("monday"));
			ent.setTuesday((Boolean) resultMap.get("tuesday"));
			ent.setWednesday((Boolean) resultMap.get("wednesday"));
			ent.setThursday((Boolean) resultMap.get("thursday"));
			ent.setFriday((Boolean) resultMap.get("friday"));
			ent.setSaturday((Boolean) resultMap.get("saturday"));
			ent.setSunday((Boolean) resultMap.get("sunday"));
			ent.setHoliday((Boolean) resultMap.get("holiday"));
			resultList.add(ent);
		}
		System.out.println(resultList);
		return resultList;

	}

	//更新
	public void update(RangeEntity ent) {
		String query = "UPDATE `range` SET startTime = ?, endTime = ?, monday = ?, tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ?, sunday = ?, holiday = ? WHERE rangeId = ?";
		jdbcTemplate.update(query,
				ent.getStartTime(),
				ent.getEndTime(),
				ent.isMonday(),
				ent.isTuesday(),
				ent.isWednesday(),
				ent.isThursday(),
				ent.isFriday(),
				ent.isSaturday(),
				ent.isSunday(),
				ent.isHoliday(),
				ent.getRangeId());

	}

	//削除
	public void delete(int id) {
		String query = "DELETE FROM `range` WHERE id = ?";
		jdbcTemplate.update(query, id);

	}
}
