package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaybackend.Logger;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Getter
@Setter
@ToString

public class PersonStatus {
    @Nullable
    private PlaybackDto headphones;

    private transient PlaybackDto originalHeadphones;
    private long originalHeadphonesTime = 0;

    @Nullable
    private Boolean isMobile;

    @Nullable
    private String deviceName;

    private String random;

    public void setOriginalHeadphones(PlaybackDto originalHeadphones, long latestUpdated) {
        this.originalHeadphones = originalHeadphones;
        this.originalHeadphonesTime = latestUpdated;
    }

    public void clear() {
        setOriginalHeadphones(null, -1);

        isMobile = null;
        deviceName = null;
        random = new Date().toString();
    }

    /**
     * Call before return it object to API endpoint
     */
    public void actualizePersonStatus() {
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
}
