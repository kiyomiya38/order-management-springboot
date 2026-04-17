-- mysql-handson 共通の初期データ

DROP TABLE IF EXISTS sample_4_4;
DROP TABLE IF EXISTS sample_4_3;
DROP TABLE IF EXISTS sample_4_2;
DROP TABLE IF EXISTS sample_4_1;

CREATE TABLE sample_4_1 (
  id                INT          NOT NULL PRIMARY KEY                 COMMENT 'ペットID',
  name              VARCHAR(30)  NOT NULL                             COMMENT '名前',
  gender            CHAR(1)      NOT NULL                             COMMENT '性別（男:M 女:F）',
  birthday          DATE         NOT NULL                             COMMENT '生年月日',
  weight            DECIMAL(4,1)                                      COMMENT '体重',
  regist_timestamp  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP   COMMENT '登録日時'
);

INSERT INTO sample_4_1 (id, name, gender, birthday, weight) VALUES
(1, 'MOCO',   'F', '2014-05-04', 3.5),
(2, 'CHOCO',  'M', '2011-08-25', 5.2),
(3, 'TARO',   'M', '2013-01-02', 7.9),
(4, 'RINRIN', 'F', '2015-12-12', 6.2),
(5, 'CHAMP',  'M', '2013-01-02', 10.9);

CREATE TABLE sample_4_2 (
  home_id           INT          NOT NULL PRIMARY KEY                 COMMENT '家庭ID',
  home_name         VARCHAR(30)  NOT NULL                             COMMENT '家庭名',
  pet_id            INT                                               COMMENT 'ペットID',
  area_id           INT                                               COMMENT '地域ID'
);

INSERT INTO sample_4_2 (home_id, home_name, pet_id, area_id) VALUES
(1, 'OKAMOTO_KE',   1,    4),
(2, 'TANAKA_KE',    NULL, NULL),
(3, 'SUZUKI_KE',    5,    3),
(4, 'IKEDA_KE',     NULL, 4),
(5, 'TAKAHASHI_KE', 2,    1),
(6, 'NAGASAWA_KE',  3,    NULL),
(7, 'TAKIMIZU_KE',  4,    2);

CREATE TABLE sample_4_3 (
  area_id           INT          NOT NULL PRIMARY KEY                 COMMENT '地域ID',
  area_name         VARCHAR(30)  NOT NULL                             COMMENT '地域名'
);

INSERT INTO sample_4_3 (area_id, area_name) VALUES
(1, 'HOKKAIDO'),
(2, 'TOHOKU'),
(3, 'KITA_KANTO'),
(4, 'MINAMI_KANTO'),
(5, 'CHUBU'),
(6, 'KANSAI'),
(7, 'CHUGOKU'),
(8, 'SHIKOKU'),
(9, 'KYUSYU');

CREATE TABLE sample_4_4 (
  student_id        INT          NOT NULL PRIMARY KEY                 COMMENT '学生ID',
  student_name      VARCHAR(30)  NOT NULL                             COMMENT '学生名',
  gender            CHAR(1)      NOT NULL                             COMMENT '性別（男:M 女:F）',
  school_name       VARCHAR(30)                                      COMMENT '学校名',
  score             INT          NOT NULL                             COMMENT '点数'
);

INSERT INTO sample_4_4 (student_id, student_name, gender, school_name, score) VALUES
(1,  'MOCO',   'F', 'UZUZ_COLLEGE', 56),
(2,  'CHOCO',  'M', 'MZMZ_COLLEGE', 92),
(3,  'TARO',   'M', 'UZUZ_COLLEGE', 92),
(4,  'RINRIN', 'F', NULL,           93),
(5,  'POCHI',  'M', 'UKUK_COLLEGE', 56),
(6,  'BEIBU',  'M', 'UZUZ_COLLEGE', 52),
(7,  'POPO',   'F', 'UKUK_COLLEGE', 90),
(8,  'BESU',   'M', 'MZMZ_COLLEGE', 40),
(9,  'OMOCHI', 'F', 'UZUZ_COLLEGE', 56),
(10, 'SASUKE', 'M', 'MZMZ_COLLEGE', 20);
