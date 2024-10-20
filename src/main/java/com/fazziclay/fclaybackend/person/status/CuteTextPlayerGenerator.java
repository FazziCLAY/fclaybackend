package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaybackend.Util;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;

public class CuteTextPlayerGenerator {
    public static String v1(PersonStatus status, String emoji) {
        PlaybackDto playback = status.getHeadphones();
        if (playback == null) return null;
        Long duration = playback.getDuration();
        Long position = playback.getPosition();

        StringBuilder msg = new StringBuilder();
        if (duration != null && position != null) {
            msg.append(Util.time(position)).append(" ─────────── ").append(Util.time(duration)).append("\n");
        }
        msg.append(emoji).append(" ").append(playback.getTitle()).append("\n");
        msg.append(" ").append(" ").append(playback.getArtist()).append("\n");

        if (playback.getVolume() != null) {
            var vlm = new StringBuilder();
            for (int i = 1; i < 9; i++) {
                float p = ((float)i) * 10f;
                String chr = p > (playback.getVolume()*100f) ? "▯" : "▮";
                vlm.append(chr);
            }
            msg.append("ᴠᴏʟᴜᴍᴇ  : ").append(vlm).append("\n");
        }
        msg.append("↠ⁿᵉˣᵗ ˢᵒⁿᵍ ↺ ʳᵉᵖᵉᵃᵗ ⊜ ᵖᵃᵘˢᵉ");

        return msg.toString();
    }
}
