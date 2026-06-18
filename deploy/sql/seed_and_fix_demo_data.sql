-- 修复 + 演示数据脚本（可重复执行，尽量幂等）
-- 用法：
-- mysql -D teaching_competition < deploy/sql/seed_and_fix_demo_data.sql

START TRANSACTION;

-- 1) 统一历史文件 URL（去掉 localhost 硬编码）
UPDATE competition
SET cover_image = REPLACE(REPLACE(cover_image, 'http://localhost:8090', ''), 'http://127.0.0.1:8090', '')
WHERE deleted = 0
  AND cover_image IS NOT NULL
  AND cover_image <> ''
  AND (cover_image LIKE 'http://localhost:8090/%' OR cover_image LIKE 'http://127.0.0.1:8090/%');

UPDATE work
SET file_url = REPLACE(REPLACE(file_url, 'http://localhost:8090', ''), 'http://127.0.0.1:8090', '')
WHERE deleted = 0
  AND file_url IS NOT NULL
  AND file_url <> ''
  AND (file_url LIKE 'http://localhost:8090/%' OR file_url LIKE 'http://127.0.0.1:8090/%');

-- 2) 现有竞赛补封面（复用已有任一封面）
SET @cover_seed := (
  SELECT cover_image
  FROM competition
  WHERE deleted = 0
    AND cover_image IS NOT NULL
    AND cover_image <> ''
  LIMIT 1
);
SET @cover_programming := '/api/file/download/files/demo/cover-programming.svg';
SET @cover_team := '/api/file/download/files/demo/cover-ai-team.svg';
SET @cover_modeling := '/api/file/download/files/demo/cover-modeling.svg';

UPDATE competition
SET cover_image = CASE
  WHEN type LIKE '%数学%' THEN @cover_modeling
  WHEN participation_type = 'TEAM' THEN @cover_team
  ELSE @cover_programming
END
WHERE deleted = 0
  AND (cover_image IS NULL OR cover_image = '');

-- 3) 修复预约空主题/空地点
UPDATE appointment
SET title = CONCAT('预约指导-', DATE_FORMAT(appointment_date, '%m%d %H:%i'))
WHERE deleted = 0
  AND (title IS NULL OR TRIM(title) = '');

UPDATE appointment
SET location = '线上会议'
WHERE deleted = 0
  AND (location IS NULL OR TRIM(location) = '');

-- 4) 修复团队赛报名缺 team_id（按同竞赛同用户已加入团队回填）
UPDATE registration r
JOIN team_member tm
  ON tm.user_id = r.user_id
 AND tm.deleted = 0
JOIN team t
  ON t.id = tm.team_id
 AND t.deleted = 0
 AND t.status = 'ACTIVE'
 AND t.competition_id = r.competition_id
SET r.team_id = t.id
WHERE r.deleted = 0
  AND r.participation_type = 'TEAM'
  AND (r.team_id IS NULL OR r.team_id = 0);

-- 5) 新增演示账号（密码沿用 student1 的哈希）
SET @pwd_seed := (SELECT password FROM sys_user WHERE username = 'student1' LIMIT 1);
SET @now := NOW();

INSERT INTO sys_user (
  username, password, name, student_id, college, class_name, phone, email, avatar, role, status, permissions, created_at, updated_at, deleted
)
SELECT 'teacher_demo', @pwd_seed, '示例教师', NULL, '软件学院', NULL, '13800000011', 'teacher_demo@example.com', NULL, 'TEACHER', 1, NULL, @now, @now, 0
FROM dual
WHERE @pwd_seed IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'teacher_demo');

INSERT INTO sys_user (
  username, password, name, student_id, college, class_name, phone, email, avatar, role, status, permissions, created_at, updated_at, deleted
)
SELECT 'student_demo_a', @pwd_seed, '示例学生甲', '2202304101', '软件学院', '软件工程1班', '13800000021', 'student_a@example.com', NULL, 'STUDENT', 1, NULL, @now, @now, 0
FROM dual
WHERE @pwd_seed IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'student_demo_a');

