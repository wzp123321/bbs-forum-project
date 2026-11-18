ALTER TABLE user_info
  ADD COLUMN nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称' AFTER user_name,
  ADD COLUMN avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL' AFTER nickname,
  ADD COLUMN signature VARCHAR(100) DEFAULT NULL COMMENT '个性签名' AFTER avatar;

UPDATE user_info SET nickname = user_name WHERE nickname IS NULL OR nickname = '';
