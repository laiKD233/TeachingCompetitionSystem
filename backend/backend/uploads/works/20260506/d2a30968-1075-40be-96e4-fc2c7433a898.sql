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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='获奖表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `award`
--

LOCK TABLES `award` WRITE;
/*!40000 ALTER TABLE `award` DISABLE KEYS */;
INSERT INTO `award` VALUES (1,3,17,2,'特等奖',NULL,'2026-04-01 20:31:01',0);
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `competition`
--

LOCK TABLES `competition` WRITE;
/*!40000 ALTER TABLE `competition` DISABLE KEYS */;
INSERT INTO `competition` VALUES (1,'2026 年大学生程序设计竞赛aa','程序设计','创新引领未来','面向全校学生的程序设计竞赛',NULL,'PUBLISHED','2026-03-01 00:00:00','2026-03-30 23:59:59','2026-05-15 23:59:59',NULL,NULL,NULL,NULL,NULL,'参赛规则说明...','提交要求说明...',4,'2026-03-19 22:20:54','2026-03-27 20:51:34',1),(2,'2026 年大学生程序设计竞赛bb','程序设计','创新引领未来','面向全校学生的程序设计竞赛',NULL,'PUBLISHED','2026-03-01 00:00:00','2026-03-30 23:59:59','2026-05-15 23:59:59',NULL,NULL,NULL,NULL,NULL,'参赛规则说明...','提交要求说明...',4,'2026-03-19 22:21:50','2026-03-27 20:51:31',1),(3,'2026 年大学生程序设计竞赛','程序设计','创新引领未来','面向全校学生的程序设计竞赛','/api/file/download/1774617627646_11166736851300.jpg','ANNOUNCED','2026-03-01 00:00:00','2026-03-30 23:59:59','2026-05-15 23:59:59','2026-03-27 21:05:17','2026-04-01 00:00:00',NULL,NULL,NULL,'参赛规则说明...','提交要求说明...',4,'2026-03-19 22:22:02','2026-03-26 21:02:41',0),(4,'2026大学生数学建模大赛','数学建模','数学建模大赛','建模',NULL,'REGISTRATION','2026-03-25 00:00:00','2026-03-26 00:00:00','2026-04-01 00:00:00','2026-04-03 00:00:00','2026-04-08 00:00:00',NULL,NULL,NULL,'','建模要',4,'2026-03-27 21:09:11','2026-03-27 21:09:11',0);
/*!40000 ALTER TABLE `competition` ENABLE KEYS */;
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
  `operation` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作描述',
  `module` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP 地址',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
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
  `project_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `advisor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '指导教师',
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
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration`
--

LOCK TABLES `registration` WRITE;
/*!40000 ALTER TABLE `registration` DISABLE KEYS */;
INSERT INTO `registration` VALUES (1,4,17,'建模项目A','暂无','关于A的数学建模','REJECTED','不合格',12,'2026-03-27 21:29:01','2026-03-27 21:24:00','2026-03-27 21:24:00',0),(2,3,17,'十七届蓝桥杯竞赛c++','陈爱国','程序设计竞赛','APPROVED',NULL,12,'2026-03-27 21:38:28','2026-03-27 21:38:11','2026-03-27 21:38:11',0);
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_task`
--

