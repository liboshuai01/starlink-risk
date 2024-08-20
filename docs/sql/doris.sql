use `starlink-risk`;


CREATE TABLE if not exists slr_event
(
    `channel`      VARCHAR(64) NOT NULL COMMENT '渠道',
    `user_id`         VARCHAR(64) NOT NULL COMMENT '用户ID',
    `user_name`         VARCHAR(255) NOT NULL COMMENT '用户名称',
    `event_id`        VARCHAR(64) NOT NULL COMMENT '事件ID',
    `event_timestamp` DATETIME     NOT NULL COMMENT '事件时间戳',
    `event_properties` Map< STRING,STRING> NULL COMMENT '事件属性'
)
    ENGINE = OLAP DUPLICATE key(`channel`, `user_id`, `user_name`, `event_id`, `event_timestamp`)
    PARTITION BY RANGE(`event_timestamp`) ()
    DISTRIBUTED BY HASH(`user_id`) BUCKETS AUTO
    PROPERTIES
(
    "dynamic_partition.enable" = "true",
    "dynamic_partition.time_unit" = "MONTH",
    "dynamic_partition.start" = "-6",
    "dynamic_partition.end" = "1",
    "dynamic_partition.prefix" = "p"
);