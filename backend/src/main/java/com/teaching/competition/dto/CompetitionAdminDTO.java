package com.teaching.competition.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompetitionAdminDTO {
    private Long adminId;
    private List<Long> competitionIds;
}