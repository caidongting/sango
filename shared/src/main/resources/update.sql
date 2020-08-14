CREATE DATABASE IF NOT EXISTS `majsoul`
    DEFAULT CHARSET utf8
    COLLATE utf8_bin;

USE `majsoul`;

DROP TABLE IF EXISTS `player_account`;
CREATE TABLE `player_account` (
    `player_id`   bigint       NOT NULL COMMENT '玩家id',
    `name`        varchar(255) NOT NULL COMMENT '玩家名字',
    `world_id`    bigint       NOT NULL COMMENT '服务器id',
    `create_time` timestamp    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`player_id`)
) ENGINE = 'innodb' COMMENT = '玩家账号';

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
    `uid`              bigint    NOT NULL COMMENT '道具uid',
    `player_id`        bigint    NOT NULL COMMENT '玩家id',
    `item_id`          int(11)   NOT NULL COMMENT '道具id',
    `count`            bigint    NOT NULL COMMENT '道具数量',
    `last_update_time` timestamp NOT NULL COMMENT '最后修改时间',
    PRIMARY KEY (`uid`, `player_id`), # same as KEY (`uid`),KEY (`uid`, `player_id`)
    KEY (`player_id`)
) ENGINE = 'innodb' COMMENT = '道具';

DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource` (
    `player_id`        bigint       NOT NULL COMMENT '玩家id',
    `type`             varchar(255) NOT NULL COMMENT '资源类型',
    `count`            bigint       NOT NULL COMMENT '数量',
    `last_update_time` timestamp    NOT NULL COMMENT '最后修改时间',
    PRIMARY KEY (`player_id`, `type`)
) ENGINE = 'innodb' COMMENT = '资源';