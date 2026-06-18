-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: teaching_competition
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约主题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '预约说明',
  `appointment_date` datetime NOT NULL COMMENT '预约时间',
  `duration` int NOT NULL DEFAULT '60' COMMENT '时长（分钟）',
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地点',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED/COMPLETED/CANCELLED）',
  `reject_reason` text COLLATE utf8mb4_unicode_ci COMMENT '拒绝原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_advisor_id` (`advisor_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `award`
--

DROP TABLE IF EXISTS `award`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `award` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '获奖 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `work_id` bigint NOT NULL COMMENT '作品 ID',
  `award_level` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '奖项等级（一等奖/二等奖/三等奖/优秀奖）',
  `certificate_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '证书 URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_work_id` (`work_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='获奖表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `award`
--

LOCK TABLES `award` WRITE;
/*!40000 ALTER TABLE `award` DISABLE KEYS */;
/*!40000 ALTER TABLE `award` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `competition`
--

DROP TABLE IF EXISTS `competition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `competition` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '竞赛 ID',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '竞赛名称',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '竞赛类型',
  `participation_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INDIVIDUAL' COMMENT '参赛方式（INDIVIDUAL-个人赛/TEAM-团队赛）',
  `theme` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '竞赛主题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '竞赛描述',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片 URL',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态（DRAFT/PUBLISHED/REGISTRATION/ONGOING/REVIEWED/ANNOUNCED/ENDED）',
  `registration_start` datetime DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` datetime DEFAULT NULL COMMENT '报名截止时间',
  `submission_deadline` datetime DEFAULT NULL COMMENT '作品提交截止',
  `review_start` datetime DEFAULT NULL COMMENT '评审开始时间',
  `review_end` datetime DEFAULT NULL COMMENT '评审结束时间',
  `announcement_start` datetime DEFAULT NULL COMMENT '结果公示开始时间',
  `announcement_end` datetime DEFAULT NULL COMMENT '结果公示结束时间',
  `awards_config` text COLLATE utf8mb4_unicode_ci COMMENT '奖项配置（JSON 格式）',
  `rules` text COLLATE utf8mb4_unicode_ci COMMENT '竞赛规则',
  `submission_requirements` text COLLATE utf8mb4_unicode_ci COMMENT '提交要求',
  `created_by` bigint NOT NULL COMMENT '创建人 ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `competition`
--

LOCK TABLES `competition` WRITE;
/*!40000 ALTER TABLE `competition` DISABLE KEYS */;
INSERT INTO `competition` (`id`, `name`, `type`, `participation_type`, `theme`, `description`, `cover_image`, `status`, `registration_start`, `registration_end`, `submission_deadline`, `review_start`, `review_end`, `announcement_start`, `announcement_end`, `awards_config`, `rules`, `submission_requirements`, `created_by`, `created_at`, `updated_at`, `deleted`) VALUES (1,'java程序竞赛','程序设计','INDIVIDUAL','','',NULL,'REGISTRATION',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','',2,'2026-04-26 19:02:20','2026-04-26 19:02:20',0),(2,'大学生创新创业团队赛','创新创业','TEAM','','',NULL,'REGISTRATION',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','',1,'2026-04-26 19:04:04','2026-04-26 19:04:04',0);
/*!40000 ALTER TABLE `competition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SYSTEM' COMMENT '类型（SYSTEM/REGISTRATION/REVIEW/APPOINTMENT/TEAM）',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读（0-未读 1-已读）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` (`id`, `user_id`, `title`, `content`, `type`, `related_id`, `is_read`, `created_at`) VALUES (1,4,'报名审核通过','您对「java程序竞赛」的报名已通过审核','REGISTRATION',2,0,'2026-04-26 19:18:06'),(2,3,'报名审核通过','您对「大学生创新创业团队赛」的报名已通过审核','REGISTRATION',1,0,'2026-04-26 19:18:09'),(3,4,'报名审核通过','您对「大学生创新创业团队赛」的报名已通过审核','REGISTRATION',3,0,'2026-04-26 20:41:42'),(4,6,'报名审核通过','您对「大学生创新创业团队赛」的报名已通过审核','REGISTRATION',4,0,'2026-04-26 20:46:44');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户角色',
  `operation` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作描述',
  `module` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '操作内容',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP 地址',
  `result` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作结果',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` (`id`, `user_id`, `username`, `role`, `operation`, `module`, `content`, `ip`, `result`, `created_at`) VALUES (1,2,'teacher1','TEACHER','创建竞赛','竞赛管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:02:20'),(2,2,'teacher1','TEACHER','更新竞赛','竞赛管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:02:58'),(3,1,'admin','ADMIN','创建竞赛','竞赛管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:04:04'),(4,1,'admin','ADMIN','更新竞赛','竞赛管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:04:25'),(5,3,'student1','STUDENT','提交报名','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:13:36'),(6,4,'tang','STUDENT','提交报名','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:15:31'),(7,1,'admin','ADMIN','通过报名审核','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:18:06'),(8,1,'admin','ADMIN','通过报名审核','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:18:09'),(9,4,'tang','STUDENT','提交报名','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 19:34:39'),(10,3,'student1','STUDENT','提交报名','报名管理',NULL,'127.0.0.1','FAIL: 您已报名该竞赛，请勿重复报名','2026-04-26 20:19:18'),(11,1,'admin','ADMIN','通过报名审核','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 20:41:42'),(12,6,'wang','STUDENT','提交报名','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 20:45:57'),(13,1,'admin','ADMIN','通过报名审核','报名管理',NULL,'127.0.0.1','SUCCESS','2026-04-26 20:46:44');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration`
--

DROP TABLE IF EXISTS `registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `project_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目名称',
  `advisor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '指导教师',
  `participation_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INDIVIDUAL' COMMENT '参赛方式',
  `team_id` bigint DEFAULT NULL COMMENT '团队ID',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '项目描述',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED）',
  `reject_reason` text COLLATE utf8mb4_unicode_ci COMMENT '拒绝原因',
  `reviewed_by` bigint DEFAULT NULL COMMENT '审核人 ID',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_team_id` (`team_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration`
--

LOCK TABLES `registration` WRITE;
/*!40000 ALTER TABLE `registration` DISABLE KEYS */;
INSERT INTO `registration` (`id`, `competition_id`, `user_id`, `project_name`, `advisor`, `participation_type`, `team_id`, `description`, `status`, `reject_reason`, `reviewed_by`, `reviewed_at`, `created_at`, `updated_at`, `deleted`) VALUES (1,2,3,'基于。。。的创业项目','','TEAM',NULL,'','APPROVED',NULL,1,'2026-04-26 19:18:09','2026-04-26 19:13:36','2026-04-26 20:28:20',0),(2,1,4,'java程序a','','INDIVIDUAL',NULL,'','APPROVED',NULL,1,'2026-04-26 19:18:06','2026-04-26 19:15:31','2026-04-26 19:15:31',0),(3,2,4,'团队','','TEAM',NULL,'','APPROVED',NULL,1,'2026-04-26 20:41:42','2026-04-26 19:34:39','2026-04-26 20:28:20',0),(4,2,6,'大学生创新创业团队赛 - 团队参赛',NULL,'TEAM',1,NULL,'APPROVED',NULL,1,'2026-04-26 20:46:44','2026-04-26 20:45:57','2026-04-26 20:45:57',0);
/*!40000 ALTER TABLE `registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_task`
--

DROP TABLE IF EXISTS `review_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评审任务 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `work_id` bigint NOT NULL COMMENT '作品 ID',
  `reviewer_id` bigint NOT NULL COMMENT '评审人 ID',
  `score` decimal(5,2) DEFAULT NULL COMMENT '评分',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '评审意见',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/COMPLETED）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_work_id` (`work_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_task`
--

LOCK TABLES `review_task` WRITE;
/*!40000 ALTER TABLE `review_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `review_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日程标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '日程描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地点',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERSONAL' COMMENT '类型（PERSONAL/APPOINTMENT/MEETING）',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID（如预约ID）',
  `color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '颜色标记',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` (`id`, `user_id`, `title`, `description`, `start_time`, `end_time`, `location`, `type`, `related_id`, `color`, `created_at`, `updated_at`, `deleted`) VALUES (1,2,'创建竞赛','创建个人赛和团队赛','2026-04-26 00:00:00','2026-05-01 00:00:00','江西财经大学','PERSONAL',NULL,'#409eff','2026-04-26 18:32:10','2026-04-26 18:32:10',0),(2,2,'查看报名情况','查看报名情况','2026-04-30 00:00:00','2026-05-01 00:00:00','江西财经大学办公室','PERSONAL',NULL,'#409eff','2026-04-26 18:33:21','2026-04-26 18:33:21',0),(3,1,'测试','','2026-04-26 00:00:00','2026-04-28 00:00:00','','PERSONAL',NULL,'#409eff','2026-04-26 19:19:10','2026-04-26 19:19:10',0);
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（登录账号）',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `student_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学号',
  `college` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学院',
  `class_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '班级',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像 URL',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STUDENT' COMMENT '角色（ADMIN/TEACHER/STUDENT/ADVISOR）',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态（0-禁用 1-正常）',
  `permissions` text COLLATE utf8mb4_unicode_ci COMMENT '权限列表（JSON 格式）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` (`id`, `username`, `password`, `name`, `student_id`, `college`, `class_name`, `phone`, `email`, `avatar`, `role`, `status`, `permissions`, `created_at`, `updated_at`, `deleted`) VALUES (1,'admin','$2b$10$9QM1aa3Z7L3qKEDyviz1TuHxbFH0cNkLNl6OmSqBOZWYDO3dnhgK2','系统管理员',NULL,NULL,NULL,NULL,NULL,NULL,'ADMIN',1,'[\"*\"]','2026-04-26 17:12:09','2026-04-26 17:49:38',0),(2,'teacher1','$2b$10$U0GmzfvKuCY24H6VQSc2he82p3U.tFVVgJR3lxbJiko0.2aEfLJ3m','教师用户',NULL,NULL,NULL,NULL,NULL,NULL,'TEACHER',1,'[\"competition:*\",\"registration:review\",\"work:review\"]','2026-04-26 17:12:09','2026-04-26 17:49:38',0),(3,'student1','$2b$10$U0GmzfvKuCY24H6VQSc2he82p3U.tFVVgJR3lxbJiko0.2aEfLJ3m','学生用户',NULL,NULL,NULL,NULL,NULL,NULL,'STUDENT',1,'[\"competition:view\",\"registration:create\",\"work:submit\"]','2026-04-26 17:12:09','2026-04-26 17:49:38',0),(4,'tang','$2a$10$a3ZYxhgVhEQGmkw/bmSc9uLXAA7jqgiaRtOs5ZTBgxicFIJbdZbO6','唐三','2202303996','软件学院',NULL,'18236515669','adw4@qq.com',NULL,'STUDENT',1,NULL,'2026-04-26 19:15:01','2026-04-26 19:15:01',0),(5,'lai','$2a$10$QSIG0Rqgn.XpwULNG8CKYO9jLvvTLSUMdm4jPgwd5ZAOQ5mo8bADC','李四','2202362255','软件学院',NULL,'18460343654','dwadw@qq.com',NULL,'STUDENT',1,NULL,'2026-04-26 19:55:37','2026-04-26 19:55:37',0),(6,'wang','$2a$10$80GQJBaBs4083RUPm91HXekqm3jx/W4kQnbPFPzaJRhE5fMgPvCGe','王五','2235647895','软件学院',NULL,'18460354601','adwddwdw@qq.com',NULL,'STUDENT',1,NULL,'2026-04-26 20:45:43','2026-04-26 20:45:43',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '团队名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '团队描述',
  `competition_id` bigint DEFAULT NULL COMMENT '关联竞赛ID（可选）',
  `leader_id` bigint NOT NULL COMMENT '队长ID',
  `advisor_id` bigint DEFAULT NULL COMMENT '指导老师ID',
  `max_members` int NOT NULL DEFAULT '5' COMMENT '最大成员数',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/DISBANDED）',
  `invite_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邀请码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_leader_id` (`leader_id`),
  KEY `idx_advisor_id` (`advisor_id`),
  KEY `idx_competition_id` (`competition_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` (`id`, `name`, `description`, `competition_id`, `leader_id`, `advisor_id`, `max_members`, `status`, `invite_code`, `created_at`, `updated_at`, `deleted`) VALUES (1,'测试团队a','',2,3,NULL,5,'ACTIVE','FB0AE354','2026-04-26 19:13:36','2026-04-26 19:13:36',0);
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_advisor`
--

DROP TABLE IF EXISTS `team_advisor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_advisor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `advisor_id` bigint NOT NULL COMMENT '指导老师ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_advisor` (`team_id`,`advisor_id`),
  KEY `idx_advisor_id` (`advisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队指导老师关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_advisor`
--

LOCK TABLES `team_advisor` WRITE;
/*!40000 ALTER TABLE `team_advisor` DISABLE KEYS */;
/*!40000 ALTER TABLE `team_advisor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_member`
--

DROP TABLE IF EXISTS `team_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成员记录ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEMBER' COMMENT '角色（LEADER/MEMBER）',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_user` (`team_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_member`
--

LOCK TABLES `team_member` WRITE;
/*!40000 ALTER TABLE `team_member` DISABLE KEYS */;
INSERT INTO `team_member` (`id`, `team_id`, `user_id`, `role`, `join_time`, `deleted`) VALUES (1,1,3,'LEADER','2026-04-26 19:13:36',0),(2,1,4,'MEMBER','2026-04-26 19:34:38',0),(3,1,5,'MEMBER','2026-04-26 19:56:03',1),(5,1,6,'MEMBER','2026-04-26 20:45:57',0);
/*!40000 ALTER TABLE `team_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_message`
--

DROP TABLE IF EXISTS `team_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '发送者ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TEXT' COMMENT '消息类型（TEXT/SYSTEM）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_message`
--

LOCK TABLES `team_message` WRITE;
/*!40000 ALTER TABLE `team_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `team_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_task`
--

DROP TABLE IF EXISTS `team_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '任务描述',
  `assignee_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/IN_PROGRESS/COMPLETED）',
  `priority` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级（LOW/MEDIUM/HIGH）',
  `due_date` datetime DEFAULT NULL COMMENT '截止日期',
  `created_by` bigint NOT NULL COMMENT '创建人ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_assignee_id` (`assignee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_task`
--

LOCK TABLES `team_task` WRITE;
/*!40000 ALTER TABLE `team_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `team_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `todo`
--

DROP TABLE IF EXISTS `todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `todo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '待办ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '待办描述',
  `due_date` datetime DEFAULT NULL COMMENT '截止日期',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/COMPLETED）',
  `priority` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级（LOW/MEDIUM/HIGH）',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERSONAL' COMMENT '类型（PERSONAL/TEAM/APPOINTMENT）',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID（如预约ID、团队任务ID）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='个人待办表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `todo`
--

LOCK TABLES `todo` WRITE;
/*!40000 ALTER TABLE `todo` DISABLE KEYS */;
/*!40000 ALTER TABLE `todo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work`
--

DROP TABLE IF EXISTS `work`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作品 ID',
  `competition_id` bigint NOT NULL COMMENT '竞赛 ID',
  `user_id` bigint NOT NULL COMMENT '作者 ID',
  `registration_id` bigint NOT NULL COMMENT '报名 ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '作品描述',
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品文件 URL',
  `file_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件原名',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态（SUBMITTED/REVIEWED）',
  `avg_score` decimal(5,2) DEFAULT NULL COMMENT '平均分数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_competition_id` (`competition_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_registration_id` (`registration_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work`
--

LOCK TABLES `work` WRITE;
/*!40000 ALTER TABLE `work` DISABLE KEYS */;
/*!40000 ALTER TABLE `work` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-26 20:48:42
