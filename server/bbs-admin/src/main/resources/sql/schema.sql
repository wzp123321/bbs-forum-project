-- =============================================================
--  9527 论坛  数据库建表脚本
--  适用版本: MySQL 5.7+ / 8.0
--  字符集:   utf8mb4
--  使用:     mysql -uroot -p bbs < schema.sql
--  说明:     本脚本幂等,使用 CREATE TABLE IF NOT EXISTS, 可重复执行
-- =============================================================

USE bbs;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------------------------------
-- 0. 用户表 (基础表,其他表外键依赖 user_id)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_info` (
    `user_id`     VARCHAR(32)  NOT NULL                COMMENT '用户唯一标识(UUID32位)',
    `user_name`   VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL                COMMENT 'BCrypt 密码',
    `gender`      VARCHAR(4)   DEFAULT '0'             COMMENT '0未知 1男 2女',
    `email`       VARCHAR(100) DEFAULT NULL,
    `phone`       VARCHAR(20)  DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_user_name` (`user_name`),
    KEY `idx_user_email` (`email`),
    KEY `idx_user_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- -------------------------------------------------------------
-- 1. 板块 / 分类表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_category`;
CREATE TABLE `bbs_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50)  NOT NULL                COMMENT '板块名称',
    `description` VARCHAR(200) DEFAULT NULL            COMMENT '板块描述',
    `sort`        INT          NOT NULL DEFAULT 0      COMMENT '排序,升序',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态: 1启用 0停用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`   VARCHAR(32)  DEFAULT NULL,
    `update_by`   VARCHAR(32)  DEFAULT NULL,
    `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除: 0未删 1已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_name` (`name`, `deleted`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='板块/分类表';

-- -------------------------------------------------------------
-- 2. 标签表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_tag`;
CREATE TABLE `bbs_tag` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50)  NOT NULL                COMMENT '标签名',
    `description` VARCHAR(200) DEFAULT NULL            COMMENT '标签描述',
    `post_count`  INT          NOT NULL DEFAULT 0      COMMENT '引用次数',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '1启用 0停用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`   VARCHAR(32)  DEFAULT NULL,
    `update_by`   VARCHAR(32)  DEFAULT NULL,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`name`, `deleted`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='标签表';

-- -------------------------------------------------------------
-- 3. 帖子表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_post`;
CREATE TABLE `bbs_post` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`        VARCHAR(32)   NOT NULL               COMMENT '发帖人ID',
    `category_id`    BIGINT        DEFAULT NULL           COMMENT '板块ID',
    `title`          VARCHAR(200)  NOT NULL               COMMENT '帖子标题',
    `content`        LONGTEXT      NOT NULL               COMMENT '帖子正文',
    `content_type`   TINYINT       NOT NULL DEFAULT 1     COMMENT '正文类型: 1富文本 2markdown',
    `view_count`     INT           NOT NULL DEFAULT 0,
    `like_count`     INT           NOT NULL DEFAULT 0,
    `comment_count`  INT           NOT NULL DEFAULT 0,
    `collect_count`  INT           NOT NULL DEFAULT 0,
    `status`         TINYINT       NOT NULL DEFAULT 1     COMMENT '1正常 0已删 2审核中 3审核不通过',
    `is_top`         TINYINT      NOT NULL DEFAULT 0     COMMENT '是否置顶: 1是 0否',
    `is_essence`     TINYINT      NOT NULL DEFAULT 0     COMMENT '是否精华: 1是 0否',
    `read_perm`      TINYINT      NOT NULL DEFAULT 1     COMMENT '阅读权限: 1公开 2登录可见 3粉丝可见 4仅作者',
    `top_time`       DATETIME      DEFAULT NULL           COMMENT '置顶时间,用于排序',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`      VARCHAR(32)   DEFAULT NULL,
    `update_by`      VARCHAR(32)   DEFAULT NULL,
    `deleted`        TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_post_user`     (`user_id`),
    KEY `idx_post_category` (`category_id`),
    KEY `idx_post_status`   (`status`, `is_top`, `top_time`),
    KEY `idx_post_feed`     (`status`, `create_time`)     COMMENT 'feed 流按时间排序',
    KEY `idx_post_create`   (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='帖子表';

-- -------------------------------------------------------------
-- 4. 帖子-标签关联表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_post_tag`;
CREATE TABLE `bbs_post_tag` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `post_id`     BIGINT      NOT NULL,
    `tag_id`      BIGINT      NOT NULL,
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`),
    KEY `idx_pt_tag` (`tag_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='帖子标签关联';

-- -------------------------------------------------------------
-- 5. 评论表 (支持楼中楼: parent_id 指向父评论)
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_comment`;
CREATE TABLE `bbs_comment` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `post_id`          BIGINT       NOT NULL                COMMENT '所属帖子',
    `user_id`          VARCHAR(32)  NOT NULL                COMMENT '评论人',
    `parent_id`        BIGINT       NOT NULL DEFAULT 0      COMMENT '父评论ID,0表示顶级评论',
    `reply_to_user_id` VARCHAR(32)  DEFAULT NULL            COMMENT '回复给谁(楼中楼)',
    `content`          TEXT         NOT NULL,
    `like_count`       INT          NOT NULL DEFAULT 0,
    `status`           TINYINT      NOT NULL DEFAULT 1      COMMENT '1正常 0已删',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`        VARCHAR(32)  DEFAULT NULL,
    `update_by`        VARCHAR(32)  DEFAULT NULL,
    `deleted`          TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_comment_post`   (`post_id`, `create_time`),
    KEY `idx_comment_user`   (`user_id`),
    KEY `idx_comment_parent` (`parent_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='评论表';

-- -------------------------------------------------------------
-- 6. 点赞表 (帖子/评论通用, target_type 区分)
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_like`;
CREATE TABLE `bbs_like` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`     VARCHAR(32) NOT NULL,
    `target_id`   BIGINT      NOT NULL                COMMENT '目标ID(帖子或评论)',
    `target_type` TINYINT     NOT NULL                COMMENT '1帖子 2评论',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_like` (`user_id`, `target_id`, `target_type`),
    KEY `idx_like_target` (`target_id`, `target_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='点赞表';

-- -------------------------------------------------------------
-- 7. 收藏表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_collect`;
CREATE TABLE `bbs_collect` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`     VARCHAR(32) NOT NULL,
    `post_id`     BIGINT      NOT NULL,
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_collect` (`user_id`, `post_id`, `deleted`),
    KEY `idx_collect_post` (`post_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='收藏表';

-- -------------------------------------------------------------
-- 8. 关注表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_follow`;
CREATE TABLE `bbs_follow` (
    `id`             BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`        VARCHAR(32) NOT NULL                COMMENT '粉丝',
    `follow_user_id` VARCHAR(32) NOT NULL                COMMENT '被关注人',
    `create_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`        TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follow` (`user_id`, `follow_user_id`, `deleted`),
    KEY `idx_follow_target` (`follow_user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='关注表';

-- -------------------------------------------------------------
-- 9. 反馈表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_feedback`;
CREATE TABLE `bbs_feedback` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        VARCHAR(32)  DEFAULT NULL            COMMENT '反馈人ID,游客可空',
    `type`           VARCHAR(20)  NOT NULL                COMMENT '类型: bug/建议/投诉/其他',
    `content`        TEXT         NOT NULL                COMMENT '反馈内容',
    `reply`          TEXT         DEFAULT NULL            COMMENT '管理员回复',
    `reply_user_id`  VARCHAR(32)  DEFAULT NULL            COMMENT '处理人',
    `reply_time`     DATETIME     DEFAULT NULL,
    `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0未处理 1已处理',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`      VARCHAR(32)  DEFAULT NULL,
    `update_by`      VARCHAR(32)  DEFAULT NULL,
    `deleted`        TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_feedback_status` (`status`, `create_time`),
    KEY `idx_feedback_user`   (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='反馈表';

-- -------------------------------------------------------------
-- 10. 字典类型表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_dictionary_type`;
CREATE TABLE `bbs_dictionary_type` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50)  NOT NULL                COMMENT '类型名称',
    `code`        VARCHAR(50)  NOT NULL                COMMENT '类型编码,英文唯一',
    `description` VARCHAR(200) DEFAULT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '1启用 0停用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`   VARCHAR(32)  DEFAULT NULL,
    `update_by`   VARCHAR(32)  DEFAULT NULL,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type_code` (`code`, `deleted`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='字典类型表';

-- -------------------------------------------------------------
-- 11. 字典数据表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `bbs_dictionary`;
CREATE TABLE `bbs_dictionary` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `type_id`     BIGINT       NOT NULL                COMMENT '字典类型ID',
    `label`       VARCHAR(50)  NOT NULL                COMMENT '展示名',
    `value`       VARCHAR(100) NOT NULL                COMMENT '存储值',
    `sort`        INT          NOT NULL DEFAULT 0,
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '1启用 0停用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`   VARCHAR(32)  DEFAULT NULL,
    `update_by`   VARCHAR(32)  DEFAULT NULL,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`type_id`, `sort`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='字典数据表';

-- -------------------------------------------------------------
-- 13. 举报表
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `bbs_report` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        VARCHAR(32)  NOT NULL                COMMENT '举报人ID',
    `target_type`    TINYINT      NOT NULL                COMMENT '举报目标: 1帖子 2评论',
    `target_id`      BIGINT       NOT NULL                COMMENT '目标ID',
    `reason_type`    VARCHAR(20)  NOT NULL                COMMENT '举报原因类型: spam/porn/violence/abuse/illegal/other',
    `content`        VARCHAR(500) DEFAULT NULL            COMMENT '补充说明',
    `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0待处理 1已处理 2已驳回',
    `handle_user_id` VARCHAR(32)  DEFAULT NULL            COMMENT '处理人',
    `handle_remark`  VARCHAR(500) DEFAULT NULL            COMMENT '处理备注',
    `handle_time`    DATETIME     DEFAULT NULL            COMMENT '处理时间',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`      VARCHAR(32)  DEFAULT NULL,
    `update_by`      VARCHAR(32)  DEFAULT NULL,
    `deleted`        TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_report_user`     (`user_id`),
    KEY `idx_report_target`   (`target_type`, `target_id`),
    KEY `idx_report_status`   (`status`, `create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='举报表';

-- -------------------------------------------------------------
-- 12. 附件表
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `bbs_attachment` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`      VARCHAR(32)   NOT NULL                COMMENT '上传人',
    `biz_type`     VARCHAR(20)   DEFAULT NULL            COMMENT '业务类型: post/comment/avatar',
    `biz_id`       BIGINT        DEFAULT NULL            COMMENT '业务ID(帖子/评论ID等)',
    `origin_name`  VARCHAR(255)  NOT NULL                COMMENT '原始文件名',
    `file_name`    VARCHAR(255)  NOT NULL                COMMENT '存储文件名',
    `file_path`    VARCHAR(500)  NOT NULL                COMMENT '存储相对路径',
    `url`          VARCHAR(500)  NOT NULL                COMMENT '访问URL',
    `content_type` VARCHAR(100)  DEFAULT NULL            COMMENT 'MIME 类型',
    `size`         BIGINT        NOT NULL DEFAULT 0      COMMENT '字节数',
    `storage_type` VARCHAR(20)   NOT NULL DEFAULT 'local' COMMENT '存储类型: local/oss',
    `status`       TINYINT       NOT NULL DEFAULT 1      COMMENT '1正常 0已删',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`    VARCHAR(32)   DEFAULT NULL,
    `update_by`    VARCHAR(32)   DEFAULT NULL,
    `deleted`      TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_attach_user`   (`user_id`),
    KEY `idx_attach_biz`    (`biz_type`, `biz_id`),
    KEY `idx_attach_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='附件表';

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
--  初始数据 (可选,首次部署可放开执行)
-- =============================================================

-- 默认板块
INSERT INTO `bbs_category` (`name`, `description`, `sort`) VALUES
('综合交流', '随便聊聊', 1),
('技术分享', '技术向', 2),
('反馈建议', '对站点的反馈', 3);

-- 默认标签
INSERT INTO `bbs_tag` (`name`, `description`) VALUES
('Vue', '前端 Vue 框架'),
('Spring Boot', '后端框架'),
('MySQL', '数据库'),
('求助', '提问求助'),
('分享', '经验分享');
