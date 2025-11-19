package net.kkah.redishomes.common.model;

import java.util.UUID;

public record TeleportRequest(
        UUID playerUuid,
        String serverId,
        String worldName,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {}