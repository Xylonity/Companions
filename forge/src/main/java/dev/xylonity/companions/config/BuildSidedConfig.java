package dev.xylonity.companions.config;

import dev.xylonity.companions.config.api.AutoConfig;
import dev.xylonity.companions.config.api.ConfigEntry;
import dev.xylonity.companions.config.impl.ConfigManager;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// Abstraction of the config api to use ForgeConfigSpec, so any mod that adds a general config GUI,
// such as Configured, detects this config. This should also enable hot-reloading
@Deprecated
public final class BuildSidedConfig {
    private static final Map<Field, ForgeConfigSpec.ConfigValue<?>> VALUES = new ConcurrentHashMap<>();

    public static void of(IEventBus modBus, Class<?> clazz) {
        // Let's create the default config from the specified config class
        ConfigManager.init(FMLPaths.CONFIGDIR.get(), clazz);

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        makeConfig(builder, clazz);

        ForgeConfigSpec spec = builder.build();

        // TODO: Default config registry type is hardcoded to COMMON
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, specName(clazz));

        // On the (re)load stage of the config directory, we reapply the changes so the config file contains the newest changes
        modBus.addListener((ModConfigEvent.Loading e) -> applyFromSpec());
        modBus.addListener((ModConfigEvent.Reloading e) -> {
            applyFromSpec();
            ConfigManager.init(FMLPaths.CONFIGDIR.get(), clazz);
        });
    }

    private static String specName(Class<?> clazz) {
        return clazz.getAnnotation(AutoConfig.class).file() + ".toml";
    }

    private static void makeConfig(ForgeConfigSpec.Builder b, Class<?> clazz) {
        // We sort by category so the entry insertion order won't matter at all
        Map<String, List<Field>> byCategory = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(ConfigEntry.class))
                .collect(Collectors.groupingBy(f -> f.getAnnotation(ConfigEntry.class).category()));

        for (var entry : byCategory.entrySet()) {
            String category = entry.getKey();
            if (!category.isEmpty()) b.push(category);

            for (Field f : entry.getValue()) {
                ConfigEntry meta = f.getAnnotation(ConfigEntry.class);
                f.setAccessible(true);
                // We 'simulate' the actual entry typedef for the config stack
                ForgeConfigSpec.ConfigValue<?> v = defineValue(b, f, meta);
                VALUES.put(f, v);
            }

            if (!category.isEmpty()) b.pop();
        }
    }

    private static ForgeConfigSpec.ConfigValue<?> defineValue(ForgeConfigSpec.Builder b, Field field, ConfigEntry meta) {
        try {
            String name = field.getName();
            b.comment(meta.comment());
            Class<?> type = field.getType();
            if (type == int.class) {
                return b.defineInRange(name, field.getInt(null), (int) meta.min(), (int) meta.max());
            }

            if (type == double.class) {
                return b.defineInRange(name, field.getDouble(null), meta.min(), meta.max());
            }

            if (type == float.class) {
                return b.defineInRange(name, field.getFloat(null), (float) meta.min(), (float) meta.max());
            }

            if (type == long.class) {
                return b.defineInRange(name, field.getLong(null), (long) meta.min(), (long) meta.max());
            }

            if (type == boolean.class) {
                return b.define(name, field.getBoolean(null));
            }

            return b.define(name, field.get(null));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static void applyFromSpec() {
        VALUES.forEach((field, value) -> {
            try {
                field.set(null, value.get());
            } catch (Exception ignore) { ;; }
        });
    }

}