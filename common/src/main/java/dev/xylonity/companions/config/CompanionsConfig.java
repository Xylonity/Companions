package dev.xylonity.companions.config;

import java.util.HashMap;
import java.util.Map;

public class CompanionsConfig {

    public static final ConfigEntry<Integer> DINAMO_MAX_RECEIVER_CONNECTIONS = ConfigRegistry.of("DINAMO_MAX_RECEIVER_CONNECTIONS", 3);

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

    public static class ConfigRegistry {
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
