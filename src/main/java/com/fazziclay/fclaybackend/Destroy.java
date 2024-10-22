package com.fazziclay.fclaybackend;

import java.util.Collection;
import java.util.List;

public interface Destroy {
    void destroy();

    static <T extends Destroy> void destroyAll(Collection<T> values) {
        for (T value : values) {
            value.destroy();
        }
    }

    static <T extends Destroy> void emptyList(List<T> destroys) {
        for (T destroy : destroys) {
            destroy.destroy();
        }
        destroys.clear();
    }
}
