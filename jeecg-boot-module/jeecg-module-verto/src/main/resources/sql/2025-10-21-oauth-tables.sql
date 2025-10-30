-- OAuth tables for storing third-party bindings
CREATE TABLE IF NOT EXISTS oauth_user (
  id            VARCHAR(64) PRIMARY KEY,
  platform      VARCHAR(32) NOT NULL COMMENT '平台：github等',
  oauth_user_id VARCHAR(64) NOT NULL COMMENT '第三方平台用户唯一标识',
  login         VARCHAR(128) NULL,
  name          VARCHAR(128) NULL,
  avatar_url    VARCHAR(512) NULL,
  email         VARCHAR(256) NULL,
  bound_at      DATETIME NULL,
  created_at    DATETIME NULL,
  updated_at    DATETIME NULL,
  last_token_id VARCHAR(64) NULL,
  UNIQUE KEY uk_platform_user (platform, oauth_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS oauth_token (
  id            VARCHAR(64) PRIMARY KEY,
  platform      VARCHAR(32) NOT NULL,
  oauth_user_id VARCHAR(64) NOT NULL,
  access_token  VARCHAR(1024) NOT NULL,
  token_type    VARCHAR(32) NULL,
  scope         VARCHAR(512) NULL,
  expires_at    DATETIME NULL,
  created_at    DATETIME NULL,
  INDEX idx_user_platform (platform, oauth_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;