-- 江西财经大学学科竞赛管理系统正式初始化数据
-- 保留系统管理员 admin，重建其他用户与全部业务数据。
-- 用法：sudo mysql teaching_competition < deploy/sql/reset_jxufe_formal_data.sql

SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;

SET @admin_id := (SELECT id FROM sys_user WHERE username = 'admin' LIMIT 1);
SET @default_pwd := (SELECT password FROM sys_user WHERE username = 'admin' LIMIT 1);
SET @now := NOW();

DELETE FROM award;
DELETE FROM review_task;
DELETE FROM work;
DELETE FROM registration;
DELETE FROM team_message;
DELETE FROM team_task;
DELETE FROM team_advisor_audit;
DELETE FROM team_advisor;
DELETE FROM team_member;
DELETE FROM team;
DELETE FROM appointment;
DELETE FROM schedule;
DELETE FROM todo;
DELETE FROM notification;
DELETE FROM operation_log;
DELETE FROM competition_admin;
DELETE FROM track;
DELETE FROM competition;
DELETE FROM teacher;
DELETE FROM student;
DELETE FROM sys_user WHERE username <> 'admin';

ALTER TABLE sys_user AUTO_INCREMENT = 1000;
ALTER TABLE competition AUTO_INCREMENT = 1000;
ALTER TABLE track AUTO_INCREMENT = 1000;
ALTER TABLE team AUTO_INCREMENT = 1000;
ALTER TABLE registration AUTO_INCREMENT = 1000;
ALTER TABLE work AUTO_INCREMENT = 1000;
ALTER TABLE review_task AUTO_INCREMENT = 1000;
ALTER TABLE award AUTO_INCREMENT = 1000;
ALTER TABLE appointment AUTO_INCREMENT = 1000;
ALTER TABLE schedule AUTO_INCREMENT = 1000;
ALTER TABLE todo AUTO_INCREMENT = 1000;
ALTER TABLE notification AUTO_INCREMENT = 1000;
ALTER TABLE operation_log AUTO_INCREMENT = 1000;
ALTER TABLE competition_admin AUTO_INCREMENT = 1000;
ALTER TABLE teacher AUTO_INCREMENT = 1000;
ALTER TABLE student AUTO_INCREMENT = 1000;
ALTER TABLE team_advisor AUTO_INCREMENT = 1000;
ALTER TABLE team_advisor_audit AUTO_INCREMENT = 1000;
ALTER TABLE team_member AUTO_INCREMENT = 1000;
ALTER TABLE team_message AUTO_INCREMENT = 1000;
ALTER TABLE team_task AUTO_INCREMENT = 1000;

