-- webapp-handson 共通サンプル
-- 利用DB: test_db

DROP TABLE IF EXISTS survey_response;
DROP TABLE IF EXISTS user_info;

CREATE TABLE survey_response (
  survey_id            INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_name            VARCHAR(30)   NOT NULL,
  satisfaction_level   INT           NOT NULL,
  comment_text         VARCHAR(255),
  created_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO survey_response (user_name, satisfaction_level, comment_text) VALUES
('Tanaka', 5, '使いやすいです'),
('Suzuki', 3, '普通です'),
('Sato',   4, '画面遷移が分かりやすいです');

CREATE TABLE user_info (
  user_id              INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
  login_id             VARCHAR(30)   NOT NULL UNIQUE,
  login_password       VARCHAR(100)  NOT NULL,
  user_name            VARCHAR(30)   NOT NULL,
  deleted_flg          CHAR(1)       NOT NULL DEFAULT '0',
  created_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO user_info (login_id, login_password, user_name, deleted_flg) VALUES
('moco',  'pass123', 'MOCO',  '0'),
('taro',  'pass123', 'TARO',  '0'),
('hanako','pass123', 'HANAKO','0');
