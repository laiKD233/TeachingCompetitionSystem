CREATE TABLE IF NOT EXISTS `team_advisor_audit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `requester_id` bigint NOT NULL COMMENT '申请人ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已同意，REJECTED-已拒绝',
  `reason` text COLLATE utf8mb4_unicode_ci COMMENT '申请/拒绝理由',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_advisor_id` (`advisor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='指导老师审核申请表';