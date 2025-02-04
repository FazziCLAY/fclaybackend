package com.fazziclay.fclaybackend.person.status.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fazziclay.fclaybackend.Destroy;
import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.autopost.IAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.ovk.OVKApiAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.telegram.TelegramBotAutoPost;
import com.fazziclay.fclaybackend.states.PersonsStatusConfig;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.*;

public class UserHandler implements Destroy {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String getAccessToken;
    private final String modifyAccessToken;
    private final PersonStatus status = new PersonStatus();
    private final List<IAutoPost> autoPosts = new ArrayList<>();
    private long statusLatestUpdated;
    private final List<DeviceUserHandler> devices = new ArrayList<>();

    public UserHandler(PersonsStatusConfig.PersonStatusUser cfgUser) {
        this.getAccessToken = cfgUser.getGetAccessToken();
        this.modifyAccessToken = cfgUser.getModifyAccessToken();
        for (ObjectNode obj : cfgUser.getAutoPosts()) {
            var service = obj.get("service").asText();

            switch (service) {
                case "telegram_blog" ->
                        this.autoPosts.add(new TelegramBotAutoPost(obj.get("token").asText(), obj.get("channel").asText()));
                case "ovk_status" ->
                        this.autoPosts.add(new OVKApiAutoPost(obj.get("host").asText(), obj.get("token").asText()));
            }
        }

        for (PersonsStatusConfig.PersonStatusDevice device : cfgUser.getDevices()) {
            DeviceUserHandler user = new DeviceUserHandler(device);
            devices.add(user);
        }
    }

    public void tick() {
        if (System.currentTimeMillis() - statusLatestUpdated > 1900) {
            updateStatus();
        }
    }

    private void onDeviceUpdated(DeviceUserHandler handler) {
        updateStatus();
    }

    public Optional<DeviceUserHandler> getDeviceHandler(@Nullable String authorization) {
        return devices.stream()
                .filter(user -> user.isAccessGrant(authorization))
                .findFirst();
    }

    private Optional<DeviceUserHandler> calcActiveDevice() {
        return devices.stream()
                .filter(Objects::nonNull)
                .filter(DeviceUserHandler::isActive)
                .findFirst();
    }

    /**
     * Calc current and set to api status
     */
    private void updateStatus() {
        Optional<DeviceUserHandler> activeDevice = calcActiveDevice();
        if (activeDevice.isEmpty()) {
            status.clear();

        } else {
            activeDevice.get().updatePersonStatus(status);
        }
        autoPostsUpdate();
        statusLatestUpdated = System.currentTimeMillis();
    }


    /**
     * Post song to all IAutoPost's
     * anti-spam must be implemented in IAutoPost
     */
    private void autoPostsUpdate() {
        for (IAutoPost autoPost : autoPosts) {
            autoPost.postPersonStatus(status);
        }
    }

    public PersonStatus getActualPersonStatus(@Nullable String authorization) {
        checkGetPermission(authorization);
        status.actualize();
        return status;
    }

    public MappingJacksonValue getActualPersonStatusFiltered(@Nullable String authorization, String fields) {
        var status = getActualPersonStatus(authorization);

        // Если query-параметр `fields` передан, фильтруем JSON
        MappingJacksonValue mapping = new MappingJacksonValue(status);

        if (fields != null && !fields.isEmpty()) {
            Set<String> fieldSet = Set.copyOf(Arrays.asList(fields.split(",")));

            FilterProvider filters = new SimpleFilterProvider()
                    .addFilter("DynamicFilter",
                            SimpleBeanPropertyFilter.filterOutAllExcept(fieldSet));

            mapping.setFilters(filters);
        }

        return mapping;
    }

    // get
    private void checkGetPermission(@Nullable String authorization) {
        if (this.getAccessToken != null) {
            if (!Objects.equals(this.getAccessToken, authorization)) {
                throw new HttpException(HttpStatus.UNAUTHORIZED, "This user need authentication for get.");
            }
        }
    }

    private void checkModifyPermission(@Nullable String authorization) {
        if (this.modifyAccessToken != null) {
            if (!Objects.equals(this.modifyAccessToken, authorization)) {
                throw new HttpException(HttpStatus.UNAUTHORIZED, "This user need authentication for modify it.");
            }
        }
    }

    // device
    public PlaybackDto putHeadphones(@Nullable String authorization, PlaybackDto status) {
        var handler = getDeviceHandler(authorization).orElseThrow(() -> new HttpException(HttpStatus.UNAUTHORIZED, "no device found"));
        handler.putHeadphones(status);
        onDeviceUpdated(handler);

        return handler.getSong();
    }

    // device
    public PlaybackDto patchHeadphones(String authorization, PlaybackDto patch) {
        var handler = getDeviceHandler(authorization).orElseThrow(() -> new HttpException(HttpStatus.UNAUTHORIZED, "no device found"));

        PlaybackDto patchedSong = handler.getSong().newWithPatch(patch);

        handler.putHeadphones(patchedSong);
        onDeviceUpdated(handler);

        return handler.getSong();
    }

    @Override
    public void destroy() {
        // dp nothing
    }

    public void deleteMoodText(String authorization) {
        checkModifyPermission(authorization);
        status.setMoodText(null);
        status.setRandom("Mood cleared " + hashCode());
    }

    @SneakyThrows
    public PersonStatus patchPersonStatus(String authorization, Map<String, Object> updates) {
        checkModifyPermission(authorization);

        // Оставляем только разрешённые поля
        boolean notAllowedKeysRemoved = updates.keySet().retainAll(PersonStatus.ALLOWED_TO_MODIFY_DIRECTLY);
        if (notAllowedKeysRemoved) status.setRandom("Not allowed keys removed! Do not use this keys :<");

        // Если после фильтрации нет полей для обновления — возвращаем ошибку
        if (updates.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "no updates found (empty json object)");
        }

        // Применяем изменения
        OBJECT_MAPPER.updateValue(status, updates);
        return status;
    }
}
