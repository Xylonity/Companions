package dev.xylonity.companions.platform;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class CompanionsServices {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Companions.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}