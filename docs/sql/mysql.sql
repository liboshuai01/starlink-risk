drop table if exists slr_rule_json;
drop table if exists slr_rule_model;
drop table if exists slr_rule_info;
drop table if exists slr_rule_condition;
drop table if exists slr_event_info;
drop table if exists slr_event_attribute;

CREATE TABLE `slr_rule_json`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `rule_code`   varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `rule_json`   longtext        NOT NULL COMMENT '规则json',

    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控规则json数据';

CREATE TABLE `slr_rule_model`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `model_code`  varchar(64)     NOT NULL DEFAULT '' COMMENT '模型编号',
    `rule_model`  longtext        NOT NULL COMMENT '规则模型代码',

    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控规则模型';

CREATE TABLE `slr_rule_info`
(
    `id`                     bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `channel`                varchar(64)     NOT NULL DEFAULT '' COMMENT '渠道',
    `rule_code`              varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `model_code`             varchar(64)     NOT NULL DEFAULT '' COMMENT '模型编号',
    `rule_name`              varchar(64)     NOT NULL DEFAULT '' COMMENT '规则名称',
    `rule_desc`              varchar(255)    NOT NULL DEFAULT '' COMMENT '规则描述',
    `condition_operator`     text            NOT NULL COMMENT '规则条件组合操作符',
    `warning_message`        text            NOT NULL COMMENT '预警信息',
    `warning_interval_value` varchar(64)     NOT NULL DEFAULT '' COMMENT '预警间隔值',
    `warning_interval_unit`  varchar(64)     NOT NULL DEFAULT '' COMMENT '预警间隔单位',
    `status`                 tinyint         NOT NULL DEFAULT '0' COMMENT '状态：0-停用，1-启用',

    `creator`                varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`                varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`            datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`            datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`                bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控规则基本信息';

CREATE TABLE `slr_rule_condition`
(
    `id`                bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `condition_code`    varchar(64)     NOT NULL DEFAULT '' COMMENT '条件编号',
    `rule_code`         varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `event_code`        varchar(64)     NOT NULL DEFAULT '' COMMENT '事件编号',
    `event_threshold`   varchar(64)     NOT NULL DEFAULT '' COMMENT '事件阈值',
    `window_size_value` varchar(64)     NOT NULL DEFAULT '' COMMENT '窗口大小值',
    `window_size_unit`  varchar(64)     NOT NULL DEFAULT '' COMMENT '窗口大小单位',
    `begin_time`        datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '开始时间',
    `end_time`          datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '结束时间',

    `creator`           varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`           varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`       datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`       datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`           bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控规则条件信息';

CREATE TABLE `slr_event_info`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `event_code`  varchar(64)     NOT NULL DEFAULT '' COMMENT '事件编号',
    `channel`     varchar(64)     NOT NULL DEFAULT '' COMMENT '渠道',
    `event_name`  varchar(64)     NOT NULL DEFAULT '' COMMENT '事件名称',
    `event_desc`  varchar(255)    NOT NULL DEFAULT '' COMMENT '事件描述',

    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控事件信息';

CREATE TABLE `slr_event_attribute`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `attribute_code` varchar(64)     NOT NULL DEFAULT '' COMMENT '属性编号',
    `event_code`     varchar(64)     NOT NULL DEFAULT '' COMMENT '事件编号',
    `field_name`     varchar(64)     NOT NULL DEFAULT '' COMMENT '字段名称',
    `field_desc`     varchar(64)     NOT NULL DEFAULT '' COMMENT '字段描述',
    `field_type`     varchar(64)     NOT NULL DEFAULT '' COMMENT '字段类型',

    `creator`        varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`        varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`    datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`    datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`        bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控事件属性';