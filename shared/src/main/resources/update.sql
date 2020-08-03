DROP TABLE IF EXISTS `player_account`;
CREATE TABLE `player_account` (
    `player_id` bigint       NOT NULL COMMENT '玩家id',
    `name`      varchar(255) NOT NULL COMMENT '玩家名字',
    `world_id`  bigint       NOT NULL COMMENT '服务器id',
    PRIMARY KEY (`player_id`)
) ENGINE = 'innodb' COMMENT = '玩家账号';

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
    `uid`       bigint  NOT NULL COMMENT '道具uid',
    `player_id` bigint  NOT NULL COMMENT '玩家id',
    `item_id`   int(11) NOT NULL COMMENT '道具id',
    `count`     bigint  NOT NULL COMMENT '道具数量',
    PRIMARY KEY (`uid`, `player_id`), # same as key (`uid`),KEY (`uid`, `player_id`)
    KEY (`player_id`)
) ENGINE = 'innodb' COMMENT = '道具';