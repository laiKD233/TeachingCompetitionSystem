package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class TeamDTO {

    @NotBlank(message = "团队名称不能为空")
    @Size(max = 100, message = "团队名称最多100个字符")
    private String name;

    private String description;

    private Long competitionId;

    private List<Long> advisorId;

    private Integer maxMembers = 5;
}
