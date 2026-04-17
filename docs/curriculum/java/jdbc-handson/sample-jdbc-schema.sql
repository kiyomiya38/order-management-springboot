-- jdbc-handson 共通サンプル
-- 利用DB: test_db

DROP TABLE IF EXISTS point_account;
DROP TABLE IF EXISTS uzuz_member;

CREATE TABLE uzuz_member (
  member_id         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  member_name       VARCHAR(30)  NOT NULL,
  age               INT          NOT NULL,
  email             VARCHAR(100) NOT NULL,
  deleted_flg       CHAR(1)      NOT NULL DEFAULT '0',
  regist_timestamp  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO uzuz_member (member_name, age, email) VALUES
('Tanaka', 24, 'tanaka@example.com'),
('Suzuki', 29, 'suzuki@example.com'),
('Sato',   22, 'sato@example.com');

CREATE TABLE point_account (
  account_id        INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  member_id         INT          NOT NULL,
  point_balance     INT          NOT NULL DEFAULT 0,
  update_timestamp  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO point_account (member_id, point_balance) VALUES
(1, 100),
(2, 150),
(3, 80);
