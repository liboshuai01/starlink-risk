drop table slr_event_info;
drop table slr_event_attribute;
drop table slr_rule_info;
drop table slr_rule_condition;

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

CREATE TABLE `slr_rule_condition`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `condition_code` varchar(64)     NOT NULL DEFAULT '' COMMENT '条件编号',
    `rule_code`      varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `event_code`     varchar(64)     NOT NULL DEFAULT '' COMMENT '事件编号',
    `count`          varchar(64)     NOT NULL DEFAULT '' COMMENT '次数',
    `begin_time`     datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '开始时间',
    `end_time`       datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '结束时间',
    `creator`        varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`        varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`    datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`    datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`        bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控规则条件信息';

CREATE TABLE `slr_rule_info`
(
    `id`                 bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `channel`            varchar(64)     NOT NULL DEFAULT '' COMMENT '渠道',
    `rule_code`          varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `rule_name`          varchar(64)     NOT NULL DEFAULT '' COMMENT '规则名称',
    `rule_desc`          varchar(255)    NOT NULL DEFAULT '' COMMENT '规则描述',
    `status`             tinyint         NOT NULL DEFAULT '0' COMMENT '状态：0-停用，1-启用',
    `condition_operator` text            NOT NULL COMMENT '规则条件组合操作符',
    `creator`            varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`            varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`        datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`        datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`            bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='星链风控规则基本信息';

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