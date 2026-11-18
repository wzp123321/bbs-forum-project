CREATE TABLE IF NOT EXISTS bbs_notification (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     VARCHAR(32)  NOT NULL                COMMENT '接收者 user_id',
    actor_id    VARCHAR(32)  DEFAULT NULL            COMMENT '触发者 user_id, 系统公告为空',
    type        TINYINT      NOT NULL                COMMENT '1赞帖子 2评论帖子 3回复评论 4关注 5系统',
    post_id     BIGINT       NOT NULL DEFAULT 0      COMMENT '关联帖子ID, 无则 0',
    comment_id  BIGINT       NOT NULL DEFAULT 0      COMMENT '关联评论ID, 无则 0',
    content     VARCHAR(200) DEFAULT NULL            COMMENT '摘要文本',
    is_read     TINYINT      NOT NULL DEFAULT 0      COMMENT '0未读 1已读',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_notify (user_id, actor_id, type, post_id, comment_id),
    KEY idx_notify_user (user_id, is_read, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='互动通知';
