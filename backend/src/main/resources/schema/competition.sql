/*
Navicat MySQL Data Transfer

Source Server         : MySQL8.0.39
Source Server Version : 80039
Source Host           : localhost:3306
Source Database       : competition

Target Server Type    : MYSQL
Target Server Version : 80039
File Encoding         : 65001

Date: 2026-05-07 15:44:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约主题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '预约说明',
  `appointment_date` datetime NOT NULL COMMENT '预约时间',
  `duration` int NOT NULL DEFAULT '60' COMMENT '时长（分钟）',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地点',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED/COMPLETED/CANCELLED）',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '拒绝原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_advisor_id` (`advisor_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES ('1', '3', '1', '1', '1', '2026-05-07 00:00:00', '60', '111', 'APPROVED', null, '2026-05-07 15:23:50', '2026-05-07 15:23:50', '0');

-- ----------------------------
-- Table structure for award
-- ----------------------------
DROP TABLE IF EXISTS `award`;
CREATE TABLE `award` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '获奖 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `work_id` bigint NOT NULL COMMENT '作品 ID',
  `award_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '奖项等级（一等奖/二等奖/三等奖/优秀奖）',
  `certificate_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '证书 URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_work_id` (`work_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='获奖表';

-- ----------------------------
-- Records of award
-- ----------------------------
INSERT INTO `award` VALUES ('1', '2', '3', '1', '一等奖', null, '2026-04-29 13:48:10', '0');

-- ----------------------------
-- Table structure for competition
-- ----------------------------
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '竞赛 ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '竞赛名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '竞赛类型',
  `participation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INDIVIDUAL' COMMENT '参赛方式（INDIVIDUAL-个人赛/TEAM-团队赛）',
  `theme` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '竞赛主题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '竞赛描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片 URL',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态（DRAFT/PUBLISHED/REGISTRATION/ONGOING/REVIEWED/ANNOUNCED/ENDED）',
  `registration_start` datetime DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` datetime DEFAULT NULL COMMENT '报名截止时间',
  `submission_deadline` datetime DEFAULT NULL COMMENT '作品提交截止',
  `review_start` datetime DEFAULT NULL COMMENT '评审开始时间',
  `review_end` datetime DEFAULT NULL COMMENT '评审结束时间',
  `announcement_start` datetime DEFAULT NULL COMMENT '结果公示开始时间',
  `announcement_end` datetime DEFAULT NULL COMMENT '结果公示结束时间',
  `awards_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '奖项配置（JSON 格式）',
  `rules` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '竞赛规则',
  `submission_requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '提交要求',
  `created_by` bigint NOT NULL COMMENT '创建人 ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛表';

-- ----------------------------
-- Records of competition
-- ----------------------------
INSERT INTO `competition` VALUES ('1', 'java程序竞赛', '程序设计', 'INDIVIDUAL', '', '', null, 'REGISTRATION', null, null, null, null, null, null, null, null, '', '', '2', '2026-04-26 19:02:20', '2026-04-26 19:02:20', '0');
INSERT INTO `competition` VALUES ('2', '大学生创新创业团队赛', '创新创业', 'TEAM', '', '', null, 'ANNOUNCED', null, null, null, null, null, null, null, null, '', '', '1', '2026-04-26 19:04:04', '2026-04-26 19:04:04', '0');
INSERT INTO `competition` VALUES ('3', '校园创新创业团队赛', '创新创业', 'TEAM', '创新', '校园创新', 'http://localhost:8090/api/file/download/files/20260507/756115c5-9a57-4a73-abf7-a2a9374ad2ca.jpg', 'REGISTRATION', '2026-05-07 00:00:00', '2026-05-15 00:00:00', '2026-05-17 00:00:00', '2026-05-18 00:00:00', '2026-05-19 00:00:00', '2026-05-20 00:00:00', '2026-05-21 00:00:00', null, '提交创新计划', '提交ppt', '1', '2026-05-07 14:24:57', '2026-05-07 14:24:57', '0');

-- ----------------------------
-- Table structure for competition_admin
-- ----------------------------
DROP TABLE IF EXISTS `competition_admin`;
CREATE TABLE `competition_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛ID',
  `admin_id` bigint NOT NULL COMMENT '管理员ID（竞赛管理员角色的用户）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_competition_admin` (`competition_id`,`admin_id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_admin_id` (`admin_id`),
  CONSTRAINT `competition_admin_ibfk_1` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`) ON DELETE CASCADE,
  CONSTRAINT `competition_admin_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛管理员关联表';

-- ----------------------------
-- Records of competition_admin
-- ----------------------------
INSERT INTO `competition_admin` VALUES ('1', '2', '2', '2026-04-30 15:40:01', '0');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SYSTEM' COMMENT '类型（SYSTEM/REGISTRATION/REVIEW/APPOINTMENT/TEAM）',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读（0-未读 1-已读）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES ('1', '4', '报名审核通过', '您对「java程序竞赛」的报名已通过审核', 'REGISTRATION', '2', '0', '2026-04-26 19:18:06');
INSERT INTO `notification` VALUES ('2', '3', '报名审核通过', '您对「大学生创新创业团队赛」的报名已通过审核', 'REGISTRATION', '1', '0', '2026-04-26 19:18:09');
INSERT INTO `notification` VALUES ('3', '4', '报名审核通过', '您对「大学生创新创业团队赛」的报名已通过审核', 'REGISTRATION', '3', '0', '2026-04-26 20:41:42');
INSERT INTO `notification` VALUES ('4', '6', '报名审核通过', '您对「大学生创新创业团队赛」的报名已通过审核', 'REGISTRATION', '4', '0', '2026-04-26 20:46:44');
INSERT INTO `notification` VALUES ('5', '2', '新评审任务', '您被分配了「大学生创新创业团队赛」竞赛的作品评审任务', 'REVIEW', '1', '0', '2026-04-29 13:37:12');
INSERT INTO `notification` VALUES ('6', '3', '报名审核未通过', '您对「java程序竞赛」的报名未通过审核，原因：没有赛道', 'REGISTRATION', '5', '0', '2026-05-06 18:11:38');
INSERT INTO `notification` VALUES ('7', '3', '报名审核通过', '您对「java程序竞赛」的报名已通过审核', 'REGISTRATION', '9', '0', '2026-05-06 18:24:58');
INSERT INTO `notification` VALUES ('8', '1', '新的预约申请', '学生预约了您的指导：「1」', 'APPOINTMENT', '1', '0', '2026-05-07 15:23:50');
INSERT INTO `notification` VALUES ('9', '3', '预约已通过', '您的预约「1」已被指导老师批准', 'APPOINTMENT', '1', '0', '2026-05-07 15:28:32');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户角色',
  `operation` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作描述',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '操作内容',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP 地址',
  `result` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作结果',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES ('1', '2', 'teacher1', 'TEACHER', '创建竞赛', '竞赛管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:02:20');
INSERT INTO `operation_log` VALUES ('2', '2', 'teacher1', 'TEACHER', '更新竞赛', '竞赛管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:02:58');
INSERT INTO `operation_log` VALUES ('3', '1', 'admin', 'ADMIN', '创建竞赛', '竞赛管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:04:04');
INSERT INTO `operation_log` VALUES ('4', '1', 'admin', 'ADMIN', '更新竞赛', '竞赛管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:04:25');
INSERT INTO `operation_log` VALUES ('5', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:13:36');
INSERT INTO `operation_log` VALUES ('6', '4', 'tang', 'STUDENT', '提交报名', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:15:31');
INSERT INTO `operation_log` VALUES ('7', '1', 'admin', 'ADMIN', '通过报名审核', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:18:06');
INSERT INTO `operation_log` VALUES ('8', '1', 'admin', 'ADMIN', '通过报名审核', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:18:09');
INSERT INTO `operation_log` VALUES ('9', '4', 'tang', 'STUDENT', '提交报名', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 19:34:39');
INSERT INTO `operation_log` VALUES ('10', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '127.0.0.1', 'FAIL: 您已报名该竞赛，请勿重复报名', '2026-04-26 20:19:18');
INSERT INTO `operation_log` VALUES ('11', '1', 'admin', 'ADMIN', '通过报名审核', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 20:41:42');
INSERT INTO `operation_log` VALUES ('12', '6', 'wang', 'STUDENT', '提交报名', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 20:45:57');
INSERT INTO `operation_log` VALUES ('13', '1', 'admin', 'ADMIN', '通过报名审核', '报名管理', null, '127.0.0.1', 'SUCCESS', '2026-04-26 20:46:44');
INSERT INTO `operation_log` VALUES ('14', '1', 'admin', 'ADMIN', '分配评审任务', '评审管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-04-29 13:37:12');
INSERT INTO `operation_log` VALUES ('15', '2', 'teacher1', 'TEACHER', '提交评审打分', '评审管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-04-29 13:39:17');
INSERT INTO `operation_log` VALUES ('16', '1', 'admin', 'ADMIN', '新增用户', '用户管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-04-29 14:12:47');
INSERT INTO `operation_log` VALUES ('17', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-06 18:04:59');
INSERT INTO `operation_log` VALUES ('18', '1', 'admin', 'ADMIN', '驳回报名', '报名管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-06 18:11:38');
INSERT INTO `operation_log` VALUES ('19', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '0:0:0:0:0:0:0:1', 'FAIL: 您已报名该竞赛，请勿重复报名', '2026-05-06 18:12:13');
INSERT INTO `operation_log` VALUES ('20', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-06 18:14:01');
INSERT INTO `operation_log` VALUES ('21', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-06 18:18:19');
INSERT INTO `operation_log` VALUES ('22', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-06 18:21:04');
INSERT INTO `operation_log` VALUES ('23', '3', 'student1', 'STUDENT', '提交报名', '报名管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-06 18:24:05');
INSERT INTO `operation_log` VALUES ('24', '1', 'admin', 'ADMIN', '通过报名审核', '报名管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-06 18:24:58');
INSERT INTO `operation_log` VALUES ('25', '1', 'admin', 'ADMIN', '创建竞赛', '竞赛管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-07 14:24:58');
INSERT INTO `operation_log` VALUES ('26', '1', 'admin', 'ADMIN', '更新竞赛', '竞赛管理', null, '0:0:0:0:0:0:0:1', 'SUCCESS', '2026-05-07 14:25:40');

-- ----------------------------
-- Table structure for registration
-- ----------------------------
DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `project_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目名称',
  `advisor` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '指导教师',
  `participation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INDIVIDUAL' COMMENT '参赛方式',
  `team_id` bigint DEFAULT NULL COMMENT '团队ID',
  `track_id` bigint DEFAULT NULL COMMENT '赛道ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '项目描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED）',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '拒绝原因',
  `reviewed_by` bigint DEFAULT NULL COMMENT '审核人 ID',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_track_id` (`track_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名表';

-- ----------------------------
-- Records of registration
-- ----------------------------
INSERT INTO `registration` VALUES ('1', '2', '3', '基于。。。的创业项目', '', 'TEAM', null, null, '', 'APPROVED', null, '1', '2026-04-26 19:18:09', '2026-04-26 19:13:36', '2026-04-26 20:28:20', '0');
INSERT INTO `registration` VALUES ('2', '1', '4', 'java程序a', '', 'INDIVIDUAL', null, null, '', 'APPROVED', null, '1', '2026-04-26 19:18:06', '2026-04-26 19:15:31', '2026-04-26 19:15:31', '0');
INSERT INTO `registration` VALUES ('3', '2', '4', '团队', '', 'TEAM', null, null, '', 'APPROVED', null, '1', '2026-04-26 20:41:42', '2026-04-26 19:34:39', '2026-04-26 20:28:20', '0');
INSERT INTO `registration` VALUES ('4', '2', '6', '大学生创新创业团队赛 - 团队参赛', null, 'TEAM', '1', null, null, 'APPROVED', null, '1', '2026-04-26 20:46:44', '2026-04-26 20:45:57', '2026-04-26 20:45:57', '0');
INSERT INTO `registration` VALUES ('9', '1', '3', '项目1', '赵六', 'INDIVIDUAL', null, '6', '项目', 'APPROVED', null, '1', '2026-05-06 18:24:58', '2026-05-06 18:24:05', '2026-05-06 18:24:05', '0');

-- ----------------------------
-- Table structure for review_task
-- ----------------------------
DROP TABLE IF EXISTS `review_task`;
CREATE TABLE `review_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评审任务 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `work_id` bigint NOT NULL COMMENT '作品 ID',
  `reviewer_id` bigint NOT NULL COMMENT '评审人 ID',
  `score` decimal(5,2) DEFAULT NULL COMMENT '评分',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评审意见',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/COMPLETED）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_work_id` (`work_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审任务表';

-- ----------------------------
-- Records of review_task
-- ----------------------------
INSERT INTO `review_task` VALUES ('1', '2', '1', '2', '80.00', '', 'COMPLETED', '2026-04-29 13:37:12', '2026-04-29 13:37:12', '0');

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日程标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '日程描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地点',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERSONAL' COMMENT '类型（PERSONAL/APPOINTMENT/MEETING）',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID（如预约ID）',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '颜色标记',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程表';

-- ----------------------------
-- Records of schedule
-- ----------------------------
INSERT INTO `schedule` VALUES ('1', '2', '创建竞赛', '创建个人赛和团队赛', '2026-04-26 00:00:00', '2026-05-01 00:00:00', '江西财经大学', 'PERSONAL', null, '#409eff', '2026-04-26 18:32:10', '2026-04-26 18:32:10', '0');
INSERT INTO `schedule` VALUES ('2', '2', '查看报名情况', '查看报名情况', '2026-04-30 00:00:00', '2026-05-01 00:00:00', '江西财经大学办公室', 'PERSONAL', null, '#409eff', '2026-04-26 18:33:21', '2026-04-26 18:33:21', '0');
INSERT INTO `schedule` VALUES ('3', '1', '测试', '', '2026-04-26 00:00:00', '2026-04-28 00:00:00', '', 'PERSONAL', null, '#409eff', '2026-04-26 19:19:10', '2026-04-26 19:19:10', '0');
INSERT INTO `schedule` VALUES ('4', '1', '1', '222', '2026-05-07 00:00:00', '2026-05-08 06:23:00', '111', 'PERSONAL', null, '#409eff', '2026-05-07 15:25:16', '2026-05-07 15:25:16', '0');
INSERT INTO `schedule` VALUES ('5', '1', '2', '333', '2026-05-07 05:00:00', '2026-05-08 00:00:00', '222', 'PERSONAL', null, '#409eff', '2026-05-07 15:26:03', '2026-05-07 15:26:03', '0');
INSERT INTO `schedule` VALUES ('6', '1', '3', '333', '2026-05-07 15:26:22', '2026-05-08 00:00:00', '333', 'PERSONAL', null, '#409eff', '2026-05-07 15:26:32', '2026-05-07 15:26:32', '0');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID（参考sys_user的id）',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号',
  `entry_year` int DEFAULT NULL COMMENT '入学年份',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '班级',
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '专业',
  `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学院名称',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '照片路径',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '个人简介',
  `is_external` tinyint(1) DEFAULT '0' COMMENT '是否校外学生（0-否, 1-是）',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学校名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_student_no` (`student_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_college_name` (`college_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='学生信息表';

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('1', '3', '202603990', '张三', '362125200303309090', '2023', '7', '软件工程', '计算机学院', null, '666', '0', '北京大学', '2026-05-06 16:39:53', '2026-05-06 16:39:53');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（登录账号）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学号',
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学院',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '班级',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像 URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STUDENT' COMMENT '角色（ADMIN/TEACHER/STUDENT/ADVISOR）',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态（0-禁用 1-正常）',
  `permissions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '权限列表（JSON 格式）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '$2b$10$9QM1aa3Z7L3qKEDyviz1TuHxbFH0cNkLNl6OmSqBOZWYDO3dnhgK2', '系统管理员', null, null, null, null, null, null, 'ADMIN', '1', '[\"*\"]', '2026-04-26 17:12:09', '2026-04-26 17:49:38', '0');
INSERT INTO `sys_user` VALUES ('2', 'teacher1', '$2b$10$U0GmzfvKuCY24H6VQSc2he82p3U.tFVVgJR3lxbJiko0.2aEfLJ3m', '教师用户', null, null, null, null, null, null, 'TEACHER', '1', '[\"competition:*\",\"registration:review\",\"work:review\"]', '2026-04-26 17:12:09', '2026-04-26 17:49:38', '0');
INSERT INTO `sys_user` VALUES ('3', 'student1', '$2b$10$U0GmzfvKuCY24H6VQSc2he82p3U.tFVVgJR3lxbJiko0.2aEfLJ3m', '学生用户', null, null, null, null, null, null, 'STUDENT', '1', '[\"competition:view\",\"registration:create\",\"work:submit\"]', '2026-04-26 17:12:09', '2026-04-26 17:49:38', '0');
INSERT INTO `sys_user` VALUES ('4', 'tang', '$2a$10$a3ZYxhgVhEQGmkw/bmSc9uLXAA7jqgiaRtOs5ZTBgxicFIJbdZbO6', '唐三', '2202303996', '软件学院', null, '18236515669', 'adw4@qq.com', null, 'STUDENT', '1', null, '2026-04-26 19:15:01', '2026-04-26 19:15:01', '0');
INSERT INTO `sys_user` VALUES ('5', 'lai', '$2a$10$QSIG0Rqgn.XpwULNG8CKYO9jLvvTLSUMdm4jPgwd5ZAOQ5mo8bADC', '李四', '2202362255', '软件学院', null, '18460343654', 'dwadw@qq.com', null, 'STUDENT', '1', null, '2026-04-26 19:55:37', '2026-04-26 19:55:37', '0');
INSERT INTO `sys_user` VALUES ('6', 'wang', '$2a$10$80GQJBaBs4083RUPm91HXekqm3jx/W4kQnbPFPzaJRhE5fMgPvCGe', '王五', '2235647895', '软件学院', null, '18460354601', 'adwddwdw@qq.com', null, 'STUDENT', '1', null, '2026-04-26 20:45:43', '2026-04-26 20:45:43', '0');
INSERT INTO `sys_user` VALUES ('7', 'zao', '$2a$10$W3yboE9aNBK7LqFLWdq2ceAdmIREkN28d4/N/8xaTHpyVkpeWyPNe', '赵六', '', '软件学院', null, '13245674567', 'example@.com', null, 'ADVISOR', '1', null, '2026-04-29 14:12:46', '2026-04-29 14:12:46', '0');

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID（参考sys_user的id）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实姓名',
  `teacher_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '工号',
  `education` int DEFAULT NULL COMMENT '学历（1-小学, 2-初中, 3-高中, 4-专科, 5-本科, 6-硕士, 7-博士）',
  `degree` int DEFAULT NULL COMMENT '学位（1-学士, 2-硕士, 3-博士）',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '个人简介',
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '专业',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '照片路径',
  `is_external` tinyint(1) DEFAULT '0' COMMENT '是否校外教师（0-否, 1-是）',
  `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学院名称',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学校名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_college_name` (`college_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='教师信息表';

-- ----------------------------
-- Records of teacher
-- ----------------------------

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '团队名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '团队描述',
  `competition_id` bigint DEFAULT NULL COMMENT '关联竞赛ID（可选）',
  `leader_id` bigint NOT NULL COMMENT '队长ID',
  `advisor_id` bigint DEFAULT NULL COMMENT '指导老师ID',
  `max_members` int NOT NULL DEFAULT '5' COMMENT '最大成员数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/DISBANDED）',
  `invite_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邀请码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_leader_id` (`leader_id`),
  KEY `idx_advisor_id` (`advisor_id`),
  KEY `idx_competition_id` (`competition_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队表';

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES ('1', '测试团队a', '111', '2', '3', null, '5', 'ACTIVE', 'FB0AE354', '2026-04-26 19:13:36', '2026-04-26 19:13:36', '0');
INSERT INTO `team` VALUES ('6', '2', '2', null, '3', null, '5', 'DISBANDED', 'ADF4B134', '2026-04-29 15:12:25', '2026-04-29 16:24:28', '1');
INSERT INTO `team` VALUES ('7', '1', '1', null, '3', null, '5', 'DISBANDED', '443317E4', '2026-04-29 16:18:54', '2026-04-29 16:24:20', '1');
INSERT INTO `team` VALUES ('10', '2', '888', null, '3', null, '5', 'ACTIVE', '283BF14F', '2026-05-06 14:46:01', '2026-05-06 14:46:01', '0');

-- ----------------------------
-- Table structure for team_advisor
-- ----------------------------
DROP TABLE IF EXISTS `team_advisor`;
CREATE TABLE `team_advisor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_advisor` (`team_id`,`advisor_id`),
  KEY `idx_advisor_id` (`advisor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队指导老师关联表';

-- ----------------------------
-- Records of team_advisor
-- ----------------------------
INSERT INTO `team_advisor` VALUES ('2', '1', '2', '2026-04-29 12:59:20', '0');
INSERT INTO `team_advisor` VALUES ('3', '6', '2', '2026-04-29 15:13:13', '1');
INSERT INTO `team_advisor` VALUES ('4', '6', '7', '2026-04-29 15:43:32', '0');
INSERT INTO `team_advisor` VALUES ('5', '7', '7', '2026-04-29 16:20:31', '0');
INSERT INTO `team_advisor` VALUES ('6', '12', '1', '2026-05-06 14:52:01', '0');

-- ----------------------------
-- Table structure for team_advisor_audit
-- ----------------------------
DROP TABLE IF EXISTS `team_advisor_audit`;
CREATE TABLE `team_advisor_audit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `requester_id` bigint NOT NULL COMMENT '申请人ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已同意，REJECTED-已拒绝',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '申请/拒绝理由',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_advisor_id` (`advisor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='指导老师审核申请表';

-- ----------------------------
-- Records of team_advisor_audit
-- ----------------------------
INSERT INTO `team_advisor_audit` VALUES ('1', '6', '2', '3', 'APPROVED', '申请成为团队指导老师', '2026-04-29 15:12:26', '2026-04-29 15:13:13', '0');
INSERT INTO `team_advisor_audit` VALUES ('3', '6', '7', '3', 'APPROVED', '申请成为团队指导老师', '2026-04-29 15:42:13', '2026-04-29 15:43:32', '0');
INSERT INTO `team_advisor_audit` VALUES ('4', '7', '7', '3', 'APPROVED', '申请成为团队指导老师', '2026-04-29 16:18:54', '2026-04-29 16:20:31', '0');
INSERT INTO `team_advisor_audit` VALUES ('5', '9', '2', '2', 'PENDING', '申请成为团队指导老师', '2026-05-06 14:43:42', null, '0');
INSERT INTO `team_advisor_audit` VALUES ('6', '10', '7', '3', 'PENDING', '申请成为团队指导老师', '2026-05-06 14:46:01', null, '0');
INSERT INTO `team_advisor_audit` VALUES ('7', '11', '2', '2', 'PENDING', '申请成为团队指导老师', '2026-05-06 14:50:02', null, '0');

-- ----------------------------
-- Table structure for team_member
-- ----------------------------
DROP TABLE IF EXISTS `team_member`;
CREATE TABLE `team_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成员记录ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEMBER' COMMENT '角色（LEADER/MEMBER）',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_user` (`team_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队成员表';

-- ----------------------------
-- Records of team_member
-- ----------------------------
INSERT INTO `team_member` VALUES ('1', '1', '3', 'LEADER', '2026-04-26 19:13:36', '0');
INSERT INTO `team_member` VALUES ('2', '1', '4', 'MEMBER', '2026-04-26 19:34:38', '0');
INSERT INTO `team_member` VALUES ('3', '1', '5', 'MEMBER', '2026-04-26 19:56:03', '1');
INSERT INTO `team_member` VALUES ('5', '1', '6', 'MEMBER', '2026-04-26 20:45:57', '0');
INSERT INTO `team_member` VALUES ('10', '6', '3', 'LEADER', '2026-04-29 15:12:26', '0');
INSERT INTO `team_member` VALUES ('11', '7', '3', 'LEADER', '2026-04-29 16:18:54', '0');
INSERT INTO `team_member` VALUES ('12', '8', '2', 'LEADER', '2026-05-06 14:40:08', '0');
INSERT INTO `team_member` VALUES ('13', '9', '2', 'LEADER', '2026-05-06 14:43:42', '0');
INSERT INTO `team_member` VALUES ('14', '10', '3', 'LEADER', '2026-05-06 14:46:01', '0');
INSERT INTO `team_member` VALUES ('15', '11', '2', 'LEADER', '2026-05-06 14:50:02', '0');
INSERT INTO `team_member` VALUES ('16', '12', '1', 'LEADER', '2026-05-06 14:52:01', '0');

-- ----------------------------
-- Table structure for team_message
-- ----------------------------
DROP TABLE IF EXISTS `team_message`;
CREATE TABLE `team_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '发送者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TEXT' COMMENT '消息类型（TEXT/SYSTEM）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队消息表';

-- ----------------------------
-- Records of team_message
-- ----------------------------
INSERT INTO `team_message` VALUES ('1', '1', '3', '1', 'TEXT', '2026-04-29 12:59:27', '0');
INSERT INTO `team_message` VALUES ('2', '1', '4', '2', 'TEXT', '2026-04-29 13:58:29', '0');
INSERT INTO `team_message` VALUES ('3', '1', '2', '1', 'TEXT', '2026-04-29 14:09:03', '0');

-- ----------------------------
-- Table structure for team_task
-- ----------------------------
DROP TABLE IF EXISTS `team_task`;
CREATE TABLE `team_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '任务描述',
  `assignee_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/IN_PROGRESS/COMPLETED）',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级（LOW/MEDIUM/HIGH）',
  `due_date` datetime DEFAULT NULL COMMENT '截止日期',
  `created_by` bigint NOT NULL COMMENT '创建人ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_assignee_id` (`assignee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队任务表';

-- ----------------------------
-- Records of team_task
-- ----------------------------
INSERT INTO `team_task` VALUES ('1', '1', '1', '1', '4', 'COMPLETED', 'MEDIUM', '2026-05-08 00:00:00', '3', '2026-05-07 15:31:53', '2026-05-07 15:31:53', '0');

-- ----------------------------
-- Table structure for todo
-- ----------------------------
DROP TABLE IF EXISTS `todo`;
CREATE TABLE `todo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '待办ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '待办描述',
  `due_date` datetime DEFAULT NULL COMMENT '截止日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/COMPLETED）',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级（LOW/MEDIUM/HIGH）',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERSONAL' COMMENT '类型（PERSONAL/TEAM/APPOINTMENT）',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID（如预约ID、团队任务ID）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='个人待办表';

-- ----------------------------
-- Records of todo
-- ----------------------------

-- ----------------------------
-- Table structure for track
-- ----------------------------
DROP TABLE IF EXISTS `track`;
CREATE TABLE `track` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '赛道名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '赛道描述',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='赛道表';

-- ----------------------------
-- Records of track
-- ----------------------------
INSERT INTO `track` VALUES ('1', '2', '人工智能', '666', '1', '2026-05-06 17:26:16', '2026-05-06 17:38:50');
INSERT INTO `track` VALUES ('3', '2', '1', '1', '0', '2026-05-06 17:35:53', '2026-05-06 17:35:58');
INSERT INTO `track` VALUES ('4', '2', '2', '2', '0', '2026-05-06 17:36:43', '2026-05-06 17:36:43');
INSERT INTO `track` VALUES ('5', '1', 'C++', 'C++', '1', '2026-05-06 17:59:53', '2026-05-06 18:23:01');
INSERT INTO `track` VALUES ('6', '1', 'java', 'java', '1', '2026-05-06 17:59:59', '2026-05-06 18:23:11');
INSERT INTO `track` VALUES ('7', '3', '1', '1', '1', '2026-05-07 15:21:47', '2026-05-07 15:21:47');
INSERT INTO `track` VALUES ('8', '3', '2', '2', '1', '2026-05-07 15:21:54', '2026-05-07 15:21:54');

-- ----------------------------
-- Table structure for work
-- ----------------------------
DROP TABLE IF EXISTS `work`;
CREATE TABLE `work` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作品 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '作者 ID',
  `registration_id` bigint NOT NULL COMMENT '报名 ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '作品描述',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品文件 URL',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件原名',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态（SUBMITTED/REVIEWED）',
  `avg_score` decimal(5,2) DEFAULT NULL COMMENT '平均分数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_registration_id` (`registration_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';

-- ----------------------------
-- Records of work
-- ----------------------------
INSERT INTO `work` VALUES ('1', '2', '3', '1', '推荐系统', '推荐', 'http://localhost:8090/api/file/download/works/20260429/678c3d08-e30a-49be-b60a-d4888ab61901.sql', 'competition2.sql', 'SUBMITTED', '80.00', '2026-04-29 13:31:29', '2026-04-29 13:31:29', '0');
INSERT INTO `work` VALUES ('2', '2', '3', '1', '竞赛', '666', 'http://localhost:8090/api/file/download/works/20260506/d2a30968-1075-40be-96e4-fc2c7433a898.sql', '_localhost__2_-2026_04_21_22_04_12-dump.sql', 'SUBMITTED', null, '2026-05-06 16:21:52', '2026-05-06 16:21:52', '0');
INSERT INTO `work` VALUES ('3', '1', '3', '9', '项目1', '项目1', 'http://localhost:8090/api/file/download/works/20260506/64b4d220-1e48-4b23-b104-49c9fdff7830.sql', 'competition2.sql', 'SUBMITTED', null, '2026-05-06 18:25:43', '2026-05-06 18:25:43', '0');