INSERT INTO sys_user (
  username, password, name, student_id, college, class_name, phone, email, avatar, role, status, permissions, created_at, updated_at, deleted
)
SELECT 'student_demo_b', @pwd_seed, '示例学生乙', '2202304102', '软件学院', '软件工程2班', '13800000022', 'student_b@example.com', NULL, 'STUDENT', 1, NULL, @now, @now, 0
FROM dual
WHERE @pwd_seed IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'student_demo_b');

SET @admin_id := (SELECT id FROM sys_user WHERE username = 'admin' LIMIT 1);
SET @teacher_demo_id := (SELECT id FROM sys_user WHERE username = 'teacher_demo' LIMIT 1);
SET @student_demo_a_id := (SELECT id FROM sys_user WHERE username = 'student_demo_a' LIMIT 1);
SET @student_demo_b_id := (SELECT id FROM sys_user WHERE username = 'student_demo_b' LIMIT 1);

-- 6) 新增演示竞赛（一个个人赛 + 一个团队赛）
INSERT INTO competition (
  name, type, participation_type, theme, description, cover_image, status,
  registration_start, registration_end, submission_deadline, review_start, review_end, announcement_start, announcement_end,
  awards_config, rules, submission_requirements, created_by, created_at, updated_at, deleted
)
SELECT
  '算法与数据结构挑战赛（演示）', '程序设计', 'INDIVIDUAL', '算法优化与工程实现',
  '用于演示个人赛报名、提交作品、评分与公示的完整流程。',
  @cover_programming, 'REGISTRATION',
  '2026-06-01 00:00:00', '2026-06-20 23:59:59', '2026-06-25 23:59:59',
  '2026-06-26 09:00:00', '2026-06-30 23:59:59', '2026-07-01 09:00:00', '2026-07-10 23:59:59',
  NULL, '按赛道提交可运行代码与文档说明。', '提交源代码压缩包与说明文档。',
  COALESCE(@admin_id, 1), @now, @now, 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM competition WHERE name = '算法与数据结构挑战赛（演示）' AND deleted = 0);

INSERT INTO competition (
  name, type, participation_type, theme, description, cover_image, status,
  registration_start, registration_end, submission_deadline, review_start, review_end, announcement_start, announcement_end,
  awards_config, rules, submission_requirements, created_by, created_at, updated_at, deleted
)
SELECT
  'AI应用创新团队赛（演示）', '创新创业', 'TEAM', 'AI+行业应用',
  '用于演示团队建队、团队报名、预约指导、团队协作与作品评审流程。',
  @cover_team, 'REGISTRATION',
  '2026-06-01 00:00:00', '2026-06-22 23:59:59', '2026-06-28 23:59:59',
  '2026-06-29 09:00:00', '2026-07-03 23:59:59', '2026-07-04 09:00:00', '2026-07-12 23:59:59',
  NULL, '每队 2-5 人，需提交项目计划书与演示材料。', '提交项目文档、PPT 与演示视频链接。',
  COALESCE(@admin_id, 1), @now, @now, 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM competition WHERE name = 'AI应用创新团队赛（演示）' AND deleted = 0);

SET @comp_individual_id := (SELECT id FROM competition WHERE name = '算法与数据结构挑战赛（演示）' AND deleted = 0 LIMIT 1);
SET @comp_team_id := (SELECT id FROM competition WHERE name = 'AI应用创新团队赛（演示）' AND deleted = 0 LIMIT 1);

-- 7) 新增赛道
INSERT INTO track (competition_id, name, description, status, created_at, updated_at)
SELECT @comp_individual_id, '算法设计', '算法复杂度优化与工程实现', 1, @now, @now
FROM dual
WHERE @comp_individual_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM track WHERE competition_id = @comp_individual_id AND name = '算法设计');

INSERT INTO track (competition_id, name, description, status, created_at, updated_at)
SELECT @comp_individual_id, '数据处理', '高并发数据处理与分析', 1, @now, @now
FROM dual
WHERE @comp_individual_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM track WHERE competition_id = @comp_individual_id AND name = '数据处理');

