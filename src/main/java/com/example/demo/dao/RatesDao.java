package com.example.demo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ParkinglotEntity;

@Repository
public class RatesDao {
	@Autowired
	JdbcTemplate jdbcTemplate;

	
	//登録
		public void insert(RatesEntity ent) {
			String query = "INSERT INTO rates (amount) values (?,?,?,?,?,?,?)";
			jdbcTemplate.update(query,ent.getAddress1(),ent.getAddress2(),ent.getAddress3(),ent.getName(),ent.getCapacity(),ent.getHourlyRate(),ent.getUpdateDate());
			
			/* FK用KeyHolder
			String sql = "INSERT INTO range (startTime, endTime, monday, tuesday, ... holiday) VALUES (?, ?, ?, ?, ..., ?)";
		    
		    KeyHolder keyHolder = new GeneratedKeyHolder();

		    jdbcTemplate.update(connection -> {
		        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		        ps.setInt(1, range.getStartTime());
		        ps.setInt(2, range.getEndTime());
		        ps.setBoolean(3, range.isMonday());
		        // ...他の曜日のsetBoolean
		        return ps;
		    }, keyHolder);

		    return keyHolder.getKey().longValue();
		    */
			
		}
		
		//全件表示
		public List<ParkinglotEntity> selectAll(){
			
			List<ParkinglotEntity> resultParkinglotList = new ArrayList<ParkinglotEntity>();
			String query = "SELECT * FROM parkinglot";
			
			List<Map<String,Object>>searchResultList = jdbcTemplate.queryForList(query);
			
			for(Map<String,Object>resultMap : searchResultList) {
				ParkinglotEntity ent = new ParkinglotEntity();
				ent.setId((Integer)resultMap.get("id"));
				ent.setAddress1((String)resultMap.get("address1"));
				ent.setAddress2((String)resultMap.get("address2"));
				ent.setAddress3((String)resultMap.get("address3"));
				ent.setName((String)resultMap.get("name"));
				ent.setCapacity((Integer)resultMap.get("capacity"));
				ent.setHourlyRate((Integer)resultMap.get("hourlyRate"));
				java.sql.Date sqlDate = (java.sql.Date) resultMap.get("updateDate");
				ent.setUpdateDate(sqlDate.toLocalDate());
				resultParkinglotList.add(ent);
			}
			System.out.println(resultParkinglotList);
			return resultParkinglotList;
			
		}
		
		/*
		//条件検索
		public List<ParkinglotEntity>selectParkings(){
			
			
			
		}
		*/
		
		//address1で検索
		public List<ParkinglotEntity> selectByAddress1(String address1){
			List<ParkinglotEntity> resultList = new ArrayList<ParkinglotEntity>();
			String query = "SELECT * FROM parkinglot WHERE address1 = ?";
			List<Map<String,Object>> searchResultList = jdbcTemplate.queryForList(query,address1);
			System.out.println(searchResultList);
			//１件だけ取得
			for(Map<String,Object> resultMap : searchResultList) {
				ParkinglotEntity ent = new ParkinglotEntity();
				ent.setId((Integer)resultMap.get("id"));
				ent.setAddress1((String)resultMap.get("address1"));
				ent.setAddress2((String)resultMap.get("address2"));
				ent.setAddress3((String)resultMap.get("address3"));
				ent.setName((String)resultMap.get("name"));
				ent.setCapacity((Integer)resultMap.get("capacity"));
				ent.setHourlyRate((Integer)resultMap.get("hourlyRate"));
				java.sql.Date sqlDate = (java.sql.Date) resultMap.get("updateDate");
				ent.setUpdateDate(sqlDate.toLocalDate());
				
				resultList.add(ent);
			}
			System.out.println(resultList);
			return resultList;

		}
		
		//nameで検索
		public List<ParkinglotEntity> selectByName(String name){
			List<ParkinglotEntity> resultList = new ArrayList<ParkinglotEntity>();
			String query = "SELECT * FROM parkinglot WHERE name = ?";
			List<Map<String,Object>> searchResultList = jdbcTemplate.queryForList(query,name);
			System.out.println(searchResultList);
			//１件だけ取得
			for(Map<String,Object> resultMap : searchResultList) {
				ParkinglotEntity ent = new ParkinglotEntity();
				ent.setId((Integer)resultMap.get("id"));
				ent.setAddress1((String)resultMap.get("address1"));
				ent.setAddress2((String)resultMap.get("address2"));
				ent.setAddress3((String)resultMap.get("address3"));
				ent.setName((String)resultMap.get("name"));
				ent.setCapacity((Integer)resultMap.get("capacity"));
				ent.setHourlyRate((Integer)resultMap.get("hourlyRate"));
				java.sql.Date sqlDate = (java.sql.Date) resultMap.get("updateDate");
				ent.setUpdateDate(sqlDate.toLocalDate());
				
				resultList.add(ent);
			}
			System.out.println(resultList);
			return resultList;

		}
		
		//両方で検索
		public List<ParkinglotEntity> selectByNameAndAddress1(String name,String address1){
			List<ParkinglotEntity> resultList = new ArrayList<ParkinglotEntity>();
			String query = "SELECT * FROM parkinglot WHERE name = ? AND address1 = ?";
			List<Map<String,Object>> searchResultList = jdbcTemplate.queryForList(query,name,address1);
			System.out.println(searchResultList);
			//１件だけ取得
			for(Map<String,Object> resultMap : searchResultList) {
				ParkinglotEntity ent = new ParkinglotEntity();
				ent.setId((Integer)resultMap.get("id"));
				ent.setAddress1((String)resultMap.get("address1"));
				ent.setAddress2((String)resultMap.get("address2"));
				ent.setAddress3((String)resultMap.get("address3"));
				ent.setName((String)resultMap.get("name"));
				ent.setCapacity((Integer)resultMap.get("capacity"));
				ent.setHourlyRate((Integer)resultMap.get("hourlyRate"));
				java.sql.Date sqlDate = (java.sql.Date) resultMap.get("updateDate");
				ent.setUpdateDate(sqlDate.toLocalDate());
				
				resultList.add(ent);
			}
			System.out.println(resultList);
			return resultList;

		}
		
		//詳細表示
		public List<ParkinglotEntity>selectById(int id){
			
			List<ParkinglotEntity> resultList = new ArrayList<ParkinglotEntity>();
			String query = "SELECT * FROM parkinglot WHERE id = ?";
			
			List<Map<String,Object>> searchResultList = jdbcTemplate.queryForList(query,id);
			System.out.println(searchResultList);
			//１件だけ取得
			for(Map<String,Object> resultMap : searchResultList) {
				ParkinglotEntity ent = new ParkinglotEntity();
				ent.setId((Integer)resultMap.get("id"));
				ent.setAddress1((String)resultMap.get("address1"));
				ent.setAddress2((String)resultMap.get("address2"));
				ent.setAddress3((String)resultMap.get("address3"));
				ent.setName((String)resultMap.get("name"));
				ent.setCapacity((Integer)resultMap.get("capacity"));
				ent.setHourlyRate((Integer)resultMap.get("hourlyRate"));
				java.sql.Date sqlDate = (java.sql.Date) resultMap.get("updateDate");
				ent.setUpdateDate(sqlDate.toLocalDate());
				
				resultList.add(ent);
			}
			System.out.println(resultList);
			return resultList;
			
		}

		
		//更新
		public void update(ParkinglotEntity ent) {
			String query = "UPDATE parkinglot set address1=?,address2=?,address3=?,name=?,capacity=?,hourlyRate=?,updateDate=? WHERE id = ?";
			jdbcTemplate.update(query,ent.getAddress1(),ent.getAddress2(),ent.getAddress3(),ent.getName(),ent.getCapacity(),ent.getHourlyRate(),ent.getUpdateDate(),ent.getId());
			
		}
		
		//削除
		public void delete(int id) {
			String query = "DELETE FROM parkinglot WHERE id = ?";
			jdbcTemplate.update(query,id);
			
		}
}
