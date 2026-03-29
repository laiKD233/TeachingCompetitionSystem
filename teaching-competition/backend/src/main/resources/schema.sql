-- 创建数据库
CREATE DATABASE IF NOT EXISTS teaching_competition DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE teaching_competition;

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
  `name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `student_id` VARCHAR(50) DEFAULT NULL COMMENT '学号',
  `college` VARCHAR(100) DEFAULT NULL COMMENT '学院',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
  `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色（ADMIN/TEACHER/STUDENT）',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `permissions` TEXT DEFAULT NULL COMMENT '权限列表（JSON 格式）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 竞赛表
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '竞赛 ID',
  `name` VARCHAR(200) NOT NULL COMMENT '竞赛名称',
  `type` VARCHAR(50) NOT NULL COMMENT '竞赛类型',
  `theme` VARCHAR(500) DEFAULT NULL COMMENT '竞赛主题',
  `description` TEXT DEFAULT NULL COMMENT '竞赛描述',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片 URL',
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态（DRAFT/PUBLISHED/REGISTRATION/ONGOING/REVIEWED/ANNOUNCED/ENDED）',
  `registration_start` DATETIME DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` DATETIME DEFAULT NULL COMMENT '报名截止时间',
  `submission_deadline` DATETIME DEFAULT NULL COMMENT '作品提交截止',
  `review_start` DATETIME DEFAULT NULL COMMENT '评审开始时间',
  `review_end` DATETIME DEFAULT NULL COMMENT '评审结束时间',
  `announcement_start` DATETIME DEFAULT NULL COMMENT '结果公示开始时间',
  `announcement_end` DATETIME DEFAULT NULL COMMENT '结果公示结束时间',
  `awards_config` TEXT DEFAULT NULL COMMENT '奖项配置（JSON 格式）',
  `rules` TEXT DEFAULT NULL COMMENT '竞赛规则',
  `submission_requirements` TEXT DEFAULT NULL COMMENT '提交要求',
  `created_by` BIGINT NOT NULL COMMENT '创建人 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛表';

-- 报名表
DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名 ID',
  `competition_id` BIGINT NOT NULL COMMENT '竞赛 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `project_name` VARCHAR(200) NOT NULL COMMENT '项目名称',
  `advisor` VARCHAR(50) DEFAULT NULL COMMENT '指导教师',
  `description` TEXT DEFAULT NULL COMMENT '项目描述',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED）',
  `reject_reason` TEXT DEFAULT NULL COMMENT '拒绝原因',
  `reviewed_by` BIGINT DEFAULT NULL COMMENT '审核人 ID',
  `reviewed_at` DATETIME DEFAULT NULL COMMENT '审核时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名表';

-- 作品表
DROP TABLE IF EXISTS `work`;
CREATE TABLE `work` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作品 ID',
  `competition_id` BIGINT NOT NULL COMMENT '竞赛 ID',
  `user_id` BIGINT NOT NULL COMMENT '作者 ID',
  `registration_id` BIGINT NOT NULL COMMENT '报名 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
  `description` TEXT DEFAULT NULL COMMENT '作品描述',
  `file_url` VARCHAR(500) NOT NULL COMMENT '作品文件 URL',
  `file_name` VARCHAR(200) NOT NULL COMMENT '文件原名',
  `status` VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态（SUBMITTED/REVIEWED）',
  `avg_score` DECIMAL(5,2) DEFAULT NULL COMMENT '平均分数',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_registration_id` (`registration_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';

-- 评审任务表
DROP TABLE IF EXISTS `review_task`;
CREATE TABLE `review_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评审任务 ID',
  `competition_id` BIGINT NOT NULL COMMENT '竞赛 ID',
  `work_id` BIGINT NOT NULL COMMENT '作品 ID',
  `reviewer_id` BIGINT NOT NULL COMMENT '评审人 ID',
  `score` DECIMAL(5,2) DEFAULT NULL COMMENT '评分',
  `comment` TEXT DEFAULT NULL COMMENT '评审意见',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/COMPLETED）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_work_id` (`work_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审任务表';

-- 获奖表
DROP TABLE IF EXISTS `award`;
CREATE TABLE `award` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '获奖 ID',
  `competition_id` BIGINT NOT NULL COMMENT '竞赛 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `work_id` BIGINT NOT NULL COMMENT '作品 ID',
  `award_level` VARCHAR(50) NOT NULL COMMENT '奖项等级（一等奖/二等奖/三等奖/优秀奖）',
  `certificate_url` VARCHAR(255) DEFAULT NULL COMMENT '证书 URL',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_work_id` (`work_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='获奖表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `operation` VARCHAR(200) NOT NULL COMMENT '操作描述',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP 地址',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 初始化数据：默认管理员账号（密码：admin123）
INSERT INTO `sys_user` (`username`, `password`, `name`, `role`, `status`, `permissions`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8RqX5pMXYvhLJHJdEqGzPqBx.5zW6', '系统管理员', 'ADMIN', 1, '["*"]'),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8RqX5pMXYvhLJHJdEqGzPqBx.5zW6', '教师用户', 'TEACHER', 1, '["competition:*","registration:review","work:review"]'),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8RqX5pMXYvhLJHJdEqGzPqBx.5zW6', '学生用户', 'STUDENT', 1, '["competition:view","registration:create","work:submit"]');
