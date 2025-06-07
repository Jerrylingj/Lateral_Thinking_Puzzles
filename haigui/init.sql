-- ----------------------------
--  首先删除已存在的表（如果存在），以便于重复执行
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
DROP TABLE IF EXISTS `chat_room`;

-- ----------------------------
--  Table structure for `chat_room`
-- ----------------------------
CREATE TABLE `chat_room` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，房间号 (由前端生成)',
  `status` varchar(50) NOT NULL DEFAULT 'PLAYING' COMMENT '房间状态 (PLAYING, FINISHED)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天室表';

-- ----------------------------
--  Table structure for `chat_message`
-- ----------------------------
CREATE TABLE `chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` bigint(20) NOT NULL COMMENT '所属房间ID',
  `role` varchar(50) NOT NULL COMMENT '消息角色 (SYSTEM, USER, ASSISTANT)',
  `content` text NOT NULL COMMENT '消息内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`) USING BTREE COMMENT '房间ID索引，加速查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表'; 