INSERT INTO track (competition_id, name, description, status, created_at, updated_at)
SELECT @comp_team_id, '智慧校园', '校园场景下的 AI 应用创新', 1, @now, @now
FROM dual
WHERE @comp_team_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM track WHERE competition_id = @comp_team_id AND name = '智慧校园');

-- 8) 新增团队（团队赛）
INSERT INTO team (
  name, description, competition_id, leader_id, advisor_id, max_members, status, invite_code, created_at, updated_at, deleted
)
SELECT
  'AI协作示例队', '演示团队：用于验证团队赛流程与预约指导流程。',
  @comp_team_id, @student_demo_b_id, @teacher_demo_id, 5, 'ACTIVE',
  UPPER(SUBSTRING(REPLACE(UUID(), '-', ''), 1, 8)), @now, @now, 0
FROM dual
WHERE @comp_team_id IS NOT NULL
  AND @student_demo_b_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM team WHERE name = 'AI协作示例队' AND deleted = 0);

SET @team_demo_id := (SELECT id FROM team WHERE name = 'AI协作示例队' AND deleted = 0 LIMIT 1);

INSERT INTO team_member (team_id, user_id, role, join_time, deleted)
SELECT @team_demo_id, @student_demo_b_id, 'LEADER', @now, 0
FROM dual
WHERE @team_demo_id IS NOT NULL
  AND @student_demo_b_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM team_member
    WHERE team_id = @team_demo_id AND user_id = @student_demo_b_id AND deleted = 0
  );

INSERT INTO team_member (team_id, user_id, role, join_time, deleted)
SELECT @team_demo_id, @student_demo_a_id, 'MEMBER', @now, 0
FROM dual
WHERE @team_demo_id IS NOT NULL
  AND @student_demo_a_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM team_member
    WHERE team_id = @team_demo_id AND user_id = @student_demo_a_id AND deleted = 0
  );

INSERT INTO team_advisor (team_id, advisor_id, created_at, deleted)
SELECT @team_demo_id, @teacher_demo_id, @now, 0
FROM dual
WHERE @team_demo_id IS NOT NULL
  AND @teacher_demo_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM team_advisor
    WHERE team_id = @team_demo_id AND advisor_id = @teacher_demo_id AND deleted = 0
  );

-- 9) 新增报名记录（直接置为 APPROVED，便于演示）
INSERT INTO registration (
  competition_id, user_id, project_name, advisor, participation_type, team_id, track_id,
  description, status, reject_reason, reviewed_by, reviewed_at, created_at, updated_at, deleted
)
SELECT
  @comp_individual_id, @student_demo_a_id, '图算法性能优化项目', '示例教师', 'INDIVIDUAL', NULL,
  (SELECT id FROM track WHERE competition_id = @comp_individual_id AND name = '算法设计' LIMIT 1),
  '个人赛演示项目：关注图搜索与最短路径优化。', 'APPROVED', NULL, COALESCE(@admin_id, 1), @now, @now, @now, 0
FROM dual
WHERE @comp_individual_id IS NOT NULL
  AND @student_demo_a_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM registration
    WHERE competition_id = @comp_individual_id
      AND user_id = @student_demo_a_id
      AND deleted = 0
  );

INSERT INTO registration (
  competition_id, user_id, project_name, advisor, participation_type, team_id, track_id,
  description, status, reject_reason, reviewed_by, reviewed_at, created_at, updated_at, deleted
)
SELECT
  @comp_team_id, @student_demo_b_id, '校园智能助手', '示例教师', 'TEAM', @team_demo_id,
  (SELECT id FROM track WHERE competition_id = @comp_team_id AND name = '智慧校园' LIMIT 1),
  '团队赛演示项目：面向校园服务的智能助手系统。', 'APPROVED', NULL, COALESCE(@admin_id, 1), @now, @now, @now, 0
FROM dual
WHERE @comp_team_id IS NOT NULL
  AND @student_demo_b_id IS NOT NULL
  AND @team_demo_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM registration
    WHERE competition_id = @comp_team_id
      AND user_id = @student_demo_b_id
      AND deleted = 0
  );

