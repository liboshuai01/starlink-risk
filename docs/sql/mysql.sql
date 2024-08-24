# create database starlink_risk CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
# GRANT ALL PRIVILEGES ON starlink_risk.* TO 'lbs'@'%';
use starlink_risk;
/*
 Navicat Premium Dump SQL

 Source Server         : master
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : rocky:3310
 Source Schema         : starlink-risk

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 22/08/2024 14:51:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for slr_event_attribute
-- ----------------------------
DROP TABLE IF EXISTS `slr_event_attribute`;
CREATE TABLE `slr_event_attribute`
(
    `id`             bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
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
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控事件属性'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slr_event_attribute
-- ----------------------------
INSERT INTO `slr_event_attribute`
VALUES (1, 'attributeCode01', 'eventCode01', 'campaignId', '活动ID', 'String', '', '', '1970-01-01 00:00:00',
        '1970-01-01 00:00:00', b'0');
INSERT INTO `slr_event_attribute`
VALUES (2, 'attributeCode02', 'eventCode01', 'campaignName', '活动名称', 'String', '', '', '1970-01-01 00:00:00',
        '1970-01-01 00:00:00', b'0');
INSERT INTO `slr_event_attribute`
VALUES (3, 'attributeCode03', 'eventCode02', 'campaignId', '活动ID', 'String', '', '', '1970-01-01 00:00:00',
        '1970-01-01 00:00:00', b'0');
INSERT INTO `slr_event_attribute`
VALUES (4, 'attributeCode04', 'eventCode02', 'campaignName', '活动名称', 'String', '', '', '1970-01-01 00:00:00',
        '1970-01-01 00:00:00', b'0');

-- ----------------------------
-- Table structure for slr_event_info
-- ----------------------------
DROP TABLE IF EXISTS `slr_event_info`;
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
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控事件信息'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slr_event_info
-- ----------------------------
INSERT INTO `slr_event_info`
VALUES (1, 'eventCode01', 'game', '抽奖', '游戏抽奖', '1', '1', '1970-01-01 00:00:00', '1970-01-01 00:00:00', b'0');
INSERT INTO `slr_event_info`
VALUES (2, 'eventCode02', 'game', '充值', '游戏充值', '1', '1', '1970-01-01 00:00:00', '1970-01-01 00:00:00', b'0');

-- ----------------------------
-- Table structure for slr_rule_condition
-- ----------------------------
DROP TABLE IF EXISTS `slr_rule_condition`;
CREATE TABLE `slr_rule_condition`
(
    `id`                bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
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
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则条件信息'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slr_rule_condition
-- ----------------------------
INSERT INTO `slr_rule_condition`
VALUES (1, 'conditionCode01', 'ruleCode01', 'eventCode01', '10', '20', 'minute', '2024-08-21 14:24:32',
        '2024-10-21 14:24:36', '1', '1', '2024-08-21 14:24:46', '2024-08-21 14:24:48', b'0');
INSERT INTO `slr_rule_condition`
VALUES (2, 'conditionCode02', 'ruleCode01', 'eventCode02', '10', '20', 'minute', '2024-08-21 14:24:32',
        '2024-10-21 14:24:36', '1', '1', '2024-08-21 14:24:46', '2024-08-21 14:24:48', b'0');

-- ----------------------------
-- Table structure for slr_rule_count
-- ----------------------------
DROP TABLE IF EXISTS `slr_rule_count`;
CREATE TABLE `slr_rule_count`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `rule_count`  bigint          NOT NULL DEFAULT -1 COMMENT '在线规则数量',
    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则在线数量'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slr_rule_count
-- ----------------------------
INSERT INTO `slr_rule_count`
VALUES (1, 1, '', '', '1970-01-01 00:00:00', '1970-01-01 00:00:00', b'0');

-- ----------------------------
-- Table structure for slr_rule_info
-- ----------------------------
DROP TABLE IF EXISTS `slr_rule_info`;
CREATE TABLE `slr_rule_info`
(
    `id`                     bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `channel`                varchar(64)     NOT NULL DEFAULT '' COMMENT '渠道',
    `rule_code`              varchar(64)     NOT NULL DEFAULT '' COMMENT '规则编号',
    `model_code`             varchar(64)     NOT NULL DEFAULT '' COMMENT '模型编号',
    `rule_type`              tinyint         NOT NULL DEFAULT 0 COMMENT '规则类型：0-范围规则；1-周期规则',
    `rule_name`              varchar(64)     NOT NULL DEFAULT '' COMMENT '规则名称',
    `rule_desc`              varchar(255)    NOT NULL DEFAULT '' COMMENT '规则描述',
    `cross_history`          bit(1)          NOT NULL DEFAULT b'1' COMMENT '是否跨历史（目前仅对范围规则生效，周期规则固定跨历史）：0-否，1-是',
    `history_timeline`       datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '历史时间点',
    `expire_begin_time`      datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '有效开始时间',
    `expire_end_time`        datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '有效结束时间',
    `condition_operator`     text            NOT NULL COMMENT '规则条件组合操作符',
    `warning_message`        text            NOT NULL COMMENT '预警信息',
    `warning_interval_value` varchar(64)     NOT NULL DEFAULT '' COMMENT '预警间隔值',
    `warning_interval_unit`  varchar(64)     NOT NULL DEFAULT '' COMMENT '预警间隔单位',
    `status`                 tinyint         NOT NULL DEFAULT 0 COMMENT '状态：0-停用，1-启用',
    `creator`                varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`                varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time`            datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time`            datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`                bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则基本信息'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slr_rule_info
-- ----------------------------
INSERT INTO `slr_rule_info`
VALUES (1, 'game', 'ruleCode01', 'modelCode01', 1, '规则01', '高频抽奖规则', b'1', '2024-08-24 21:27:36',
        '2024-08-24 21:27:36', '2024-09-24 21:27:36', 'and',
        '[异常高频抽奖]${bankName}：${campaignName}(${campaignId})中游戏用户(${keyId})最近${windowSize}内抽奖数量为${eventValueSum}，超过${eventThreshold}次，请您及时查看原因！',
        '5', 'minute', 1, '1', '1', '2024-08-21 14:22:16', '2024-08-21 14:22:20', b'0');

-- ----------------------------
-- Table structure for slr_rule_json
-- ----------------------------
DROP TABLE IF EXISTS `slr_rule_json`;
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
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则json数据'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slr_rule_json
-- ----------------------------
INSERT INTO `slr_rule_json`
VALUES (1, 'ruleCode01',
        '{"channel":"game","rule_code":"ruleCode01","model_code":"modelCode01","rule_type":1,"rule_name":"规则01","rule_desc":"高频抽奖规则","cross_history":true,"history_timeline":"2024-08-24 21:27:36","expire_begin_time":"2024-08-24 21:27:36","expire_end_time":"2024-09-24 21:27:36","condition_operator":"and","warning_message":"[异常高频抽奖]${bankName}：${campaignName}(${campaignId})中游戏用户(${keyId})最近${windowSize}内抽奖数量为${eventValueSum}，超过${eventThreshold}次，请您及时查看原因！","warning_interval_value":"5","warning_interval_unit":"minute","status":1,"rule_condition_list":[{"condition_code":"conditionCode01","rule_code":"ruleCode01","event_code":"eventCode01","event_threshold":"10","window_size_value":"20","window_size_unit":"minute","begin_time":"2024-08-21 14:24:32","end_time":"2024-10-21 14:24:36","event_info":{"event_code":"eventCode02","channel":"game","event_name":"充值","event_desc":"游戏充值","event_attribute":[{"attribute_code":"attributeCode01","event_code":"eventCode01","field_name":"campaignId","field_desc":"活动ID"},{"attribute_code":"attributeCode02","event_code":"eventCode01","field_name":"campaignName","field_desc":"活动名称"}]}},{"condition_code":"conditionCode02","rule_code":"ruleCode01","event_code":"eventCode02","event_threshold":"10","window_size_value":"20","window_size_unit":"minute","begin_time":"2024-08-21 14:24:32","end_time":"2024-10-21 14:24:36","event_info":{"event_code":"eventCode02","channel":"game","event_name":"充值","event_desc":"游戏充值","event_attribute":[{"attribute_code":"attributeCode03","event_code":"eventCode02","field_name":"campaignId","field_desc":"活动ID","field_type":"String"},{"attribute_code":"attributeCode04","event_code":"eventCode02","field_name":"campaignName","field_desc":"活动名称","field_type":"String"}]}}],"rule_model":"代码省略"}',
        '', '', '1970-01-01 00:00:00', '1970-01-01 00:00:00', b'0');

-- ----------------------------
-- Table structure for slr_rule_model
-- ----------------------------
DROP TABLE IF EXISTS `slr_rule_model`;
CREATE TABLE `slr_rule_model`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `model_code`  varchar(64)     NOT NULL DEFAULT '' COMMENT '模型编号',
    `rule_model`  longtext        NOT NULL COMMENT '规则模型代码',
    `creator`     varchar(255)    NOT NULL DEFAULT '' COMMENT '创建用户',
    `updater`     varchar(255)    NOT NULL DEFAULT '' COMMENT '更新用户',
    `create_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
    `deleted`     bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '星链风控规则模型'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slr_rule_model
-- ----------------------------
INSERT INTO `slr_rule_model`
VALUES (1, 'modelCode01', '代码省略', '', '', '1970-01-01 00:00:00', '1970-01-01 00:00:00', b'0');

-- ----------------------------
-- Table structure for sys_bank_database_version
-- ----------------------------
DROP TABLE IF EXISTS `sys_bank_database_version`;
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
  AUTO_INCREMENT = 11
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT = '银行信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_bank_database_version
-- ----------------------------
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

SET FOREIGN_KEY_CHECKS = 1;
