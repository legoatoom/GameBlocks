package com.legoatoom.gameblocks;


import net.fabricmc.api.EnvType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameBlocksState {
    private GameBlocksState() {}

    public static final Logger LOGGER = LogManager.getFormatterLogger("GameBlocks");

    public static EnvType env;
    public static boolean isDev;

    public static void error(String reason) {
        if (env == EnvType.SERVER || isDev)
            throw new RuntimeException(reason);
        LOGGER.error(reason);
    }

    public static void warn(String reason) {
        LOGGER.warn(reason);
    }

    public static void info(String reason) {
        LOGGER.info(reason);
    }
}