SET @reg_individual_id := (
  SELECT id FROM registration
  WHERE competition_id = @comp_individual_id AND user_id = @student_demo_a_id AND deleted = 0
  ORDER BY id DESC LIMIT 1
);

SET @reg_team_id := (
  SELECT id FROM registration
  WHERE competition_id = @comp_team_id AND user_id = @student_demo_b_id AND deleted = 0
  ORDER BY id DESC LIMIT 1
);

-- 10) 新增作品（复用现有可下载文件 URL 做演示）
SET @work_seed_url := (
  SELECT file_url
  FROM work
  WHERE deleted = 0 AND file_url IS NOT NULL AND file_url <> ''
  LIMIT 1
);

INSERT INTO work (
  competition_id, user_id, registration_id, title, description, file_url, file_name, status, avg_score, created_at, updated_at, deleted
)
SELECT
  @comp_individual_id, @student_demo_a_id, @reg_individual_id,
  '图算法性能优化方案', '个人赛演示作品', COALESCE(@work_seed_url, '/api/file/download/files/demo/example.pdf'),
  'graph-optimization-demo.pdf', 'SUBMITTED', NULL, @now, @now, 0
FROM dual
WHERE @comp_individual_id IS NOT NULL
  AND @student_demo_a_id IS NOT NULL
  AND @reg_individual_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM work
    WHERE registration_id = @reg_individual_id AND deleted = 0
  );

INSERT INTO work (
  competition_id, user_id, registration_id, title, description, file_url, file_name, status, avg_score, created_at, updated_at, deleted
)
SELECT
  @comp_team_id, @student_demo_b_id, @reg_team_id,
  '校园智能助手项目计划书', '团队赛演示作品', COALESCE(@work_seed_url, '/api/file/download/files/demo/example.pdf'),
  'campus-ai-team-demo.pdf', 'SUBMITTED', NULL, @now, @now, 0
FROM dual
WHERE @comp_team_id IS NOT NULL
  AND @student_demo_b_id IS NOT NULL
  AND @reg_team_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM work
    WHERE registration_id = @reg_team_id AND deleted = 0
  );

-- 11) 新增老师日程与预约演示
INSERT INTO schedule (
  user_id, title, description, start_time, end_time, location, type, related_id, color, created_at, updated_at, deleted
)
SELECT
  @teacher_demo_id, '演示指导时段（上午）', '用于学生预约演示', '2026-06-08 09:00:00', '2026-06-08 12:00:00',
  '线上会议', 'PERSONAL', NULL, '#409eff', @now, @now, 0
FROM dual
WHERE @teacher_demo_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM schedule
    WHERE user_id = @teacher_demo_id
      AND title = '演示指导时段（上午）'
      AND start_time = '2026-06-08 09:00:00'
      AND deleted = 0
  );

INSERT INTO appointment (
  student_id, advisor_id, title, description, appointment_date, duration, location, status, reject_reason, created_at, updated_at, deleted
)
SELECT
  @student_demo_a_id, @teacher_demo_id, '演示预约：算法问题讨论', '用于验证预约管理列表展示', '2026-06-08 09:30:00',
  60, '线上会议', 'APPROVED', NULL, @now, @now, 0
FROM dual
WHERE @student_demo_a_id IS NOT NULL
  AND @teacher_demo_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM appointment
    WHERE student_id = @student_demo_a_id
      AND advisor_id = @teacher_demo_id
      AND appointment_date = '2026-06-08 09:30:00'
      AND deleted = 0
  );

COMMIT;

-- 12) 修复结果检查
SELECT 'done' AS result,
       (SELECT COUNT(*) FROM competition WHERE deleted = 0) AS competition_count,
       (SELECT COUNT(*) FROM team WHERE deleted = 0 AND status = 'ACTIVE') AS active_team_count,
       (SELECT COUNT(*) FROM registration WHERE deleted = 0) AS registration_count,
       (SELECT COUNT(*) FROM work WHERE deleted = 0) AS work_count,
       (SELECT COUNT(*) FROM appointment WHERE deleted = 0) AS appointment_count;
