package dev.xylonity.companions.config;

import java.util.HashMap;
import java.util.Map;

public class CompanionsConfig {

    public static final ConfigEntry<Integer> DINAMO_MAX_RECEIVER_CONNECTIONS = ConfigRegistry.of("DINAMO_MAX_RECEIVER_CONNECTIONS", 7);
    public static final ConfigEntry<Boolean> DINAMO_RECEIVER_REDSTONE_MODE = ConfigRegistry.of("DINAMO_RECEIVER_REDSTONE_MODE", true);

    public static final ConfigEntry<Double> FIRE_MARK_EFFECT_RADIUS = ConfigRegistry.of("FIRE_MARK_EFFECT_RADIUS", 2.5D);
    public static final ConfigEntry<Double> HEAL_RING_HEALING = ConfigRegistry.of("HEAL_RING_HEALING", 6D);

    public static final ConfigEntry<Integer> CROISSANT_EGG_LIFETIME = ConfigRegistry.of("CROISSANT_EGG_LIFETIME", 6000);

    public static class ConfigEntry<T> {
        private final String key;
        private final T defaultValue;
        private T value;

        public ConfigEntry(String key, T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public String getName() {
            return key;
        }

        public T getDefaultValue() {
            return defaultValue;
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
        }

    }

    protected static class ConfigRegistry {
        private static final Map<String, ConfigEntry<?>> entries = new HashMap<>();

        public static <T> ConfigEntry<T> of(String key, T defaultValue) {
            ConfigEntry<T> entry = new ConfigEntry<>(key, defaultValue);
            entries.put(key, entry);
            return entry;
        }

        public static ConfigEntry<?> getEntry(String key) {
            return entries.get(key);
        }

    }

}
