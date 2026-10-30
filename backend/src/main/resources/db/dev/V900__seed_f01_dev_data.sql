INSERT INTO toa_nha (id, code, name, address)
VALUES ('10000000-0000-0000-0000-000000000001', 'AN01', 'TTTM ANEON', 'Hà Nội');

INSERT INTO tang (id, building_id, code, name, level)
VALUES ('20000000-0000-0000-0000-000000000001',
        '10000000-0000-0000-0000-000000000001', 'F01', 'Tầng 1', 1);

INSERT INTO khu_vuc (id, floor_id, code, name, description)
VALUES ('30000000-0000-0000-0000-000000000001',
        '20000000-0000-0000-0000-000000000001', 'ZONE-A', 'Khu A', 'Khu vực thử nghiệm Postman');

INSERT INTO vi_tri (id, zone_id, x, y, text_description)
VALUES ('40000000-0000-0000-0000-000000000001',
        '30000000-0000-0000-0000-000000000001', 100.0000, 200.0000, 'Vị trí mẫu khu A');

INSERT INTO nganh_hang (id, code, name, description)
VALUES ('50000000-0000-0000-0000-000000000001', 'FASHION', 'Thời trang', 'Ngành hàng mẫu');
