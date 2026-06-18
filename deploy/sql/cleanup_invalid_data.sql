-- Clean invalid test/orphan data and normalize competition statuses.
-- Safe to re-run: all destructive operations are soft deletes.

START TRANSACTION;

-- Remove obvious test competitions that were accidentally created during manual testing.
UPDATE competition
SET deleted = 1
WHERE deleted = 0
  AND (
    id IN (1007, 1008)
    OR name IN ('示例', '示例比赛', 'a''a''a')
  );

-- Soft-delete records that point to deleted or missing competitions/users/teams.
UPDATE registration r
LEFT JOIN competition c ON c.id = r.competition_id AND c.deleted = 0
LEFT JOIN sys_user u ON u.id = r.user_id AND u.deleted = 0
SET r.deleted = 1
WHERE r.deleted = 0
  AND (c.id IS NULL OR u.id IS NULL);

UPDATE team t
LEFT JOIN competition c ON c.id = t.competition_id AND c.deleted = 0
LEFT JOIN sys_user u ON u.id = t.leader_id AND u.deleted = 0
SET t.deleted = 1, t.status = 'DISBANDED'
WHERE t.deleted = 0
  AND (c.id IS NULL OR u.id IS NULL);

UPDATE team_member tm
LEFT JOIN team t ON t.id = tm.team_id AND t.deleted = 0
LEFT JOIN sys_user u ON u.id = tm.user_id AND u.deleted = 0
SET tm.deleted = 1
WHERE tm.deleted = 0
  AND (t.id IS NULL OR u.id IS NULL);

UPDATE team_advisor ta
LEFT JOIN team t ON t.id = ta.team_id AND t.deleted = 0
LEFT JOIN sys_user u ON u.id = ta.advisor_id AND u.deleted = 0
SET ta.deleted = 1
WHERE ta.deleted = 0
  AND (t.id IS NULL OR u.id IS NULL);

UPDATE team_advisor_audit taa
LEFT JOIN team t ON t.id = taa.team_id AND t.deleted = 0
LEFT JOIN sys_user u ON u.id = taa.advisor_id AND u.deleted = 0
SET taa.deleted = 1
WHERE taa.deleted = 0
  AND (t.id IS NULL OR u.id IS NULL);

UPDATE work w
LEFT JOIN competition c ON c.id = w.competition_id AND c.deleted = 0
LEFT JOIN registration r ON r.id = w.registration_id AND r.deleted = 0
SET w.deleted = 1
WHERE w.deleted = 0
  AND (c.id IS NULL OR (w.registration_id IS NOT NULL AND r.id IS NULL));

UPDATE review_task rt
LEFT JOIN competition c ON c.id = rt.competition_id AND c.deleted = 0
LEFT JOIN work w ON w.id = rt.work_id AND w.deleted = 0
LEFT JOIN sys_user u ON u.id = rt.reviewer_id AND u.deleted = 0
SET rt.deleted = 1
WHERE rt.deleted = 0
  AND (c.id IS NULL OR w.id IS NULL OR u.id IS NULL);

UPDATE award a
LEFT JOIN competition c ON c.id = a.competition_id AND c.deleted = 0
LEFT JOIN work w ON w.id = a.work_id AND w.deleted = 0
SET a.deleted = 1
WHERE a.deleted = 0
  AND (c.id IS NULL OR w.id IS NULL);

UPDATE competition_admin ca
LEFT JOIN competition c ON c.id = ca.competition_id AND c.deleted = 0
LEFT JOIN sys_user u ON u.id = ca.admin_id AND u.deleted = 0
SET ca.deleted = 1
WHERE ca.deleted = 0
  AND (c.id IS NULL OR u.id IS NULL);

-- Normalize active competition status by timeline.
UPDATE competition
SET status = CASE
  WHEN announcement_end IS NOT NULL AND NOW() >= announcement_end THEN 'FINISHED'
  WHEN announcement_start IS NOT NULL AND NOW() >= announcement_start THEN 'ANNOUNCEMENT'
  WHEN review_end IS NOT NULL AND NOW() >= review_end THEN 'ANNOUNCEMENT'
  WHEN review_start IS NOT NULL AND NOW() >= review_start THEN 'REVIEW'
  WHEN submission_deadline IS NOT NULL AND NOW() >= submission_deadline THEN 'REVIEW'
  WHEN registration_end IS NOT NULL AND NOW() >= registration_end THEN 'SUBMISSION'
  WHEN registration_start IS NOT NULL AND NOW() >= registration_start THEN 'REGISTRATION'
  WHEN registration_start IS NULL AND registration_end IS NOT NULL AND NOW() < registration_end THEN 'REGISTRATION'
  ELSE 'PUBLISHED'
END
WHERE deleted = 0;

COMMIT;

SELECT 'cleanup_done' AS result,
       (SELECT COUNT(*) FROM competition WHERE deleted = 0) AS active_competitions,
       (SELECT COUNT(*) FROM registration WHERE deleted = 0) AS active_registrations,
       (SELECT COUNT(*) FROM team WHERE deleted = 0) AS active_teams,
       (SELECT COUNT(*) FROM work WHERE deleted = 0) AS active_works,
       (SELECT COUNT(*) FROM review_task WHERE deleted = 0) AS active_review_tasks;
