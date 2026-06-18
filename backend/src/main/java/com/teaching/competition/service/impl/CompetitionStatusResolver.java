package com.teaching.competition.service.impl;

import com.teaching.competition.entity.Competition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompetitionStatusResolver {

    public String resolveStatus(Competition competition) {
        return resolveStatus(competition, LocalDateTime.now());
    }

    public String resolveStatus(Competition competition, LocalDateTime now) {
        if (competition == null) {
            return "PUBLISHED";
        }
        if (isReached(now, competition.getAnnouncementEnd())) {
            return "FINISHED";
        }
        if (isReached(now, competition.getAnnouncementStart()) || isReached(now, competition.getReviewEnd())) {
            return "ANNOUNCEMENT";
        }
        if (isReached(now, competition.getReviewStart()) || isReached(now, competition.getSubmissionDeadline())) {
            return "REVIEW";
        }
        if (isReached(now, competition.getRegistrationEnd())) {
            return "SUBMISSION";
        }
        if (isReached(now, competition.getRegistrationStart())
                || (competition.getRegistrationStart() == null && competition.getRegistrationEnd() != null && now.isBefore(competition.getRegistrationEnd()))) {
            return "REGISTRATION";
        }
        return "PUBLISHED";
    }

    public void refreshStatus(Competition competition) {
        if (competition != null) {
            competition.setStatus(resolveStatus(competition));
        }
    }

    private boolean isReached(LocalDateTime now, LocalDateTime boundary) {
        return boundary != null && !now.isBefore(boundary);
    }
}
