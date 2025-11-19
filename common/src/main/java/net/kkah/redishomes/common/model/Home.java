package net.kkah.redishomes.common.model;

import java.util.UUID;

public record Home(
        UUID owner,
        String name,
        String server,
        String world,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {}