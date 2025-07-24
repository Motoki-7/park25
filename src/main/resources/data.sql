--本番環境用--------------------------
--INSERT IGNORE INTO parkinglot(id,address1,address2,address3,name,capacity,hourlyRate,updateDate) 
--VALUES (1,'東京都','品川区西五反田','1丁目','タイムズパーク24グループ本社ビル',50,1320,'2025-07-20');


--ローカルテスト用--------------------------
INSERT INTO parkinglot(address1,address2,address3,name,capacity,hourlyRate,updateDate) 
VALUES ('東京都','品川区西五反田','1丁目','タイムズパーク24グループ本社ビル',50,1320,'2025-07-20');

INSERT INTO rates(parkinglotId,rangeId,amount,time,maxRateTimely,maxRate24h,maxRateDaily) 
VALUES (1, 1, 200, 15, 2000, 3000, 5000);

INSERT INTO range(startTime,endTime,monday,tuesday,wednesday,thursday,friday,saturday,sunday,holiday) 
VALUES (0900, 2200, true, true, true, true, true, true, true, true);