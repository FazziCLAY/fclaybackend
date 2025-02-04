package com.fazziclay.fclaybackend.person.status;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@JsonFilter("DynamicFilter")
public class PersonStatus {
    public static final Set<String> ALLOWED_TO_MODIFY_DIRECTLY = Set.of(
            "moodText",
            "customStatus"
    );

    @Nullable private PlaybackDto headphones;
    @Nullable private String moodText;
    @Nullable private String customStatus;

    @Nullable private transient PlaybackDto originalHeadphones;
    private long originalHeadphonesTime = 0;

    @Nullable private Boolean isHeadphonesMobile;

    @Nullable private String headphonesDeviceName;

    private String random;

    public void setOriginalHeadphones(PlaybackDto originalHeadphones, long latestUpdated) {
        this.originalHeadphones = originalHeadphones;
        this.originalHeadphonesTime = latestUpdated;
        actualizeHeadphones();
    }

    public void clear() {
        setOriginalHeadphones(null, -1);

        isHeadphonesMobile = null;
        headphonesDeviceName = null;
        random = new Date().toString();
    }

    /**
     * Call before return this object to API endpoint
     * It recalc times in song
     */
    public void actualizeHeadphones() {
        PlaybackDto actual = originalHeadphones;
        if (originalHeadphones != null) {
            Long position = originalHeadphones.getPosition();
            if (position != null) {
                long overdue = System.currentTimeMillis() - originalHeadphonesTime;
                long nw = position + overdue;
                actual = originalHeadphones.newWithPatch(new PlaybackDto(null, null, null, null, null, nw, null, null));
                //Logger.debug("actualizePersonStatus f*** math check: pos=" + position + "; overdue=" + overdue + "; nw=" + nw);
            }
        }

        setHeadphones(actual);
    }

    public void actualize() {
        actualizeHeadphones();
    }
}
