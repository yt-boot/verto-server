-- 为 oauth_binding 表添加外键到 oauth_user 表
-- 注意：若外键已存在，执行会报错，请忽略或跳过。

SET NAMES utf8mb4;
START TRANSACTION;

ALTER TABLE `oauth_binding`
  ADD CONSTRAINT `fk_binding_oauth_user`
  FOREIGN KEY (`platform`, `oauth_user_id`)
  REFERENCES `oauth_user` (`platform`, `oauth_user_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

COMMIT;