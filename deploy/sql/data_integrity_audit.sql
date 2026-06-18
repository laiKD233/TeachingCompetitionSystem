-- 数据一致性巡检（只读）
-- 用法：mysql -D teaching_competition < deploy/sql/data_integrity_audit.sql

SELECT 'A1_TEAM_REGISTRATION_WITHOUT_TEAM' AS check_code, COUNT(*) AS issue_count
FROM registration r
WHERE r.deleted = 0
  AND r.participation_type = 'TEAM'
  AND (r.team_id IS NULL OR r.team_id = 0);

SELECT 'A2_REGISTRATION_TEAM_MISMATCH' AS check_code, COUNT(*) AS issue_count
FROM registration r
LEFT JOIN team t ON t.id = r.team_id AND t.deleted = 0
WHERE r.deleted = 0
  AND r.participation_type = 'TEAM'
  AND r.team_id IS NOT NULL
  AND (t.id IS NULL OR t.competition_id <> r.competition_id);

SELECT 'A3_WORK_WITHOUT_REGISTRATION' AS check_code, COUNT(*) AS issue_count
FROM work w
LEFT JOIN registration r ON r.id = w.registration_id AND r.deleted = 0
WHERE w.deleted = 0
  AND (w.registration_id IS NULL OR r.id IS NULL);

SELECT 'A4_TEAM_MEMBER_ORPHAN' AS check_code, COUNT(*) AS issue_count
FROM team_member tm
LEFT JOIN team t ON t.id = tm.team_id AND t.deleted = 0
WHERE tm.deleted = 0
  AND t.id IS NULL;

SELECT 'A5_APPOINTMENT_EMPTY_TITLE' AS check_code, COUNT(*) AS issue_count
FROM appointment a
WHERE a.deleted = 0
  AND (a.title IS NULL OR TRIM(a.title) = '');

SELECT 'A6_APPOINTMENT_USER_ORPHAN' AS check_code, COUNT(*) AS issue_count
FROM appointment a
LEFT JOIN sys_user stu ON stu.id = a.student_id AND stu.deleted = 0
LEFT JOIN sys_user adv ON adv.id = a.advisor_id AND adv.deleted = 0
WHERE a.deleted = 0
  AND (stu.id IS NULL OR adv.id IS NULL);

SELECT 'A7_TEAM_IN_NON_TEAM_COMPETITION' AS check_code, COUNT(*) AS issue_count
FROM team t
LEFT JOIN competition c ON c.id = t.competition_id AND c.deleted = 0
WHERE t.deleted = 0
  AND (
    c.id IS NULL
    OR c.participation_type IS NULL
    OR c.participation_type <> 'TEAM'
  );
