package com.fazziclay.fclaybackend.person.status.autopost;

import com.fazziclay.fclaybackend.person.status.PersonStatus;
import org.jetbrains.annotations.NotNull;

public interface IAutoPost {
    void postPersonStatus(@NotNull PersonStatus status);
}
