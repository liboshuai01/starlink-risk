CREATE TABLE `slr_rule_model`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `model_code`  varchar(64)     NOT NULL DEFAULT '' COMMENT '模型编号',
    `groovy`      longtext        NOT NULL COMMENT '规则模型groovy代码',
    `version`     bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '模型版本号',
    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则模型';

CREATE TABLE `slr_rule_info`
(
    `id`                          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `channel`                     varchar(64)     NOT NULL DEFAULT '' COMMENT '渠道',
    `rule_code`                   varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `model_code`                  varchar(64)     NOT NULL DEFAULT '' COMMENT '模型编号',
    `rule_name`                   varchar(64)     NOT NULL DEFAULT '' COMMENT '规则名称',
    `rule_desc`                   varchar(255)    NOT NULL DEFAULT '' COMMENT '规则描述',
    `expire_begin_time`           datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '有效开始时间',
    `expire_end_time`             datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '有效结束时间',
    `combined_condition_operator` tinyint         NOT NULL DEFAULT 0 COMMENT '规则条件组合操作符: 0-and；1-or',
    `warn_message`                text            NOT NULL COMMENT '预警信息',
    `warn_interval_value`         bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '预警间隔值（仅用于前端展示）',
    `warn_interval_unit`          varchar(64)     NOT NULL DEFAULT '' COMMENT '预警间隔单位（仅用于前端展示）',
    `warn_interval`               bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '预警间隔',
    `status`                      tinyint         NOT NULL DEFAULT 0 COMMENT '状态：0-停用，1-启用',
    `creator`                     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`                     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`                 datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`                 datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`                     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则基本信息';

CREATE TABLE `slr_rule_condition`
(
    `id`                     bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `rule_code`              varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `event_code`             varchar(64)     NOT NULL DEFAULT '' COMMENT '事件编号',
    `event_threshold`        bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '事件阈值',
    `condition_type`         tinyint         NOT NULL DEFAULT 0 COMMENT '条件类型：0-固定范围条件；1-滑动窗口条件',
    `begin_time`             datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '固定范围开始时间',
    `end_time`               datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '固定范围结束时间',
    `window_size_value`      bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '滑动窗口大小值（仅用于前端展示）',
    `window_size_unit`       tinyint         NOT NULL DEFAULT 0 COMMENT '滑动窗口大小单位（仅用于前端展示）: 0-MILLISECOND; 1-SECOND; 2-MINUTE; 3-HOUR; 4-DAY; 5-WEEK; 6-MONTH; 7-YEAR',
    `window_size`            bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '滑动窗口大小',
    `is_cross_history`       bit(1)          NOT NULL DEFAULT b'1' COMMENT '是否跨历史（目前仅对范围规则生效，周期规则固定跨历史）：0-否，1-是',
    `cross_history_timeline` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '跨历史时间点',
    `creator`                varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`                varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`            datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`            datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`                bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则条件信息';

CREATE TABLE `slr_event_info`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `event_code`  varchar(64)     NOT NULL DEFAULT '' COMMENT '事件编号',
    `channel`     varchar(64)     NOT NULL DEFAULT '' COMMENT '渠道',
    `event_name`  varchar(64)     NOT NULL DEFAULT '' COMMENT '事件名称',
    `event_desc`  varchar(255)    NOT NULL DEFAULT '' COMMENT '事件描述',
    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控事件信息';

CREATE TABLE `slr_event_attribute`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `event_code`  varchar(64)     NOT NULL DEFAULT '' COMMENT '事件编号',
    `field_name`  varchar(64)     NOT NULL DEFAULT '' COMMENT '字段名称',
    `field_desc`  varchar(64)     NOT NULL DEFAULT '' COMMENT '字段描述',
    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控事件属性'
  ROW_FORMAT = Dynamic;

CREATE TABLE `slr_rule_online_count`
(
    `id`           bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `online_count` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '规则在线数量',
    `creator`      varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`      varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`  datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`  datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`      bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则在线数量';

CREATE TABLE `slr_rule_json`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `rule_code`   varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `rule_json`   longtext        NOT NULL COMMENT '规则json',
    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则json数据';

CREATE TABLE `sys_bank_database_version`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `version`       varchar(255) NOT NULL DEFAULT '' COMMENT '卡系统版本',
    `database`      varchar(255) NOT NULL DEFAULT '' COMMENT '生产库名',
    `name`          varchar(255) NOT NULL DEFAULT '' COMMENT '银行名称',
    `bank`          varchar(255) NOT NULL DEFAULT '' COMMENT '银行号',
    `institution`   varchar(255) NOT NULL DEFAULT '' COMMENT '银行机构号',
    `clm_database`  varchar(255) NOT NULL DEFAULT '' COMMENT '积分系统数据库',
    `bank_province` varchar(255) NOT NULL DEFAULT '' COMMENT '银行所属省份地区',
    `bank_type`     varchar(1)   NOT NULL DEFAULT '1' COMMENT '银行类型：0-全国性银行；区域性银行-1（默认）',
    `initals`       varchar(255) NOT NULL DEFAULT '' COMMENT '银行首字母',
    `is_deleted`    varchar(1)   NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0-正常，1-删除',
    `create_user`   varchar(255) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   datetime     NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_user`   varchar(255) NOT NULL DEFAULT '' COMMENT '修改人',
    `update_time`   datetime     NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '银行信息表';

INSERT INTO `sys_bank_database_version`
VALUES (1, '1.0.0', 'prod_db_1', '中国银行', 'C0001', 'B001', 'clm_db_1', '北京', '0', 'Z', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (2, '1.0.1', 'prod_db_2', '建设银行', 'C0002', 'B002', 'clm_db_2', '上海', '0', 'J', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (3, '1.0.2', 'prod_db_3', '工商银行', 'C0003', 'B003', 'clm_db_3', '广东', '1', 'G', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (4, '1.0.3', 'prod_db_4', '农业银行', 'C0004', 'B004', 'clm_db_4', '江苏', '0', 'N', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (5, '1.0.4', 'prod_db_5', '交通银行', 'C0005', 'B005', 'clm_db_5', '浙江', '0', 'J', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (6, '1.0.5', 'prod_db_6', '邮储银行', 'C0006', 'B006', 'clm_db_6', '山东', '1', 'Y', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (7, '1.0.6', 'prod_db_7', '光大银行', 'C0007', 'B007', 'clm_db_7', '湖北', '0', 'G', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (8, '1.0.7', 'prod_db_8', '华夏银行', 'C0008', 'B008', 'clm_db_8', '河南', '0', 'H', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (9, '1.0.8', 'prod_db_9', '民生银行', 'C0009', 'B009', 'clm_db_9', '四川', '1', 'M', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');
INSERT INTO `sys_bank_database_version`
VALUES (10, '1.0.9', 'prod_db_10', '兴业银行', 'C0010', 'B010', 'clm_db_10', '福建', '0', 'X', '0', 'admin',
        '2024-08-22 14:37:51', 'admin', '2024-08-22 14:37:51');