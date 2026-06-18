-- 赛道表
DROP TABLE IF EXISTS `track`;
CREATE TABLE `track` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛ID',
  `name` varchar(100) NOT NULL COMMENT '赛道名称',
  `description` text COMMENT '赛道描述',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='赛道表';