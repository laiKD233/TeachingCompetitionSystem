/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 90600 (9.6.0)
 Source Host           : localhost:3306
 Source Schema         : competition

 Target Server Type    : MySQL
 Target Server Version : 90600 (9.6.0)
 File Encoding         : 65001

 Date: 23/04/2026 17:04:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约主题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '预约说明',
  `appointment_date` datetime NOT NULL COMMENT '预约时间',
  `duration` int NOT NULL DEFAULT 60 COMMENT '时长（分钟）',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地点',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED/COMPLETED/CANCELLED）',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '拒绝原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_advisor_id`(`advisor_id` ASC) USING BTREE,
  INDEX `idx_appointment_date`(`appointment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1, 17, 12, 'java并发', 'java', '2026-04-28 00:00:00', 60, '一教212', 'APPROVED', NULL, '2026-04-22 19:39:30', '2026-04-22 19:39:30', 0);

-- ----------------------------
-- Table structure for award
-- ----------------------------
DROP TABLE IF EXISTS `award`;
CREATE TABLE `award`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '获奖 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `work_id` bigint NOT NULL COMMENT '作品 ID',
  `award_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '奖项等级（一等奖/二等奖/三等奖/优秀奖）',
  `certificate_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证书 URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  `status` int NULL DEFAULT NULL COMMENT '0.刚录入，1，竞赛管理员已审核通过，-1审核不通过，-2已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_competition_id`(`competition_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_work_id`(`work_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '获奖表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of award
-- ----------------------------
INSERT INTO `award` VALUES (1, 3, 17, 2, '特等奖', NULL, '2026-04-01 20:31:01', 0, NULL);

-- ----------------------------
-- Table structure for competition
-- ----------------------------
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '竞赛 ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '竞赛名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '竞赛类型',
  `theme` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '竞赛主题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '竞赛描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片 URL',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态（DRAFT/PUBLISHED/REGISTRATION/ONGOING/REVIEWED/ANNOUNCED/ENDED）',
  `registration_start` datetime NULL DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` datetime NULL DEFAULT NULL COMMENT '报名截止时间',
  `submission_deadline` datetime NULL DEFAULT NULL COMMENT '作品提交截止',
  `review_start` datetime NULL DEFAULT NULL COMMENT '评审开始时间',
  `review_end` datetime NULL DEFAULT NULL COMMENT '评审结束时间',
  `announcement_start` datetime NULL DEFAULT NULL COMMENT '结果公示开始时间',
  `announcement_end` datetime NULL DEFAULT NULL COMMENT '结果公示结束时间',
  `awards_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '奖项配置（JSON 格式）',
  `rules` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '竞赛规则',
  `submission_requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '提交要求',
  `created_by` bigint NOT NULL COMMENT '创建人 ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_by`(`created_by` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '竞赛表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition
-- ----------------------------
INSERT INTO `competition` VALUES (1, '2026 年大学生程序设计竞赛aa', '程序设计', '创新引领未来', '面向全校学生的程序设计竞赛', NULL, 'PUBLISHED', '2026-03-01 00:00:00', '2026-03-30 23:59:59', '2026-05-15 23:59:59', NULL, NULL, NULL, NULL, NULL, '参赛规则说明...', '提交要求说明...', 4, '2026-03-19 22:20:54', '2026-03-27 20:51:34', 1);
INSERT INTO `competition` VALUES (2, '2026 年大学生程序设计竞赛bb', '程序设计', '创新引领未来', '面向全校学生的程序设计竞赛', NULL, 'PUBLISHED', '2026-03-01 00:00:00', '2026-03-30 23:59:59', '2026-05-15 23:59:59', NULL, NULL, NULL, NULL, NULL, '参赛规则说明...', '提交要求说明...', 4, '2026-03-19 22:21:50', '2026-03-27 20:51:31', 1);
INSERT INTO `competition` VALUES (3, '2026 年大学生程序设计竞赛', '程序设计', '创新引领未来', '面向全校学生的程序设计竞赛', '/api/file/download/1774617627646_11166736851300.jpg', 'ANNOUNCED', '2026-03-01 00:00:00', '2026-03-30 23:59:59', '2026-05-15 23:59:59', '2026-03-27 21:05:17', '2026-04-01 00:00:00', NULL, NULL, NULL, '参赛规则说明...', '提交要求说明...', 4, '2026-03-19 22:22:02', '2026-03-26 21:02:41', 0);
INSERT INTO `competition` VALUES (4, '2026大学生数学建模大赛', '数学建模', '数学建模大赛', '建模', NULL, 'REGISTRATION', '2026-03-25 00:00:00', '2026-03-26 00:00:00', '2026-04-01 00:00:00', '2026-04-03 00:00:00', '2026-04-08 00:00:00', NULL, NULL, NULL, '', '建模要', 4, '2026-03-27 21:09:11', '2026-03-27 21:09:11', 0);
INSERT INTO `competition` VALUES (5, '中国大学生创新创业', '创新创业', '创新创业', '一类', NULL, 'REGISTRATION', '2026-04-21 00:00:00', '2026-04-30 00:00:00', '2026-05-10 00:00:00', '2026-05-11 00:00:00', '2026-05-20 00:00:00', '2026-05-21 00:00:00', '2026-05-30 00:00:00', NULL, '5人一组', 'ppt', 4, '2026-04-22 16:18:45', '2026-04-22 16:18:45', 0);
INSERT INTO `competition` VALUES (6, '挑战杯', '创新创业', '挑战', '赛道', NULL, 'REGISTRATION', '2026-04-23 00:00:00', '2026-04-24 00:00:00', '2026-04-25 00:00:00', '2026-04-27 00:00:00', '2026-04-28 00:00:00', '2026-04-29 00:00:00', '2026-04-30 00:00:00', NULL, '1', '1', 4, '2026-04-23 14:47:43', '2026-04-23 14:47:43', 0);

-- ----------------------------
-- Table structure for competition_admin
-- ----------------------------
DROP TABLE IF EXISTS `competition_admin`;
CREATE TABLE `competition_admin`  (
  `id` bigint NOT NULL,
  `userid` bigint NULL DEFAULT NULL,
  `competition_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_admin
-- ----------------------------

-- ----------------------------
-- Table structure for huojiangqinkuan
-- ----------------------------
DROP TABLE IF EXISTS `huojiangqinkuan`;
CREATE TABLE `huojiangqinkuan`  (
  `id` bigint NOT NULL,
  `college` bigint NULL DEFAULT NULL,
  `projectname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '竞赛项目名称',
  `workname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参赛作品名',
  `studentlist` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学生idlist',
  `teacher1` bigint NULL DEFAULT NULL COMMENT '指导教师1',
  `teacher2` bigint NULL DEFAULT NULL,
  `teacher3` bigint NULL DEFAULT NULL,
  `teacher4` bigint NULL DEFAULT NULL,
  `teacher5` bigint NULL DEFAULT NULL,
  `banjiangdanwei` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颁奖单位',
  `huojiangleibie` int NULL DEFAULT NULL COMMENT '获奖类别',
  `jibie` int NULL DEFAULT NULL COMMENT '级别',
  `huojiangdengji` int NULL DEFAULT NULL COMMENT '获奖等级',
  `istuanti` int NULL DEFAULT NULL COMMENT '是否是团体奖',
  `huojiangshijian` date NULL DEFAULT NULL COMMENT '获奖时间',
  `xuelicengci` int NULL DEFAULT NULL COMMENT '学历层次',
  `zhengshu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证书',
  `memo` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of huojiangqinkuan
-- ----------------------------

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `operation` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作描述',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作模块',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP 地址',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for registration
-- ----------------------------
DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `project_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `advisor` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指导教师',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '项目描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED）',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '拒绝原因',
  `reviewed_by` bigint NULL DEFAULT NULL COMMENT '审核人 ID',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_competition_id`(`competition_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of registration
-- ----------------------------
INSERT INTO `registration` VALUES (1, 4, 17, '建模项目A', '暂无', '关于A的数学建模', 'REJECTED', '不合格', 12, '2026-03-27 21:29:01', '2026-03-27 21:24:00', '2026-03-27 21:24:00', 0);
INSERT INTO `registration` VALUES (2, 3, 17, '十七届蓝桥杯竞赛c++', '陈爱国', '程序设计竞赛', 'APPROVED', NULL, 12, '2026-03-27 21:38:28', '2026-03-27 21:38:11', '2026-03-27 21:38:11', 0);
INSERT INTO `registration` VALUES (3, 5, 17, '助农', '李四', '新时代助农', 'PENDING', NULL, NULL, NULL, '2026-04-22 17:02:34', '2026-04-22 17:02:34', 0);

-- ----------------------------
-- Table structure for review_task
-- ----------------------------
DROP TABLE IF EXISTS `review_task`;
CREATE TABLE `review_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评审任务 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `work_id` bigint NOT NULL COMMENT '作品 ID',
  `reviewer_id` bigint NOT NULL COMMENT '评审人 ID',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '评分',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评审意见',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/COMPLETED）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_competition_id`(`competition_id` ASC) USING BTREE,
  INDEX `idx_work_id`(`work_id` ASC) USING BTREE,
  INDEX `idx_reviewer_id`(`reviewer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评审任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review_task
-- ----------------------------
INSERT INTO `review_task` VALUES (1, 3, 2, 1, NULL, NULL, 'PENDING', '2026-03-31 21:18:31', '2026-03-31 21:18:31', 0);
INSERT INTO `review_task` VALUES (2, 3, 2, 4, 86.00, '还行', 'COMPLETED', '2026-04-01 19:45:58', '2026-04-01 19:45:58', 0);
INSERT INTO `review_task` VALUES (3, 3, 2, 12, 80.00, '好', 'COMPLETED', '2026-04-01 19:46:12', '2026-04-01 19:46:12', 0);

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日程标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '日程描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地点',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERSONAL' COMMENT '类型（PERSONAL/APPOINTMENT/MEETING）',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联ID（如预约ID）',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '颜色标记',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedule
-- ----------------------------
INSERT INTO `schedule` VALUES (1, 12, '团队指导', '1', '2026-04-23 00:00:00', '2026-04-24 00:00:00', '一教212', 'PERSONAL', NULL, '#409eff', '2026-04-22 19:26:07', '2026-04-22 19:26:07', 0);
INSERT INTO `schedule` VALUES (2, 12, '学生指导', '1', '2026-04-27 00:00:00', '2026-04-29 00:00:00', '一教208', 'PERSONAL', NULL, '#409eff', '2026-04-22 19:28:19', '2026-04-22 19:28:19', 0);
INSERT INTO `schedule` VALUES (3, 12, '团队指导', '1', '2026-04-27 00:00:00', '2026-04-30 00:00:00', '一教212', 'PERSONAL', NULL, '#409eff', '2026-04-22 19:29:22', '2026-04-22 19:29:22', 0);
INSERT INTO `schedule` VALUES (4, 4, '团队指导', '1', '2026-04-23 14:52:25', '2026-04-24 00:00:00', '112', 'PERSONAL', NULL, '#409eff', '2026-04-23 14:52:36', '2026-04-23 14:52:36', 0);
INSERT INTO `schedule` VALUES (5, 4, '团队指导', '3', '2026-04-23 00:00:00', '2026-04-24 00:00:00', '112', 'PERSONAL', NULL, '#409eff', '2026-04-23 14:53:38', '2026-04-23 14:53:38', 0);

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` bigint NOT NULL,
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `idcard` varchar(21) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `enteryear` int NULL DEFAULT NULL COMMENT '入学年份',
  `clazz` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `zhuanye` int NULL DEFAULT NULL COMMENT '专业',
  `college` int NULL DEFAULT NULL COMMENT '学院',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `jianjie` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `isxiaowai` int NULL DEFAULT NULL COMMENT '是否校外',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（登录账号）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学号',
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学院',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像 URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STUDENT' COMMENT '角色（ADMIN/TEACHER/STUDENT）',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `permissions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '权限列表（JSON 格式）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (4, 'admin', '$2a$10$8eg5IioZmOtmaegigbElGuWcueFmsfj2XEUGNm5IScCU80SoLs2na', '系统管理员', NULL, NULL, NULL, '15968853348', 'admin@163.com', NULL, 'ADMIN', 1, NULL, '2026-03-19 16:37:15', '2026-03-19 21:52:00', 0);
INSERT INTO `sys_user` VALUES (5, 'teacher1', '$2a$10$xqnDYQBgtDJBAmg6n8WVlufwcxZFuoKoKpk54Es6Etj4KAHSIuwhm', '老师1', NULL, NULL, NULL, NULL, NULL, NULL, 'TEACHER', 1, NULL, '2026-03-19 16:37:15', '2026-03-19 22:27:41', 1);
INSERT INTO `sys_user` VALUES (6, 'student1', '$2a$10$xqnDYQBgtDJBAmg6n8WVlufwcxZFuoKoKpk54Es6Etj4KAHSIuwhm', '学生用户1', NULL, NULL, NULL, NULL, NULL, NULL, 'STUDENT', 1, NULL, '2026-03-19 16:37:15', '2026-03-19 22:25:31', 1);
INSERT INTO `sys_user` VALUES (8, 'xie', '$2a$10$xqnDYQBgtDJBAmg6n8WVlufwcxZFuoKoKpk54Es6Etj4KAHSIuwhm', '王五', '2026001', '计算机学院', NULL, '13800138000', 'student1@example.com', NULL, 'STUDENT', 1, NULL, '2026-03-19 18:13:31', '2026-03-19 21:45:55', 0);
INSERT INTO `sys_user` VALUES (10, 'zensen', '$2a$10$NXQPWj/4f08dsNu3XoJ9duOyLZWmXOiwdSLv3VK8EHbYcnQbAZ3nG', '李四', '2026002', '软件学院', '软件工程 2101', '13900139000', 'lisi@example.com', NULL, 'STUDENT', 1, NULL, '2026-03-19 18:16:18', '2026-03-27 21:46:27', 1);
INSERT INTO `sys_user` VALUES (11, 'lai', '$2a$10$G90LpYRnVsyFViBAbethueUzRYT8ekej8iMatga8e3Vzme4p6yEOS', '赖涵', '2026003', '软件学院', NULL, '18460347716', '3167269314@qq.com', NULL, 'STUDENT', 1, NULL, '2026-03-19 21:47:39', '2026-03-19 22:24:44', 1);
INSERT INTO `sys_user` VALUES (12, 'caozh', '$2a$10$/1KqrRV2d3FRWLKLXYpuoe1KlkyOEkL0OMXwNWNTiKsCsFK2osVwG', '曹老师', NULL, NULL, NULL, '13588694457', 'cao@163.com', NULL, 'TEACHER', 1, NULL, '2026-03-19 22:26:46', '2026-03-19 22:26:46', 0);
INSERT INTO `sys_user` VALUES (17, 'tang', '$2a$10$G90LpYRnVsyFViBAbethueUzRYT8ekej8iMatga8e3Vzme4p6yEOS', '张三', '2026008', '软件学院', NULL, '18460347716', '3167269314@qq.com', NULL, 'STUDENT', 1, NULL, '2026-03-21 14:33:12', '2026-03-21 14:36:01', 0);
INSERT INTO `sys_user` VALUES (18, 'liu', '$2a$10$P0YO39nA9T2q365jv27Z1.tuSC4TQC38tExxineTiakbyLVhNF47m', '刘强', '2202303998', '物联网学院', NULL, '15290876653', 'awdwdw@qq.com', NULL, 'STUDENT', 1, NULL, '2026-03-30 16:15:21', '2026-03-30 16:15:21', 0);
INSERT INTO `sys_user` VALUES (19, 'zao', '$2a$10$116X/ytetyrCO9brhIQRSuNLZsPPPzS85yDUWmkNuZZ9/3KQQCbPm', '赵明', '', '软件学院', NULL, '13234563456', '@example.com', NULL, 'ADVISOR', 1, NULL, '2026-04-22 20:08:42', '2026-04-22 20:08:42', 0);
INSERT INTO `sys_user` VALUES (20, 'zhang', '$2a$10$dMWO7rvrgoiij/bEfd.2oefxMoTt2d/os1oHfXGLqQNSbKRCzSB36', '张四', '0233789', '软件学院', NULL, '14245674567', '@example.com', NULL, 'STUDENT', 1, NULL, '2026-04-22 20:55:05', '2026-04-22 20:55:05', 0);

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `id` bigint NOT NULL,
  `userid` bigint NULL DEFAULT NULL COMMENT '参考sys_user的id',
  `realname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `teacher_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工号',
  `xueli` int NULL DEFAULT NULL COMMENT '学历',
  `xuewei` int NULL DEFAULT NULL COMMENT '学位',
  `jianjie` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简介',
  `zhuanye` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `idcard` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片',
  `isXiaowai` int NULL DEFAULT NULL,
  `college` int NULL DEFAULT NULL,
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '团队名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '团队描述',
  `competition_id` bigint NULL DEFAULT NULL COMMENT '关联竞赛ID（可选）',
  `leader_id` bigint NOT NULL COMMENT '队长ID',
  `advisor_id` bigint NULL DEFAULT NULL COMMENT '指导老师ID',
  `max_members` int NOT NULL DEFAULT 5 COMMENT '最大成员数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/DISBANDED）',
  `invite_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邀请码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_leader_id`(`leader_id` ASC) USING BTREE,
  INDEX `idx_advisor_id`(`advisor_id` ASC) USING BTREE,
  INDEX `idx_competition_id`(`competition_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '团队表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (1, '001', '666', NULL, 17, 12, 5, 'ACTIVE', '40D3C34B', '2026-04-22 17:55:40', '2026-04-22 17:55:40', 0);
INSERT INTO `team` VALUES (4, '002', '2', NULL, 17, NULL, 5, 'ACTIVE', '6058C3B2', '2026-04-22 21:49:32', '2026-04-22 21:49:32', 0);

-- ----------------------------
-- Table structure for team_advisor
-- ----------------------------
DROP TABLE IF EXISTS `team_advisor`;
CREATE TABLE `team_advisor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_team_advisor`(`team_id` ASC, `advisor_id` ASC) USING BTREE,
  INDEX `idx_advisor_id`(`advisor_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '团队指导老师关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_advisor
-- ----------------------------
INSERT INTO `team_advisor` VALUES (18, 4, 19, '2026-04-22 21:49:51', 0);
INSERT INTO `team_advisor` VALUES (19, 4, 12, '2026-04-22 21:49:51', 0);
INSERT INTO `team_advisor` VALUES (22, 1, 12, '2026-04-22 22:06:01', 0);

-- ----------------------------
-- Table structure for team_member
-- ----------------------------
DROP TABLE IF EXISTS `team_member`;
CREATE TABLE `team_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成员记录ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEMBER' COMMENT '角色（LEADER/MEMBER）',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_team_user`(`team_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '团队成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_member
-- ----------------------------
INSERT INTO `team_member` VALUES (1, 1, 17, 'LEADER', '2026-04-22 17:55:41', 0);
INSERT INTO `team_member` VALUES (2, 1, 8, 'MEMBER', '2026-04-22 18:08:07', 0);
INSERT INTO `team_member` VALUES (5, 4, 17, 'LEADER', '2026-04-22 21:49:32', 0);
INSERT INTO `team_member` VALUES (6, 4, 8, 'MEMBER', '2026-04-22 22:19:05', 0);

-- ----------------------------
-- Table structure for team_message
-- ----------------------------
DROP TABLE IF EXISTS `team_message`;
CREATE TABLE `team_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '发送者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TEXT' COMMENT '消息类型（TEXT/SYSTEM）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_team_id`(`team_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '团队消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_message
-- ----------------------------
INSERT INTO `team_message` VALUES (1, 1, 17, '1', 'TEXT', '2026-04-22 18:19:23', 0);

-- ----------------------------
-- Table structure for team_task
-- ----------------------------
DROP TABLE IF EXISTS `team_task`;
CREATE TABLE `team_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '任务描述',
  `assignee_id` bigint NULL DEFAULT NULL COMMENT '负责人ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/IN_PROGRESS/COMPLETED）',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级（LOW/MEDIUM/HIGH）',
  `due_date` datetime NULL DEFAULT NULL COMMENT '截止日期',
  `created_by` bigint NOT NULL COMMENT '创建人ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_team_id`(`team_id` ASC) USING BTREE,
  INDEX `idx_assignee_id`(`assignee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '团队任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_task
-- ----------------------------

-- ----------------------------
-- Table structure for todo
-- ----------------------------
DROP TABLE IF EXISTS `todo`;
CREATE TABLE `todo`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '待办ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '待办描述',
  `due_date` datetime NULL DEFAULT NULL COMMENT '截止日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/COMPLETED）',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级（LOW/MEDIUM/HIGH）',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERSONAL' COMMENT '类型（PERSONAL/TEAM/APPOINTMENT）',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联ID（如预约ID、团队任务ID）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_due_date`(`due_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '个人待办表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of todo
-- ----------------------------

-- ----------------------------
-- Table structure for work
-- ----------------------------
DROP TABLE IF EXISTS `work`;
CREATE TABLE `work`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作品 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '作者 ID',
  `registration_id` bigint NOT NULL COMMENT '报名 ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '作品描述',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品文件 URL',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件原名',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态（SUBMITTED/REVIEWED）',
  `avg_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '平均分数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_competition_id`(`competition_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_registration_id`(`registration_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '作品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work
-- ----------------------------
INSERT INTO `work` VALUES (1, 3, 17, 2, '智能推荐系统', '基于深度学习的推荐系统', 'https://teaching-competition-files.https://oss-cn-hangzhou.aliyuncs.com/works/20260330/208aa319-1fff-459f-8cb5-a43e770e9647.cpp', 'homework1.cpp', 'SUBMITTED', NULL, '2026-03-30 15:54:23', '2026-03-30 15:59:40', 1);
INSERT INTO `work` VALUES (2, 3, 17, 2, '智能推荐系统', '基于深度学习的推荐系统', 'https://teaching-competition-files.oss-cn-hangzhou.aliyuncs.com/works/20260330/ee169048-6171-4d03-a099-312f0895ae51.cpp', '万年历.cpp', 'SUBMITTED', 83.00, '2026-03-30 15:54:51', '2026-03-30 15:54:51', 0);
INSERT INTO `work` VALUES (3, 3, 17, 2, '测试', '测定为', 'https://teaching-competition-files.oss-cn-hangzhou.aliyuncs.com/works/20260330/f915b000-cf23-477c-8e75-082147d266cf.png', '赖涵软件237班0233996.png', 'SUBMITTED', NULL, '2026-03-30 16:47:55', '2026-03-30 16:48:48', 1);
INSERT INTO `work` VALUES (4, 3, 17, 2, 'の我特特', '2', 'https://teaching-competition-files.oss-cn-hangzhou.aliyuncs.com/works/20260330/78ac66a3-0cd5-4fca-b996-ef8377587210.png', '屏幕截图 2024-04-07 194929.png', 'SUBMITTED', NULL, '2026-03-30 17:12:18', '2026-03-30 17:12:50', 1);

SET FOREIGN_KEY_CHECKS = 1;