LOCK TABLES `review_task` WRITE;
/*!40000 ALTER TABLE `review_task` DISABLE KEYS */;
INSERT INTO `review_task` VALUES (1,3,2,1,NULL,NULL,'PENDING','2026-03-31 21:18:31','2026-03-31 21:18:31',0),(2,3,2,4,86.00,'还行','COMPLETED','2026-04-01 19:45:58','2026-04-01 19:45:58',0),(3,3,2,12,80.00,'好','COMPLETED','2026-04-01 19:46:12','2026-04-01 19:46:12',0);
/*!40000 ALTER TABLE `review_task` ENABLE KEYS */;
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
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STUDENT' COMMENT '角色（ADMIN/TEACHER/STUDENT）',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态（0-禁用 1-正常）',
  `permissions` text COLLATE utf8mb4_unicode_ci COMMENT '权限列表（JSON 格式）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标记（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (4,'admin','$2a$10$8eg5IioZmOtmaegigbElGuWcueFmsfj2XEUGNm5IScCU80SoLs2na','系统管理员',NULL,NULL,NULL,'15968853348','admin@163.com',NULL,'ADMIN',1,NULL,'2026-03-19 16:37:15','2026-03-19 21:52:00',0),(5,'teacher1','$2a$10$xqnDYQBgtDJBAmg6n8WVlufwcxZFuoKoKpk54Es6Etj4KAHSIuwhm','老师1',NULL,NULL,NULL,NULL,NULL,NULL,'TEACHER',1,NULL,'2026-03-19 16:37:15','2026-03-19 22:27:41',1),(6,'student1','$2a$10$xqnDYQBgtDJBAmg6n8WVlufwcxZFuoKoKpk54Es6Etj4KAHSIuwhm','学生用户1',NULL,NULL,NULL,NULL,NULL,NULL,'STUDENT',1,NULL,'2026-03-19 16:37:15','2026-03-19 22:25:31',1),(8,'xie','$2a$10$xqnDYQBgtDJBAmg6n8WVlufwcxZFuoKoKpk54Es6Etj4KAHSIuwhm','谢仁海','2026001','计算机学院',NULL,'13800138000','student1@example.com',NULL,'STUDENT',1,NULL,'2026-03-19 18:13:31','2026-03-19 21:45:55',0),(10,'zensen','$2a$10$NXQPWj/4f08dsNu3XoJ9duOyLZWmXOiwdSLv3VK8EHbYcnQbAZ3nG','李四','2026002','软件学院','软件工程 2101','13900139000','lisi@example.com',NULL,'STUDENT',1,NULL,'2026-03-19 18:16:18','2026-03-27 21:46:27',1),(11,'lai','$2a$10$G90LpYRnVsyFViBAbethueUzRYT8ekej8iMatga8e3Vzme4p6yEOS','赖涵','2026003','软件学院',NULL,'18460347716','3167269314@qq.com',NULL,'STUDENT',1,NULL,'2026-03-19 21:47:39','2026-03-19 22:24:44',1),(12,'caozh','$2a$10$/1KqrRV2d3FRWLKLXYpuoe1KlkyOEkL0OMXwNWNTiKsCsFK2osVwG','曹老师',NULL,NULL,NULL,'13588694457','cao@163.com',NULL,'TEACHER',1,NULL,'2026-03-19 22:26:46','2026-03-19 22:26:46',0),(17,'tang','$2a$10$G90LpYRnVsyFViBAbethueUzRYT8ekej8iMatga8e3Vzme4p6yEOS','张三','2026008','软件学院',NULL,'18460347716','3167269314@qq.com',NULL,'STUDENT',1,NULL,'2026-03-21 14:33:12','2026-03-21 14:36:01',0),(18,'liu','$2a$10$P0YO39nA9T2q365jv27Z1.tuSC4TQC38tExxineTiakbyLVhNF47m','刘强','2202303998','物联网学院',NULL,'15290876653','awdwdw@qq.com',NULL,'STUDENT',1,NULL,'2026-03-30 16:15:21','2026-03-30 16:15:21',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work`
--

LOCK TABLES `work` WRITE;
/*!40000 ALTER TABLE `work` DISABLE KEYS */;
INSERT INTO `work` VALUES (1,3,17,2,'智能推荐系统','基于深度学习的推荐系统','https://teaching-competition-files.https://oss-cn-hangzhou.aliyuncs.com/works/20260330/208aa319-1fff-459f-8cb5-a43e770e9647.cpp','homework1.cpp','SUBMITTED',NULL,'2026-03-30 15:54:23','2026-03-30 15:59:40',1),(2,3,17,2,'智能推荐系统','基于深度学习的推荐系统','https://teaching-competition-files.oss-cn-hangzhou.aliyuncs.com/works/20260330/ee169048-6171-4d03-a099-312f0895ae51.cpp','万年历.cpp','SUBMITTED',83.00,'2026-03-30 15:54:51','2026-03-30 15:54:51',0),(3,3,17,2,'测试','测定为','https://teaching-competition-files.oss-cn-hangzhou.aliyuncs.com/works/20260330/f915b000-cf23-477c-8e75-082147d266cf.png','赖涵软件237班0233996.png','SUBMITTED',NULL,'2026-03-30 16:47:55','2026-03-30 16:48:48',1),(4,3,17,2,'の我特特','2','https://teaching-competition-files.oss-cn-hangzhou.aliyuncs.com/works/20260330/78ac66a3-0cd5-4fca-b996-ef8377587210.png','屏幕截图 2024-04-07 194929.png','SUBMITTED',NULL,'2026-03-30 17:12:18','2026-03-30 17:12:50',1);
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

-- Dump completed on 2026-04-21 22:04:12
