package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    @Select("SELECT a.id, a.student_id, a.advisor_id, " +
            "COALESCE(NULLIF(TRIM(a.title), ''), '未填写主题') as title, " +
            "a.description, a.appointment_date, a.duration, a.location, a.status, a.reject_reason, " +
            "a.created_at, a.updated_at, a.deleted, " +
            "COALESCE(NULLIF(TRIM(u1.name), ''), u1.username, CONCAT('用户', a.student_id)) as student_name, " +
            "COALESCE(NULLIF(TRIM(u2.name), ''), u2.username, CONCAT('用户', a.advisor_id)) as advisor_name " +
            "FROM appointment a " +
            "LEFT JOIN sys_user u1 ON a.student_id = u1.id " +
            "LEFT JOIN sys_user u2 ON a.advisor_id = u2.id " +
            "WHERE a.student_id = #{studentId} AND a.deleted = 0 " +
            "ORDER BY a.appointment_date DESC")
    List<Appointment> selectByStudentId(Long studentId);

    @Select("SELECT a.id, a.student_id, a.advisor_id, " +
            "COALESCE(NULLIF(TRIM(a.title), ''), '未填写主题') as title, " +
            "a.description, a.appointment_date, a.duration, a.location, a.status, a.reject_reason, " +
            "a.created_at, a.updated_at, a.deleted, " +
            "COALESCE(NULLIF(TRIM(u1.name), ''), u1.username, CONCAT('用户', a.student_id)) as student_name, " +
            "COALESCE(NULLIF(TRIM(u2.name), ''), u2.username, CONCAT('用户', a.advisor_id)) as advisor_name " +
            "FROM appointment a " +
            "LEFT JOIN sys_user u1 ON a.student_id = u1.id " +
            "LEFT JOIN sys_user u2 ON a.advisor_id = u2.id " +
            "WHERE a.advisor_id = #{advisorId} AND a.deleted = 0 " +
            "ORDER BY a.appointment_date DESC")
    List<Appointment> selectByAdvisorId(Long advisorId);

    @Select({
            "<script>",
            "SELECT a.id, a.student_id, a.advisor_id, ",
            "COALESCE(NULLIF(TRIM(a.title), ''), '未填写主题') as title, ",
            "a.description, a.appointment_date, a.duration, a.location, a.status, a.reject_reason, ",
            "a.created_at, a.updated_at, a.deleted, ",
            "COALESCE(NULLIF(TRIM(u1.name), ''), u1.username, CONCAT('用户', a.student_id)) as student_name, ",
            "COALESCE(NULLIF(TRIM(u2.name), ''), u2.username, CONCAT('用户', a.advisor_id)) as advisor_name ",
            "FROM appointment a ",
            "LEFT JOIN sys_user u1 ON a.student_id = u1.id ",
            "LEFT JOIN sys_user u2 ON a.advisor_id = u2.id ",
            "WHERE a.advisor_id = #{advisorId} AND a.deleted = 0 ",
            "<if test='status != null and status != \"\"'>",
            "  AND a.status = #{status} ",
            "</if>",
            "ORDER BY a.appointment_date DESC",
            "</script>"
    })
    List<Appointment> selectByAdvisorIdAndStatus(@Param("advisorId") Long advisorId,
                                                 @Param("status") String status);

    @Select({
            "<script>",
            "SELECT a.id, a.student_id, a.advisor_id, ",
            "COALESCE(NULLIF(TRIM(a.title), ''), '未填写主题') as title, ",
            "a.description, a.appointment_date, a.duration, a.location, a.status, a.reject_reason, ",
            "a.created_at, a.updated_at, a.deleted, ",
            "COALESCE(NULLIF(TRIM(u1.name), ''), u1.username, CONCAT('用户', a.student_id)) as student_name, ",
            "COALESCE(NULLIF(TRIM(u2.name), ''), u2.username, CONCAT('用户', a.advisor_id)) as advisor_name ",
            "FROM appointment a ",
            "LEFT JOIN sys_user u1 ON a.student_id = u1.id ",
            "LEFT JOIN sys_user u2 ON a.advisor_id = u2.id ",
            "WHERE a.deleted = 0 ",
            "<if test='status != null and status != \"\"'>",
            "  AND a.status = #{status} ",
            "</if>",
            "ORDER BY a.appointment_date DESC",
            "</script>"
    })
    List<Appointment> selectAllWithStatus(@Param("status") String status);

    @Select("SELECT COUNT(1) FROM appointment " +
            "WHERE advisor_id = #{advisorId} AND deleted = 0 " +
            "AND status IN ('PENDING', 'APPROVED') " +
            "AND appointment_date < #{endTime} " +
            "AND DATE_ADD(appointment_date, INTERVAL duration MINUTE) > #{startTime}")
    int countAdvisorTimeConflict(@Param("advisorId") Long advisorId,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COUNT(1) FROM appointment " +
            "WHERE student_id = #{studentId} AND deleted = 0 " +
            "AND status IN ('PENDING', 'APPROVED') " +
            "AND appointment_date < #{endTime} " +
            "AND DATE_ADD(appointment_date, INTERVAL duration MINUTE) > #{startTime}")
    int countStudentTimeConflict(@Param("studentId") Long studentId,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);
}