INSERT INTO sys_user
(id, username, password, name, student_id, college, class_name, phone, email, avatar, role, status, permissions, created_at, updated_at, deleted)
VALUES
(101, 'teacher_lqs', @default_pwd, '刘青山', NULL, '软件与物联网工程学院', NULL, '13807910001', 'lqs@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(102, 'teacher_cmz', @default_pwd, '陈明珠', NULL, '软件与物联网工程学院', NULL, '13807910002', 'cmz@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(103, 'teacher_wyb', @default_pwd, '王彦博', NULL, '统计与数据科学学院', NULL, '13807910003', 'wyb@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(104, 'teacher_ljn', @default_pwd, '李佳宁', NULL, '会计学院', NULL, '13807910004', 'ljn@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(105, 'teacher_zqh', @default_pwd, '周启航', NULL, '金融学院', NULL, '13807910005', 'zqh@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(106, 'teacher_lsy', @default_pwd, '罗思远', NULL, '工商管理学院', NULL, '13807910006', 'lsy@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(107, 'teacher_zyw', @default_pwd, '赵雅文', NULL, '艺术学院', NULL, '13807910007', 'zyw@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(108, 'teacher_hy', @default_pwd, '何远', NULL, '软件与物联网工程学院', NULL, '13807910008', 'hy@jxufe.edu.cn', NULL, 'TEACHER', 1, NULL, @now, @now, 0),
(109, 'advisor_ly', @default_pwd, '廖远', NULL, '软件与物联网工程学院', NULL, '13807910009', 'ly@jxufe.edu.cn', NULL, 'ADVISOR', 1, NULL, @now, @now, 0),
(110, 'advisor_sx', @default_pwd, '苏晓', NULL, '金融学院', NULL, '13807910010', 'sx@jxufe.edu.cn', NULL, 'ADVISOR', 1, NULL, @now, @now, 0),
(111, 'advisor_fm', @default_pwd, '方敏', NULL, '工商管理学院', NULL, '13807910011', 'fm@jxufe.edu.cn', NULL, 'ADVISOR', 1, NULL, @now, @now, 0),
(112, 'advisor_gj', @default_pwd, '龚洁', NULL, '统计与数据科学学院', NULL, '13807910012', 'gj@jxufe.edu.cn', NULL, 'ADVISOR', 1, NULL, @now, @now, 0),
(201, 's2202304101', @default_pwd, '张子涵', '2202304101', '软件与物联网工程学院', '软件工程2201班', '13970010001', '2202304101@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(202, 's2202304102', @default_pwd, '李思源', '2202304102', '软件与物联网工程学院', '软件工程2201班', '13970010002', '2202304102@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(203, 's2202304103', @default_pwd, '陈雨桐', '2202304103', '软件与物联网工程学院', '软件工程2201班', '13970010003', '2202304103@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(204, 's2202304201', @default_pwd, '王景行', '2202304201', '统计与数据科学学院', '经济统计2201班', '13970010004', '2202304201@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(205, 's2202304202', @default_pwd, '刘若曦', '2202304202', '统计与数据科学学院', '经济统计2201班', '13970010005', '2202304202@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(206, 's2202304203', @default_pwd, '周明远', '2202304203', '统计与数据科学学院', '数据科学2201班', '13970010006', '2202304203@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(207, 's2202304301', @default_pwd, '黄一诺', '2202304301', '软件与物联网工程学院', '物联网工程2201班', '13970010007', '2202304301@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(208, 's2202304302', @default_pwd, '赵晨曦', '2202304302', '软件与物联网工程学院', '物联网工程2201班', '13970010008', '2202304302@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(209, 's2202304303', @default_pwd, '孙嘉懿', '2202304303', '软件与物联网工程学院', '物联网工程2201班', '13970010009', '2202304303@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(210, 's2202304401', @default_pwd, '吴昊然', '2202304401', '金融学院', '金融学2201班', '13970010010', '2202304401@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(211, 's2202304402', @default_pwd, '郑可欣', '2202304402', '金融学院', '金融科技2201班', '13970010011', '2202304402@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(212, 's2202304403', @default_pwd, '胡宇轩', '2202304403', '金融学院', '金融科技2201班', '13970010012', '2202304403@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(213, 's2202304501', @default_pwd, '郭语嫣', '2202304501', '会计学院', '会计学2201班', '13970010013', '2202304501@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(214, 's2202304502', @default_pwd, '唐嘉泽', '2202304502', '会计学院', '智能会计2201班', '13970010014', '2202304502@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(215, 's2202304503', @default_pwd, '宋清妍', '2202304503', '会计学院', '智能会计2201班', '13970010015', '2202304503@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(216, 's2202304601', @default_pwd, '余承安', '2202304601', '工商管理学院', '工商管理2201班', '13970010016', '2202304601@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(217, 's2202304602', @default_pwd, '许念慈', '2202304602', '工商管理学院', '市场营销2201班', '13970010017', '2202304602@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(218, 's2202304603', @default_pwd, '秦朗', '2202304603', '工商管理学院', '市场营销2201班', '13970010018', '2202304603@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(219, 's2202304701', @default_pwd, '谢知微', '2202304701', '国际经贸学院', '国际经济与贸易2201班', '13970010019', '2202304701@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(220, 's2202304702', @default_pwd, '邓星河', '2202304702', '国际经贸学院', '电子商务2201班', '13970010020', '2202304702@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(221, 's2202304703', @default_pwd, '蒋南乔', '2202304703', '国际经贸学院', '电子商务2201班', '13970010021', '2202304703@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(222, 's2202304801', @default_pwd, '熊予安', '2202304801', '软件与物联网工程学院', '网络空间安全2201班', '13970010022', '2202304801@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(223, 's2202304802', @default_pwd, '林清越', '2202304802', '软件与物联网工程学院', '网络空间安全2201班', '13970010023', '2202304802@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(224, 's2202304803', @default_pwd, '沈墨白', '2202304803', '软件与物联网工程学院', '网络空间安全2201班', '13970010024', '2202304803@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(225, 's2202304901', @default_pwd, '叶舒然', '2202304901', '艺术学院', '数字媒体艺术2201班', '13970010025', '2202304901@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(226, 's2202304902', @default_pwd, '程星予', '2202304902', '艺术学院', '数字媒体艺术2201班', '13970010026', '2202304902@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(227, 's2202304903', @default_pwd, '孟知夏', '2202304903', '艺术学院', '数字媒体艺术2201班', '13970010027', '2202304903@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0),
(228, 's2202305001', @default_pwd, '乔以宁', '2202305001', '人文与传播学院', '新闻学2201班', '13970010028', '2202305001@stu.jxufe.edu.cn', NULL, 'STUDENT', 1, NULL, @now, @now, 0);

INSERT INTO teacher
(id, user_id, real_name, teacher_no, education, degree, bio, major, id_card, photo, is_external, college_name, school, created_at, updated_at)
VALUES
(1, 101, '刘青山', 'JXUFE200801', 7, 3, '主要研究软件工程与竞赛实践教学。', '软件工程', '360102198001010011', NULL, 0, '软件与物联网工程学院', '江西财经大学', @now, @now),
(2, 102, '陈明珠', 'JXUFE201203', 7, 3, '主要研究人工智能与数据挖掘。', '人工智能', '360102198403020022', NULL, 0, '软件与物联网工程学院', '江西财经大学', @now, @now),
(3, 103, '王彦博', 'JXUFE201106', 7, 3, '长期指导数学建模与统计分析竞赛。', '统计学', '360102198206030033', NULL, 0, '统计与数据科学学院', '江西财经大学', @now, @now),
(4, 104, '李佳宁', 'JXUFE201508', 6, 2, '研究方向为智能财务与管理会计。', '会计学', '360102198507040044', NULL, 0, '会计学院', '江西财经大学', @now, @now),
(5, 105, '周启航', 'JXUFE201404', 7, 3, '关注金融科技、量化分析与风险管理。', '金融科技', '360102198308050055', NULL, 0, '金融学院', '江西财经大学', @now, @now),
(6, 106, '罗思远', 'JXUFE201009', 6, 2, '指导创新创业与商业计划竞赛。', '工商管理', '360102197912060066', NULL, 0, '工商管理学院', '江西财经大学', @now, @now),
(7, 107, '赵雅文', 'JXUFE201706', 6, 2, '研究交互设计与数字媒体创意。', '数字媒体艺术', '360102198811070077', NULL, 0, '艺术学院', '江西财经大学', @now, @now),
(8, 108, '何远', 'JXUFE201305', 7, 3, '研究网络安全、攻防演练与安全运维。', '网络空间安全', '360102198401080088', NULL, 0, '软件与物联网工程学院', '江西财经大学', @now, @now),
(9, 109, '廖远', 'JXUFE201809', 6, 2, '专职指导软件创新与工程实践类团队。', '软件工程', '360102198909090099', NULL, 0, '软件与物联网工程学院', '江西财经大学', @now, @now),
(10, 110, '苏晓', 'JXUFE201912', 6, 2, '专职指导财经数据分析与金融科技项目。', '金融科技', '360102199001100010', NULL, 0, '金融学院', '江西财经大学', @now, @now),
(11, 111, '方敏', 'JXUFE201708', 6, 2, '专职指导创业计划、路演表达与商业模式设计。', '工商管理', '360102198812110011', NULL, 0, '工商管理学院', '江西财经大学', @now, @now),
(12, 112, '龚洁', 'JXUFE201607', 7, 3, '专职指导数学建模与统计分析团队。', '统计学', '360102198711120012', NULL, 0, '统计与数据科学学院', '江西财经大学', @now, @now);

INSERT INTO student
(id, user_id, student_no, real_name, id_card, entry_year, class_name, major, college_name, photo, bio, is_external, school, created_at, updated_at)
SELECT id - 200, id, student_id, name, CONCAT('3601022003', LPAD(id, 6, '0'), '01'), 2022, class_name,
       CASE
         WHEN class_name LIKE '%软件工程%' THEN '软件工程'
         WHEN class_name LIKE '%物联网%' THEN '物联网工程'
         WHEN class_name LIKE '%经济统计%' THEN '经济统计学'
         WHEN class_name LIKE '%数据科学%' THEN '数据科学与大数据技术'
         WHEN class_name LIKE '%金融科技%' THEN '金融科技'
         WHEN class_name LIKE '%金融学%' THEN '金融学'
         WHEN class_name LIKE '%会计%' THEN '会计学'
         WHEN class_name LIKE '%工商管理%' THEN '工商管理'
         WHEN class_name LIKE '%市场营销%' THEN '市场营销'
         WHEN class_name LIKE '%国际经济%' THEN '国际经济与贸易'
         WHEN class_name LIKE '%电子商务%' THEN '电子商务'
         WHEN class_name LIKE '%网络空间安全%' THEN '网络空间安全'
         WHEN class_name LIKE '%数字媒体%' THEN '数字媒体艺术'
         ELSE '新闻学'
       END,
       college, NULL, '江西财经大学本科生，参与学科竞赛与项目实践。', 0, '江西财经大学', @now, @now
FROM sys_user
WHERE role = 'STUDENT';

INSERT INTO competition
(id, name, type, participation_type, theme, description, cover_image, status, registration_start, registration_end, submission_deadline, review_start, review_end, announcement_start, announcement_end, awards_config, rules, submission_requirements, created_by, created_at, updated_at, deleted)
VALUES
(301, '江西财经大学软件创新杯', '程序设计', 'INDIVIDUAL', '软件工程实践与校园服务创新', '面向全校学生征集具有实际应用价值的软件作品，重点考察需求分析、系统设计、编码质量与用户体验。', '/api/file/download/files/covers/software-cup.svg', 'REGISTRATION', '2026-06-01 08:00:00', '2026-06-25 23:59:59', '2026-07-05 23:59:59', '2026-07-06 08:00:00', '2026-07-12 18:00:00', '2026-07-13 08:00:00', '2026-07-20 18:00:00', NULL, '个人参赛，须提交可运行系统、源代码和设计说明。', '提交项目说明文档、源码压缩包和运行截图。', @admin_id, '2026-05-20 09:00:00', @now, 0),
(302, '物联网应用设计竞赛', '物联网', 'TEAM', '智慧校园感知与低碳管理', '围绕教学楼、图书馆、宿舍等校园场景设计物联网应用方案，鼓励硬件原型与数据可视化结合。', '/api/file/download/files/covers/iot-design.svg', 'SUBMISSION', '2026-05-01 08:00:00', '2026-05-25 23:59:59', '2026-06-20 23:59:59', '2026-06-21 08:00:00', '2026-06-28 18:00:00', '2026-06-29 08:00:00', '2026-07-05 18:00:00', NULL, '团队参赛，每队2-5人，可申请1-2名指导老师。', '提交方案书、原型照片、数据采集说明和展示视频。', @admin_id, '2026-04-20 09:00:00', @now, 0),
(303, '数学建模校内选拔赛', '数学建模', 'TEAM', '财经场景建模与决策优化', '选拔参加高水平数学建模竞赛的校内团队，题目聚焦财经决策、物流优化和风险预测。', '/api/file/download/files/covers/math-modeling.svg', 'FINISHED', '2026-04-01 08:00:00', '2026-04-20 23:59:59', '2026-05-05 23:59:59', '2026-05-06 08:00:00', '2026-05-15 18:00:00', '2026-05-16 08:00:00', '2026-05-23 18:00:00', NULL, '团队参赛，每队3人，作品需包含模型、代码和论文。', '提交建模论文、代码和结果附件。', @admin_id, '2026-03-20 09:00:00', @now, 0),
(304, '财经大数据分析挑战赛', '大数据分析', 'INDIVIDUAL', '财经数据洞察与风险识别', '基于公开财经数据开展数据清洗、特征构建、模型训练和可视化分析。', '/api/file/download/files/covers/finance-data.svg', 'FINISHED', '2026-03-01 08:00:00', '2026-03-20 23:59:59', '2026-04-10 23:59:59', '2026-04-11 08:00:00', '2026-04-20 18:00:00', '2026-04-21 08:00:00', '2026-04-30 18:00:00', NULL, '个人参赛，允许使用Python、R或SQL等工具。', '提交分析报告、数据处理脚本和可视化图表。', @admin_id, '2026-02-22 09:00:00', @now, 0),
(305, '人工智能应用创新赛', '人工智能', 'TEAM', 'AI赋能校园服务', '鼓励团队围绕教务咨询、学习支持、校园治理等场景构建AI应用原型。', '/api/file/download/files/covers/ai-innovation.svg', 'REGISTRATION', '2026-06-05 08:00:00', '2026-06-30 23:59:59', '2026-07-12 23:59:59', '2026-07-13 08:00:00', '2026-07-20 18:00:00', '2026-07-21 08:00:00', '2026-07-28 18:00:00', NULL, '团队参赛，每队2-5人，需说明数据来源与应用边界。', '提交项目计划书、原型地址、模型说明和答辩PPT。', @admin_id, '2026-05-25 09:00:00', @now, 0),
(306, '创新创业计划竞赛', '创新创业', 'TEAM', '财经特色创业项目孵化', '围绕财经科技、校园服务、数字商业等方向提交可落地商业计划。', '/api/file/download/files/covers/business-plan.svg', 'PUBLISHED', '2026-07-01 08:00:00', '2026-07-25 23:59:59', '2026-08-10 23:59:59', '2026-08-11 08:00:00', '2026-08-18 18:00:00', '2026-08-19 08:00:00', '2026-08-25 18:00:00', NULL, '团队参赛，鼓励跨学院组队。', '提交商业计划书、路演PPT和财务预测表。', @admin_id, '2026-06-01 09:00:00', @now, 0),
(307, '网络安全攻防竞赛', '网络安全', 'TEAM', '校园网络安全防护', '通过攻防靶场、日志分析和应急处置任务考察网络安全实践能力。', '/api/file/download/files/covers/cyber-security.svg', 'FINISHED', '2026-01-10 08:00:00', '2026-01-25 23:59:59', '2026-02-10 23:59:59', '2026-02-11 08:00:00', '2026-02-18 18:00:00', '2026-02-19 08:00:00', '2026-02-28 18:00:00', NULL, '团队参赛，每队3-4人，严禁对非竞赛环境进行攻击。', '提交攻防报告、漏洞复现说明和修复建议。', @admin_id, '2026-01-02 09:00:00', @now, 0),
(308, '数字媒体创意设计赛', '数字媒体', 'INDIVIDUAL', '财经文化传播与交互设计', '围绕江西财经大学校园文化、财经知识传播和数字交互体验进行作品创作。', '/api/file/download/files/covers/digital-media.svg', 'DRAFT', '2026-09-01 08:00:00', '2026-09-25 23:59:59', '2026-10-10 23:59:59', '2026-10-11 08:00:00', '2026-10-18 18:00:00', '2026-10-19 08:00:00', '2026-10-25 18:00:00', NULL, '个人参赛，作品需保证原创性。', '提交设计说明、源文件和展示视频。', @admin_id, '2026-06-05 09:00:00', @now, 0);

INSERT INTO competition_admin (id, competition_id, admin_id, created_at, deleted)
VALUES
(1, 301, @admin_id, @now, 0), (2, 302, @admin_id, @now, 0), (3, 303, @admin_id, @now, 0), (4, 304, @admin_id, @now, 0),
(5, 305, @admin_id, @now, 0), (6, 306, @admin_id, @now, 0), (7, 307, @admin_id, @now, 0), (8, 308, @admin_id, @now, 0);

INSERT INTO track (id, competition_id, name, description, status, created_at, updated_at)
VALUES
(401, 301, 'Web应用开发', '面向校园服务的软件系统开发。', 1, @now, @now),
(402, 301, '移动应用开发', '移动端应用与小程序方向。', 1, @now, @now),
(403, 302, '智慧教室', '教学空间感知与设备联动。', 1, @now, @now),
(404, 302, '低碳校园', '能耗监测与节能控制。', 1, @now, @now),
(405, 303, '预测建模', '财经数据预测与模型解释。', 1, @now, @now),
(406, 303, '优化决策', '资源配置与运营优化。', 1, @now, @now),
(407, 304, '金融风控', '风险识别与信用评估。', 1, @now, @now),
(408, 304, '商业洞察', '用户行为与经营分析。', 1, @now, @now),
(409, 305, '智能问答', '校园问答与知识服务。', 1, @now, @now),
(410, 305, '智能办公', '办公流程自动化与辅助决策。', 1, @now, @now),
(411, 306, '财经科技创业', '财经科技产品与商业模式。', 1, @now, @now),
(412, 306, '校园服务创业', '面向校园生活服务创新。', 1, @now, @now),
(413, 307, 'Web安全', '漏洞识别、复现与加固。', 1, @now, @now),
(414, 307, '日志分析', '异常流量分析与应急响应。', 1, @now, @now),
(415, 308, '交互设计', '交互原型与用户体验。', 1, @now, @now),
(416, 308, '视觉传播', '视觉叙事与品牌传播。', 1, @now, @now);

INSERT INTO team
(id, name, description, competition_id, leader_id, advisor_id, max_members, status, invite_code, created_at, updated_at, deleted)
VALUES
(501, '江财物联感知队', '围绕教学楼能耗监测设计传感器采集与可视化系统。', 302, 201, 109, 5, 'ACTIVE', 'WLJX2026', '2026-05-04 10:00:00', @now, 0),
(502, '滕王阁建模队', '聚焦财经场景预测建模与优化决策。', 303, 204, 103, 3, 'ACTIVE', 'TWMX2026', '2026-04-05 10:00:00', @now, 0),
(503, '财智AI先锋队', '建设面向竞赛咨询的智能问答助手。', 305, 207, 109, 5, 'ACTIVE', 'AICZ2026', '2026-06-06 10:00:00', @now, 0),
(504, '云账本创业队', '面向小微企业数字记账与经营分析场景。', 306, 211, 111, 5, 'ACTIVE', 'YZBC2026', '2026-06-03 10:00:00', @now, 0),
(505, '蓝盾攻防队', '围绕Web安全与日志分析开展攻防训练。', 307, 222, 108, 4, 'ACTIVE', 'LDAF2026', '2026-01-12 10:00:00', @now, 0),
(506, '智能仓储队', '设计校内资产仓储环境感知与告警系统。', 302, 210, 101, 5, 'ACTIVE', 'ZNCC2026', '2026-05-05 10:00:00', @now, 0),
(507, '星火创业队', '面向校园二手交易与公益服务的创业计划。', 306, 219, 106, 5, 'DISBANDED', 'XHCY2026', '2026-05-10 10:00:00', @now, 0),
(508, '赣鄱智算队', '围绕物流调度和库存优化开展建模。', 303, 216, 103, 3, 'ACTIVE', 'GPZS2026', '2026-04-06 10:00:00', @now, 0),
(509, '产学协同指导组', '由教师发起的创业赛团队，带领学生完成财务预测模块。', 306, 101, 106, 5, 'ACTIVE', 'CXZD2026', '2026-06-07 10:00:00', @now, 0);

INSERT INTO team_member (id, team_id, user_id, role, join_time, deleted)
VALUES
(1, 501, 201, 'LEADER', '2026-05-04 10:00:00', 0), (2, 501, 202, 'MEMBER', '2026-05-04 11:00:00', 0), (3, 501, 203, 'MEMBER', '2026-05-04 11:10:00', 0),
(4, 502, 204, 'LEADER', '2026-04-05 10:00:00', 0), (5, 502, 205, 'MEMBER', '2026-04-05 11:00:00', 0), (6, 502, 206, 'MEMBER', '2026-04-05 11:20:00', 0),
(7, 503, 207, 'LEADER', '2026-06-06 10:00:00', 0), (8, 503, 208, 'MEMBER', '2026-06-06 11:00:00', 0), (9, 503, 209, 'MEMBER', '2026-06-06 11:20:00', 0),
(10, 504, 211, 'LEADER', '2026-06-03 10:00:00', 0), (11, 504, 212, 'MEMBER', '2026-06-03 11:00:00', 0), (12, 504, 213, 'MEMBER', '2026-06-03 11:20:00', 0),
(13, 505, 222, 'LEADER', '2026-01-12 10:00:00', 0), (14, 505, 223, 'MEMBER', '2026-01-12 11:00:00', 0), (15, 505, 224, 'MEMBER', '2026-01-12 11:20:00', 0),
(16, 506, 210, 'LEADER', '2026-05-05 10:00:00', 0), (17, 506, 214, 'MEMBER', '2026-05-05 11:00:00', 0), (18, 506, 215, 'MEMBER', '2026-05-05 11:20:00', 0),
(19, 507, 219, 'LEADER', '2026-05-10 10:00:00', 0), (20, 507, 220, 'MEMBER', '2026-05-10 11:00:00', 0),
(21, 508, 216, 'LEADER', '2026-04-06 10:00:00', 0), (22, 508, 217, 'MEMBER', '2026-04-06 11:00:00', 0), (23, 508, 218, 'MEMBER', '2026-04-06 11:20:00', 0),
(24, 509, 101, 'LEADER', '2026-06-07 10:00:00', 0), (25, 509, 225, 'MEMBER', '2026-06-07 11:00:00', 0), (26, 509, 226, 'MEMBER', '2026-06-07 11:20:00', 0);

INSERT INTO team_advisor (id, team_id, advisor_id, created_at, deleted)
VALUES
(1, 501, 109, @now, 0), (2, 502, 112, @now, 0), (3, 503, 109, @now, 0), (4, 503, 110, @now, 0),
(5, 504, 111, @now, 0), (6, 505, 108, @now, 0), (7, 506, 101, @now, 0), (8, 507, 111, @now, 0),
(9, 508, 112, @now, 0), (10, 509, 106, @now, 0);

INSERT INTO team_advisor_audit
(id, team_id, advisor_id, requester_id, status, reason, created_at, reviewed_at, deleted)
VALUES
(1, 501, 109, 201, 'APPROVED', '申请指导物联网应用设计竞赛。', '2026-05-04 12:00:00', '2026-05-04 16:00:00', 0),
(2, 503, 110, 207, 'APPROVED', '申请协助金融场景模型设计。', '2026-06-06 12:00:00', '2026-06-06 16:00:00', 0),
(3, 504, 110, 211, 'PENDING', '申请补充金融测算指导。', '2026-06-08 09:00:00', NULL, 0),
(4, 508, 102, 216, 'REJECTED', '申请AI算法方向指导。', '2026-04-06 12:00:00', '2026-04-07 10:00:00', 0);

INSERT INTO registration
(id, competition_id, user_id, project_name, advisor, participation_type, team_id, description, status, reject_reason, reviewed_by, reviewed_at, created_at, updated_at, deleted, track_id)
VALUES
(601, 301, 227, '校园竞赛数据看板', '刘青山', 'INDIVIDUAL', NULL, '构建竞赛数据可视化与流程跟踪页面。', 'APPROVED', NULL, @admin_id, '2026-06-03 10:00:00', '2026-06-02 09:10:00', @now, 0, 401),
(602, 301, 228, '移动端报名助手', '陈明珠', 'INDIVIDUAL', NULL, '设计面向学生的移动端报名助手。', 'PENDING', NULL, NULL, NULL, '2026-06-04 10:20:00', @now, 0, 402),
(603, 301, 220, '智能课程提醒插件', '刘青山', 'INDIVIDUAL', NULL, '课程提醒与竞赛日程联动。', 'REJECTED', '项目说明不完整，请补充功能边界。', @admin_id, '2026-06-05 10:00:00', '2026-06-04 13:20:00', @now, 0, 401),
(604, 301, 221, '学科竞赛知识库', '陈明珠', 'INDIVIDUAL', NULL, '建设竞赛政策与参赛经验知识库。', 'APPROVED', NULL, @admin_id, '2026-06-05 14:00:00', '2026-06-04 14:20:00', @now, 0, 401),
(605, 301, 225, '作品提交质量检测工具', '刘青山', 'INDIVIDUAL', NULL, '对提交材料进行格式与完整性检查。', 'APPROVED', NULL, @admin_id, '2026-06-05 15:00:00', '2026-06-04 15:20:00', @now, 0, 401),
(606, 301, 226, '竞赛证书生成助手', '赵雅文', 'INDIVIDUAL', NULL, '面向获奖证书的模板化生成工具。', 'PENDING', NULL, NULL, NULL, '2026-06-05 16:20:00', @now, 0, 402),
(607, 302, 201, '教学楼能耗感知系统', '廖远', 'TEAM', 501, '采集教学楼能耗数据并进行实时可视化。', 'APPROVED', NULL, @admin_id, '2026-05-08 10:00:00', '2026-05-06 10:00:00', @now, 0, 404),
(608, 302, 210, '资产仓储环境监测系统', '刘青山', 'TEAM', 506, '监测资产仓库温湿度并提供告警。', 'PENDING', NULL, NULL, NULL, '2026-05-18 10:00:00', @now, 0, 403),
(609, 303, 204, '赣江新区物流调度模型', '王彦博', 'TEAM', 502, '建立物流调度与路径优化模型。', 'APPROVED', NULL, @admin_id, '2026-04-10 10:00:00', '2026-04-08 10:00:00', @now, 0, 406),
(610, 303, 216, '库存周转优化模型', '王彦博', 'TEAM', 508, '针对校企合作库存数据进行周转优化。', 'REJECTED', '队伍成员材料未完整上传。', @admin_id, '2026-04-11 10:00:00', '2026-04-09 10:00:00', @now, 0, 406),
(611, 305, 207, '竞赛咨询智能问答助手', '廖远', 'TEAM', 503, '构建面向学生的竞赛咨询智能问答系统。', 'APPROVED', NULL, @admin_id, '2026-06-07 10:00:00', '2026-06-06 14:00:00', @now, 0, 409),
(612, 306, 211, '云账本经营分析平台', '方敏', 'TEAM', 504, '面向小微商户的智能记账与经营分析平台。', 'PENDING', NULL, NULL, NULL, '2026-06-08 10:00:00', @now, 0, 411),
(613, 306, 219, '校园循环市集计划', '罗思远', 'TEAM', 507, '校园二手交易与公益捐赠计划。', 'REJECTED', '团队已解散，请重新组队后提交。', @admin_id, '2026-06-08 11:00:00', '2026-06-07 10:00:00', @now, 0, 412),
(614, 306, 101, '财务预测协同平台', '罗思远', 'TEAM', 509, '教师发起团队，指导学生完成财务预测模块。', 'APPROVED', NULL, @admin_id, '2026-06-08 15:00:00', '2026-06-07 15:00:00', @now, 0, 411),
(615, 307, 222, '校园网络异常流量识别', '何远', 'TEAM', 505, '对校园网络日志进行异常检测与应急处置。', 'APPROVED', NULL, @admin_id, '2026-01-18 10:00:00', '2026-01-15 10:00:00', @now, 0, 414),
(616, 304, 210, '银行客户流失预测', '周启航', 'INDIVIDUAL', NULL, '基于客户行为数据构建流失预测模型。', 'APPROVED', NULL, @admin_id, '2026-03-05 10:00:00', '2026-03-03 10:00:00', @now, 0, 407),
(617, 304, 213, '智能财务费用分析', '李佳宁', 'INDIVIDUAL', NULL, '面向费用报销数据进行异常分析。', 'APPROVED', NULL, @admin_id, '2026-03-06 10:00:00', '2026-03-04 10:00:00', @now, 0, 408),
(618, 304, 214, '企业现金流风险识别', '李佳宁', 'INDIVIDUAL', NULL, '识别企业经营现金流风险信号。', 'APPROVED', NULL, @admin_id, '2026-03-07 10:00:00', '2026-03-05 10:00:00', @now, 0, 407),
(619, 304, 215, '消费行为画像分析', '周启航', 'INDIVIDUAL', NULL, '构建用户消费画像和营销建议。', 'APPROVED', NULL, @admin_id, '2026-03-07 11:00:00', '2026-03-05 11:00:00', @now, 0, 408);

INSERT INTO work
(id, competition_id, user_id, registration_id, title, description, file_url, file_name, status, avg_score, created_at, updated_at, deleted)
VALUES
(701, 301, 227, 601, '校园竞赛数据看板', '系统包含数据统计、流程跟踪和可视化组件。', '/api/file/download/files/works/software-innovation-report.md', '校园竞赛数据看板说明.md', 'SUBMITTED', NULL, '2026-07-01 10:00:00', @now, 0),
(702, 301, 221, 604, '学科竞赛知识库', '汇总竞赛政策、报名经验和常见问题。', '/api/file/download/files/works/software-innovation-report.md', '学科竞赛知识库说明.md', 'SUBMITTED', NULL, '2026-07-01 11:00:00', @now, 0),
(703, 301, 225, 605, '作品提交质量检测工具', '检测作品材料格式、命名和完整性。', '/api/file/download/files/works/software-innovation-report.md', '质量检测工具说明.md', 'SUBMITTED', NULL, '2026-07-01 12:00:00', @now, 0),
(704, 302, 201, 607, '教学楼能耗感知系统', '完成传感器数据模拟、采集与展示。', '/api/file/download/files/works/iot-campus-report.md', '教学楼能耗感知方案.md', 'SUBMITTED', NULL, '2026-06-12 10:00:00', @now, 0),
(705, 303, 204, 609, '赣江新区物流调度模型', '建立路径优化模型并给出调度方案。', '/api/file/download/files/works/finance-data-report.md', '物流调度建模论文.md', 'SUBMITTED', 89.00, '2026-05-04 10:00:00', @now, 0),
(706, 305, 207, 611, '竞赛咨询智能问答助手', '实现竞赛问答、政策检索和推荐功能。', '/api/file/download/files/works/ai-service-report.md', '智能问答助手说明.md', 'SUBMITTED', NULL, '2026-07-08 10:00:00', @now, 0),
(707, 306, 101, 614, '财务预测协同平台', '完成销售预测和现金流测算模块。', '/api/file/download/files/works/finance-data-report.md', '财务预测协同平台方案.md', 'SUBMITTED', NULL, '2026-08-03 10:00:00', @now, 0),
(708, 307, 222, 615, '校园网络异常流量识别方案', '完成日志清洗、异常规则和响应建议。', '/api/file/download/files/works/security-defense-report.md', '网络安全攻防报告.md', 'SUBMITTED', 92.50, '2026-02-08 10:00:00', @now, 0),
(709, 304, 210, 616, '银行客户流失预测模型', '基于客户交易特征构建预测模型。', '/api/file/download/files/works/finance-data-report.md', '银行客户流失预测报告.md', 'SUBMITTED', 91.00, '2026-04-08 10:00:00', @now, 0),
(710, 304, 213, 617, '智能财务费用分析系统', '识别费用异常并生成分析报告。', '/api/file/download/files/works/finance-data-report.md', '智能财务费用分析报告.md', 'SUBMITTED', 86.50, '2026-04-08 11:00:00', @now, 0),
(711, 304, 214, 618, '企业现金流风险识别模型', '结合现金流指标识别经营风险。', '/api/file/download/files/works/finance-data-report.md', '现金流风险识别报告.md', 'SUBMITTED', 84.00, '2026-04-08 12:00:00', @now, 0),
(712, 304, 215, 619, '消费行为画像分析', '构建消费分层和精准营销建议。', '/api/file/download/files/works/finance-data-report.md', '消费行为画像分析报告.md', 'SUBMITTED', 78.50, '2026-04-08 13:00:00', @now, 0);

INSERT INTO review_task
(id, competition_id, work_id, reviewer_id, score, comment, status, created_at, updated_at, deleted)
VALUES
(801, 304, 709, 105, 92, '模型解释充分，业务价值清晰。', 'COMPLETED', '2026-04-12 10:00:00', @now, 0),
(802, 304, 709, 103, 90, '数据处理规范，结果稳定。', 'COMPLETED', '2026-04-12 10:05:00', @now, 0),
(803, 304, 710, 104, 87, '财务场景贴合度较高。', 'COMPLETED', '2026-04-12 10:10:00', @now, 0),
(804, 304, 710, 105, 86, '可视化表达可继续增强。', 'COMPLETED', '2026-04-12 10:15:00', @now, 0),
(805, 304, 711, 104, 85, '风险指标选择合理。', 'COMPLETED', '2026-04-12 10:20:00', @now, 0),
(806, 304, 711, 103, 83, '模型对比部分略显不足。', 'COMPLETED', '2026-04-12 10:25:00', @now, 0),
(807, 304, 712, 105, 79, '分析路径完整。', 'COMPLETED', '2026-04-12 10:30:00', @now, 0),
(808, 304, 712, 104, 78, '结论建议还可更聚焦。', 'COMPLETED', '2026-04-12 10:35:00', @now, 0),
(809, 307, 708, 108, 94, '攻防流程完整，修复建议有效。', 'COMPLETED', '2026-02-12 10:00:00', @now, 0),
(810, 307, 708, 101, 91, '日志分析清晰。', 'COMPLETED', '2026-02-12 10:05:00', @now, 0),
(811, 303, 705, 103, 90, '模型结构清楚，优化结果可信。', 'COMPLETED', '2026-05-08 10:00:00', @now, 0),
(812, 303, 705, 105, 88, '财经解释较完整。', 'COMPLETED', '2026-05-08 10:05:00', @now, 0),
(813, 301, 701, 101, NULL, NULL, 'PENDING', '2026-07-06 09:00:00', @now, 0),
(814, 301, 702, 102, NULL, NULL, 'PENDING', '2026-07-06 09:05:00', @now, 0),
(815, 302, 704, 101, NULL, NULL, 'PENDING', '2026-06-21 09:00:00', @now, 0),
(816, 305, 706, 102, NULL, NULL, 'PENDING', '2026-07-13 09:00:00', @now, 0);

INSERT INTO award (id, competition_id, user_id, work_id, award_level, certificate_url, created_at, deleted)
VALUES
(901, 304, 210, 709, '一等奖', NULL, '2026-04-21 10:00:00', 0),
(902, 304, 213, 710, '二等奖', NULL, '2026-04-21 10:05:00', 0),
(903, 304, 214, 711, '三等奖', NULL, '2026-04-21 10:10:00', 0),
(904, 304, 215, 712, '优秀奖', NULL, '2026-04-21 10:15:00', 0),
(905, 307, 222, 708, '一等奖', NULL, '2026-02-19 10:00:00', 0),
(906, 303, 204, 705, '一等奖', NULL, '2026-05-16 10:00:00', 0);

INSERT INTO schedule
(id, user_id, title, description, start_time, end_time, location, type, related_id, color, created_at, updated_at, deleted)
VALUES
(1101, 101, '软件创新杯答疑', '参赛项目需求分析与技术路线答疑。', '2026-06-12 09:00:00', '2026-06-12 12:00:00', '蛟桥园北区实验楼A305', 'PERSONAL', NULL, '#409eff', @now, @now, 0),
(1102, 109, '物联网项目辅导', '传感器选型与数据采集方案讨论。', '2026-06-13 14:00:00', '2026-06-13 17:00:00', '软件与物联网工程学院会议室', 'PERSONAL', NULL, '#67c23a', @now, @now, 0),
(1103, 102, 'AI应用项目辅导', '知识库构建与问答流程设计。', '2026-06-14 09:00:00', '2026-06-14 11:30:00', '实验楼B402', 'PERSONAL', NULL, '#409eff', @now, @now, 0),
(1104, 103, '数学建模论文指导', '模型假设、灵敏度分析与论文结构。', '2026-06-15 14:00:00', '2026-06-15 17:00:00', '统计与数据科学学院214', 'PERSONAL', NULL, '#e6a23c', @now, @now, 0),
(1105, 110, '财经数据分析答疑', '特征工程与模型评估。', '2026-06-16 09:00:00', '2026-06-16 12:00:00', '金融学院302', 'PERSONAL', NULL, '#909399', @now, @now, 0),
(1106, 111, '创业计划书辅导', '商业模式与财务预测。', '2026-06-17 15:00:00', '2026-06-17 18:00:00', '大学生创业园204', 'PERSONAL', NULL, '#f56c6c', @now, @now, 0),
(1107, 108, '网络安全复盘', '漏洞复现与修复建议复盘。', '2026-06-18 10:00:00', '2026-06-18 12:00:00', '网络安全实验室', 'PERSONAL', NULL, '#303133', @now, @now, 0);

INSERT INTO appointment
(id, student_id, advisor_id, title, description, appointment_date, duration, location, status, reject_reason, created_at, updated_at, deleted)
VALUES
(1001, 227, 101, '软件创新杯项目需求评审', '希望确认数据看板的功能边界。', '2026-06-12 09:30:00', 60, '蛟桥园北区实验楼A305', 'APPROVED', NULL, '2026-06-10 10:00:00', @now, 0),
(1002, 201, 109, '物联网采集方案讨论', '讨论能耗数据采集频率与可视化指标。', '2026-06-13 14:30:00', 60, '软件与物联网工程学院会议室', 'PENDING', NULL, '2026-06-11 10:00:00', @now, 0),
(1003, 207, 102, 'AI问答助手语料设计', '咨询知识库结构与问答准确率评估。', '2026-06-14 09:30:00', 60, '实验楼B402', 'APPROVED', NULL, '2026-06-11 11:00:00', @now, 0),
(1004, 204, 103, '数学建模论文结构修改', '希望老师检查模型假设与论文结构。', '2026-06-15 15:00:00', 60, '统计与数据科学学院214', 'COMPLETED', NULL, '2026-06-10 15:00:00', @now, 0),
(1005, 210, 110, '金融风控模型复盘', '想讨论变量解释和评审意见。', '2026-06-16 10:00:00', 60, '金融学院302', 'REJECTED', '该时间段需参加学院会议，请重新预约。', '2026-06-12 09:00:00', @now, 0),
(1006, 211, 111, '创业计划财务预测', '咨询收入假设与现金流预测。', '2026-06-17 15:30:00', 60, '大学生创业园204', 'APPROVED', NULL, '2026-06-12 10:00:00', @now, 0),
(1007, 222, 108, '网络安全攻防报告复盘', '希望复盘日志分析部分。', '2026-06-18 10:30:00', 60, '网络安全实验室', 'CANCELLED', NULL, '2026-06-12 11:00:00', @now, 0);

INSERT INTO team_task
(id, team_id, title, description, assignee_id, status, priority, due_date, created_by, created_at, updated_at, deleted)
VALUES
(1301, 501, '完成传感器采集模块', '整理采集字段并完成接口联调。', 202, 'IN_PROGRESS', 'HIGH', '2026-06-14 18:00:00', 201, @now, @now, 0),
(1302, 501, '绘制能耗趋势图', '完成前端可视化图表。', 203, 'PENDING', 'MEDIUM', '2026-06-16 18:00:00', 201, @now, @now, 0),
(1303, 503, '整理竞赛政策语料', '完成问答知识库初稿。', 208, 'COMPLETED', 'HIGH', '2026-06-10 18:00:00', 207, @now, @now, 0),
(1304, 504, '撰写商业模式画布', '补充目标客户和成本结构。', 212, 'PENDING', 'MEDIUM', '2026-06-20 18:00:00', 211, @now, @now, 0),
(1305, 505, '复盘漏洞修复建议', '完善风险说明和加固方案。', 223, 'COMPLETED', 'HIGH', '2026-02-15 18:00:00', 222, @now, @now, 0);

INSERT INTO team_message (id, team_id, user_id, content, type, created_at, deleted)
VALUES
(1401, 501, 201, '今晚先完成采集字段确认，明天统一调接口。', 'TEXT', '2026-06-10 20:00:00', 0),
(1402, 501, 202, '我已经把温湿度和用电量字段整理好了。', 'TEXT', '2026-06-10 20:10:00', 0),
(1403, 503, 207, '语料先按报名、作品、评审、成绩四类拆分。', 'TEXT', '2026-06-11 19:30:00', 0),
(1404, 504, 211, '明天把财务预测表合并到计划书里。', 'TEXT', '2026-06-11 20:00:00', 0);

INSERT INTO todo
(id, user_id, title, description, due_date, status, priority, type, related_id, created_at, updated_at, deleted)
VALUES
(1501, 227, '补充数据看板测试截图', '上传作品前完善关键页面截图。', '2026-07-03 18:00:00', 'PENDING', 'HIGH', 'PERSONAL', NULL, @now, @now, 0),
(1502, 201, '整理物联网原型照片', '放入作品材料附件。', '2026-06-18 18:00:00', 'PENDING', 'MEDIUM', 'TEAM', 501, @now, @now, 0),
(1503, 204, '提交建模论文终稿', '已完成并归档。', '2026-05-04 18:00:00', 'COMPLETED', 'HIGH', 'TEAM', 502, @now, @now, 0),
(1504, 211, '完善创业计划财务测算', '补充收入、成本和现金流预测。', '2026-06-20 18:00:00', 'PENDING', 'HIGH', 'TEAM', 504, @now, @now, 0);

INSERT INTO notification
(id, user_id, title, content, type, related_id, is_read, created_at)
VALUES
(1201, 227, '报名审核通过', '您报名的江西财经大学软件创新杯已审核通过。', 'REGISTRATION', 601, 0, @now),
(1202, 220, '报名被驳回', '智能课程提醒插件报名材料不完整，请修改后重新提交。', 'REGISTRATION', 603, 0, @now),
(1203, 101, '新的预约申请', '张子涵预约了您的软件创新杯项目需求评审。', 'APPOINTMENT', 1001, 1, @now),
(1204, 207, '团队指导审核通过', '财智AI先锋队的指导老师申请已通过。', 'TEAM', 503, 0, @now),
(1205, 210, '获奖通知', '您在财经大数据分析挑战赛中获得一等奖。', 'AWARD', 901, 0, @now),
(1206, 222, '获奖通知', '蓝盾攻防队在网络安全攻防竞赛中获得一等奖。', 'AWARD', 905, 0, @now);

INSERT INTO operation_log
(id, user_id, username, role, module, operation, content, ip, result, created_at)
VALUES
(1601, @admin_id, 'admin', 'ADMIN', '竞赛管理', '创建竞赛', '创建江西财经大学软件创新杯。', '127.0.0.1', 'SUCCESS', @now),
(1602, @admin_id, 'admin', 'ADMIN', '报名审核', '审核通过', '通过张子涵的软件创新杯报名。', '127.0.0.1', 'SUCCESS', @now),
(1603, 101, 'teacher_lqs', 'TEACHER', '日程安排', '新增日程', '新增软件创新杯答疑时段。', '127.0.0.1', 'SUCCESS', @now),
(1604, 207, 's2202304301', 'STUDENT', '团队管理', '创建团队', '创建财智AI先锋队。', '127.0.0.1', 'SUCCESS', @now);

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'done' AS result,
       (SELECT COUNT(*) FROM sys_user WHERE username <> 'admin' AND deleted = 0) AS recreated_user_count,
       (SELECT COUNT(*) FROM competition WHERE deleted = 0) AS competition_count,
       (SELECT COUNT(*) FROM team WHERE deleted = 0) AS team_count,
       (SELECT COUNT(*) FROM registration WHERE deleted = 0) AS registration_count,
       (SELECT COUNT(*) FROM work WHERE deleted = 0) AS work_count,
       (SELECT COUNT(*) FROM appointment WHERE deleted = 0) AS appointment_count,
       (SELECT COUNT(*) FROM review_task WHERE deleted = 0) AS review_task_count;
