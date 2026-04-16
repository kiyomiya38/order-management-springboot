■■■3_04_サンプルSQL■■■

#########【 準備 】##########################################################################################

#ペット情報管理テーブル
CREATE TABLE SAMPLE_4_1 (
  ID                INT          NOT NULL PRIMARY KEY                 COMMENT 'ペットID'             ,
  NAME              VARCHAR(30)  NOT NULL                             COMMENT '名前'                 ,
  GENDER            CHAR(1)      NOT NULL                             COMMENT '性別（男：M／女：F）' ,
  BIRTHDAY          DATE         NOT NULL                             COMMENT '生年月日'             ,
  WEIGHT            DECIMAL(4,1)                                      COMMENT '体重'                 ,
  REGIST_TIMESTAMP  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP   COMMENT '登録日時' 
);
INSERT INTO SAMPLE_4_1 (ID , NAME     , GENDER , BIRTHDAY     , WEIGHT )
                VALUES (1  , 'MOCO'   , 'F'    , '2014-05-04' , 3.5    ) ;
INSERT INTO SAMPLE_4_1 (ID , NAME     , GENDER , BIRTHDAY     , WEIGHT )
                VALUES (2  , 'CHOCO'  , 'M'    , '2011-08-25' , 5.2    ) ;
INSERT INTO SAMPLE_4_1 (ID , NAME     , GENDER , BIRTHDAY     , WEIGHT )
                VALUES (3  , 'TARO'   , 'M'    , '2013-01-02' , 7.9    ) ;
INSERT INTO SAMPLE_4_1 (ID , NAME     , GENDER , BIRTHDAY     , WEIGHT )
                VALUES (4  , 'RINRIN' , 'F'    , '2015-12-12' , 6.2    ) ;
INSERT INTO SAMPLE_4_1 (ID , NAME     , GENDER , BIRTHDAY     , WEIGHT )
                VALUES (5  , 'CHAMP'  , 'M'    , '2013-01-02' , 10.9   ) ;

#家庭情報管理テーブル
CREATE TABLE SAMPLE_4_2 (
  HOME_ID           INT          NOT NULL PRIMARY KEY                 COMMENT '家庭ID'               ,
  HOME_NAME         VARCHAR(30)  NOT NULL                             COMMENT '家庭の総称'           ,
  PET_ID            INT                                               COMMENT 'ペットID'             ,
  AREA_ID           INT                                               COMMENT '地域ID'
);
INSERT INTO SAMPLE_4_2 (HOME_ID , HOME_NAME      , PET_ID , AREA_ID )
                   VALUES (1       , 'OKAMOTO_KE'   , 1      , 4       ) ;
INSERT INTO SAMPLE_4_2 (HOME_ID , HOME_NAME      , PET_ID , AREA_ID )
                   VALUES (2       , 'TANAKA_KE'    , NULL   , NULL    ) ;
INSERT INTO SAMPLE_4_2 (HOME_ID , HOME_NAME      , PET_ID , AREA_ID )
                   VALUES (3       , 'SUZUKI_KE'    , 5      , 3       ) ;
INSERT INTO SAMPLE_4_2 (HOME_ID , HOME_NAME      , PET_ID , AREA_ID )
                   VALUES (4       , 'IKEDA_KE'     , NULL   , 4       ) ;
INSERT INTO SAMPLE_4_2 (HOME_ID , HOME_NAME      , PET_ID , AREA_ID )
                   VALUES (5       , 'TAKAHASHI_KE' , 2      , 1       ) ;
INSERT INTO SAMPLE_4_2 (HOME_ID , HOME_NAME      , PET_ID , AREA_ID )
                   VALUES (6       , 'NAGASAWA_KE'  , 3      , NULL    ) ;
INSERT INTO SAMPLE_4_2 (HOME_ID , HOME_NAME      , PET_ID , AREA_ID )
                   VALUES (7       , 'TAKIMIZU_KE'  , 4      , 2       ) ;

#地域情報管理テーブル
CREATE TABLE SAMPLE_4_3 (
  AREA_ID           INT          NOT NULL PRIMARY KEY                 COMMENT '地域ID'               ,
  AREA_NAME         VARCHAR(30)  NOT NULL                             COMMENT '地域名'
);
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (1       , 'HOKKAIDO'     ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (2       , 'TOHOKU'       ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (3       , 'KITA_KANTO'   ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (4       , 'MINAMI_KANTO' ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (5       , 'CHUBU'        ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (6       , 'KANSAI'       ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (7       , 'CHUGOKU'      ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (8       , 'SHIKOKU'      ) ;
INSERT INTO SAMPLE_4_3 (AREA_ID , AREA_NAME      )
                   VALUES (9       , 'KYUSYU'       ) ;

#試験結果管理テーブル
CREATE TABLE SAMPLE_4_4 (
  STUDENT_ID    INT         NOT NULL PRIMARY KEY COMMENT '学生ID'               ,
  STUDENT_NAME  VARCHAR(30) NOT NULL             COMMENT '学生名'               ,
  GENDER        CHAR(1)     NOT NULL             COMMENT '性別（男：M／女：F）' ,
  SCHOOL_NAME   VARCHAR(30)                      COMMENT '学校ID'               ,
  SCORE         INT         NOT NULL             COMMENT '点数'
);
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (1          , 'MOCO'      , 'F'     , 'UZUZ_COLLEGE' , 56    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (2          , 'CHOCO'     , 'M'     , 'MZMZ_COLLEGE' , 92    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (3          , 'TARO'      , 'M'     , 'UZUZ_COLLEGE' , 92    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (4          , 'RINRIN'    , 'F'     , NULL           , 93    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (5          , 'POCHI'     , 'M'     , 'UKUK_COLLEGE' , 56    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (6          , 'BEIBU'     , 'M'     , 'UZUZ_COLLEGE' , 52    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (7          , 'POPO'      , 'F'     , 'UKUK_COLLEGE' , 90    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (8          , 'BESU'      , 'M'     , 'MZMZ_COLLEGE' , 40    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (9          , 'OMOCHI'    , 'F'     , 'UZUZ_COLLEGE' , 56    ) ;
INSERT INTO SAMPLE_4_4 (STUDENT_ID , STUDENT_NAME, GENDER  , SCHOOL_NAME    , SCORE )
                VALUES (10         , 'SASUKE'    , 'M'     , 'MZMZ_COLLEGE' , 20    ) ;



#########【 3_04_1_SELECTの基本 】###########################################################################

### SELECT文の構造
SELECT ID , NAME ,GENDER , BIRTHDAY , WEIGHT
  FROM SAMPLE_4_1 ;

### 全カラム抽出「 * 」
SELECT *
  FROM SAMPLE_4_1 ;

### 別名の付与（AS）
SELECT ID       AS PET_ID       , 
       NAME     AS PET_NAME     ,
       GENDER   AS PET_GENDER   ,
       BIRTHDAY AS PET_BIRTHDAY ,
       WEIGHT   AS PET_WEIGHT
  FROM SAMPLE_4_1 ;



#########【 3_04_2_WHERE句 】###########################################################################

### WHERE句の構造 
SELECT *
  FROM SAMPLE_4_1
 WHERE GENDER = 'M'
   AND WEIGHT > 6.2 ;

### WHERE句で扱える演算子（ IN ） 
SELECT *
  FROM SAMPLE_4_1
 WHERE NAME IN ('MOCO','TARO','RINRIN') ;

### WHERE句で扱える演算子（ NOT ） 
SELECT *
  FROM SAMPLE_4_1
 WHERE NAME NOT IN ('MOCO','TARO','RINRIN') ;

### WHERE句で扱える演算子（ LIKE ） 
SELECT *
  FROM SAMPLE_4_1
 WHERE BIRTHDAY LIKE '2013%' ;

### WHERE句で扱える演算子（ BETWEEN ） 
SELECT *
  FROM SAMPLE_4_1
 WHERE WEIGHT BETWEEN 5.0 AND 8.0 ;



#########【 3_04_3_NULLの扱い 】###########################################################################

#以下、抽出失敗（NULLに比較演算子を使用したため判定不可として）
SELECT *
  FROM SAMPLE_4_2
 WHERE PET_ID = NULL ;

### NULLの比較（ IS NULL ） 
SELECT *
  FROM SAMPLE_4_2
 WHERE PET_ID IS NULL ;

### NULLの比較（ IS NOT NULL ） 
SELECT *
  FROM SAMPLE_4_2
 WHERE PET_ID IS NOT NULL ;



#########【 3_04_4_ORDER BY句 】###########################################################################

### ORDER BY句の構造 
SELECT *
  FROM SAMPLE_4_1
 WHERE GENDER = 'M'
 ORDER BY BIRTHDAY , WEIGHT DESC ;

### NULLデータの並び替え
SELECT *
  FROM SAMPLE_4_2
 ORDER BY PET_ID , HOME_ID ;



#########【 3_04_5_データの集計 】###########################################################################

### レコード数の集計（ COUNT関数 ）
SELECT COUNT(*) , COUNT(PET_ID)
  FROM SAMPLE_4_2 ;

### 数値の集計（SUM関数／AVG関数／MAX関数／MIN関数） 
SELECT SUM(WEIGHT) ,
       AVG(WEIGHT) ,
       MAX(WEIGHT) ,
       MIN(WEIGHT)
  FROM SAMPLE_4_1 ;

### 重複を除いたデータの抽出（DISTINCT） 
SELECT DISTINCT GENDER
  FROM SAMPLE_4_1 ;

### 重複を除いたレコード数の集計（ DISTINCT×COUNT ） 
SELECT COUNT( DISTINCT GENDER )
  FROM SAMPLE_4_1 ;



#########【 3_04_6_GROUP BY 】###########################################################################

### グループ化（GROUP BY句） 
SELECT SCHOOL_NAME AS SCHOOL_NAME   ,
       MAX(SCORE)  AS HIGH_SCORE    ,
       AVG(SCORE)  AS AVERAGE_SCORE
  FROM SAMPLE_4_4
 WHERE SCHOOL_NAME IS NOT NULL
 GROUP BY SCHOOL_NAME
 ORDER BY HIGH_SCORE DESC , AVERAGE_SCORE DESC , SCHOOL_NAME ;



#########【 3_04_7_HAVING 】###########################################################################

#以下、エラー発生（WHERE句に集約関数を記述）
SELECT SCHOOL_NAME AS SCHOOL_NAME   ,
       MAX(SCORE)  AS HIGH_SCORE    ,
       AVG(SCORE)  AS AVERAGE_SCORE
  FROM SAMPLE_4_4
 WHERE SCHOOL_NAME IS NOT NULL
   AND COUNT(*) >= 3
 GROUP BY SCHOOL_NAME
 ORDER BY HIGH_SCORE DESC , AVERAGE_SCORE DESC , SCHOOL_NAME ;

### グループへの抽出条件（HAVING）
SELECT SCHOOL_NAME AS SCHOOL_NAME   ,
       MAX(SCORE)  AS HIGH_SCORE    ,
       AVG(SCORE)  AS AVERAGE_SCORE
  FROM SAMPLE_4_4
 WHERE SCHOOL_NAME IS NOT NULL
 GROUP BY SCHOOL_NAME
HAVING COUNT(*) >= 3
 ORDER BY HIGH_SCORE DESC , AVERAGE_SCORE DESC , SCHOOL_NAME ;

### アンチパターン（HAVING句に集約キーへの絞り込みを記述）
SELECT SCHOOL_NAME AS SCHOOL_NAME   ,
       MAX(SCORE)  AS HIGH_SCORE    ,
       AVG(SCORE)  AS AVERAGE_SCORE
  FROM SAMPLE_4_4
 GROUP BY SCHOOL_NAME
HAVING COUNT(*) >= 3
   AND SCHOOL_NAME IS NOT NULL
 ORDER BY HIGH_SCORE DESC , AVERAGE_SCORE DESC ,SCHOOL_NAME ;



#########【 3_04_8_内部結合 】###########################################################################

### テーブル結合（内部結合）

#INNNER JOINを用いた内部結合
SELECT SAMPLE_4_2.HOME_ID , SAMPLE_4_2.HOME_NAME , SAMPLE_4_1.NAME
  FROM SAMPLE_4_1 INNER JOIN SAMPLE_4_2 ON SAMPLE_4_2.PET_ID = SAMPLE_4_1.ID
 ORDER BY SAMPLE_4_2.HOME_ID ;

#WHERE句を用いた内部結合
SELECT SAMPLE_4_2.HOME_ID , SAMPLE_4_2.HOME_NAME , SAMPLE_4_1.NAME
  FROM SAMPLE_4_1 ,
       SAMPLE_4_2
 WHERE SAMPLE_4_2.PET_ID  = SAMPLE_4_1.ID
 ORDER BY SAMPLE_4_2.HOME_ID ;

#INNNER JOINを用いた内部結合（エイリアス使用）
SELECT b.HOME_ID , b.HOME_NAME , a.NAME
  FROM SAMPLE_4_1 a INNER JOIN SAMPLE_4_2 b ON b.PET_ID = a.ID
 ORDER BY b.HOME_ID ;

#WHERE句を用いた内部結合（エイリアス使用）
SELECT b.HOME_ID , b.HOME_NAME , a.NAME
  FROM SAMPLE_4_1 a ,
       SAMPLE_4_2 b
 WHERE b.PET_ID  = a.ID
 ORDER BY b.HOME_ID ;



#########【 3_04_9_外部結合 】###########################################################################

### テーブル結合（外部結合）

#OUTER JOINを用いた外部結合
SELECT b.HOME_ID , b.HOME_NAME , a.NAME
  FROM SAMPLE_4_1 a RIGHT OUTER JOIN SAMPLE_4_2 b ON b.PET_ID = a.ID
 ORDER BY b.HOME_ID ;



#########【 3_04_10_３つ以上のテーブル結合 】###########################################################################

### テーブル結合（３つ以上のテーブルの内部結合） 

#INNNER JOINを用いた内部結合
SELECT b.HOME_ID , b.HOME_NAME , a.NAME , c.AREA_NAME
  FROM ( SAMPLE_4_1 a INNER JOIN SAMPLE_4_2 b ON b.PET_ID  = a.ID )
                      INNER JOIN SAMPLE_4_3 c ON b.AREA_ID = c.AREA_ID
 ORDER BY b.HOME_ID ;

#WHERE句を用いた内部結合
SELECT b.HOME_ID , b.HOME_NAME , a.NAME , c.AREA_NAME
  FROM SAMPLE_4_1 a ,
       SAMPLE_4_2 b ,
       SAMPLE_4_3 c
 WHERE b.PET_ID  = a.ID
   AND b.AREA_ID = c.AREA_ID
 ORDER BY b.HOME_ID ;

### テーブル結合（３つ以上のテーブルの外部結合） 

#OUTER JOINを用いた外部結合
SELECT b.HOME_ID , b.HOME_NAME , a.NAME , c.AREA_NAME
  FROM ( SAMPLE_4_1 a RIGHT OUTER JOIN SAMPLE_4_2 b ON b.PET_ID  = a.ID )
                      LEFT  OUTER JOIN SAMPLE_4_3 c ON b.AREA_ID = c.AREA_ID
 ORDER BY b.HOME_ID ;


