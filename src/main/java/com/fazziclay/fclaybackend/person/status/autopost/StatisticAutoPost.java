package com.fazziclay.fclaybackend.person.status.autopost;

import com.fazziclay.fclaybackend.FclaySpringApplication;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.Statistic;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;

public class StatisticAutoPost extends AutoPostFilter {
    private final Statistic statistic;

    public StatisticAutoPost(Statistic statistic) {
        super(Settings.builder()
                .rateLimit(3000)
                .changesApplyDelay(3000)
                .build());
        this.statistic = statistic;
    }

    @Override
    public void sendMessageAbout(PersonStatus status) {
        PlaybackDto headphones = status.getHeadphones();

        if (headphones != null) {
            statistic.appendStat(PlaybackDto.cloneDto(headphones));
        }
    }
}
