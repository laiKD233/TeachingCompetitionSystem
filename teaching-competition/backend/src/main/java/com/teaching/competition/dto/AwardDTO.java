package com.teaching.competition.dto;

import lombok.Data;
import java.util.List;

@Data
public class AwardDTO {
    private Long competitionId;
    private List<AwardItemDTO> items;

    @Data
    public static class AwardItemDTO {
        private Long workId;
        private String awardLevel;
    }
}
