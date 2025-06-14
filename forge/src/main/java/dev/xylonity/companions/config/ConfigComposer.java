package dev.xylonity.companions.config;

import dev.xylonity.companions.config.api.AutoConfig;
import dev.xylonity.companions.config.api.ConfigEntry;
import dev.xylonity.companions.config.api.DecorationType;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

// Abstraction of the config api to use ForgeConfigSpec, so any mod that adds a general config GUI,
// such as Configured, detects this config. This should also enable hot-reloading
@Deprecated
public final class ConfigComposer {
    private static final Map<Field, ForgeConfigSpec.ConfigValue<?>> VALUES = new ConcurrentHashMap<>();

    public static void registerConfig(Class<?> clazz, IEventBus modBus) {
        // Let's create the default config from the specified config class
        ConfigManager.init(FMLPaths.CONFIGDIR.get(), clazz);

        AutoConfig ac = clazz.getAnnotation(AutoConfig.class);
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        makeConfig(builder, clazz, ac.style(), ac.categoryBanner());

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

    private static void makeConfig(ForgeConfigSpec.Builder b, Class<?> clazz, DecorationType style, boolean categoryBanner) {
        // We sort by category so the entry insertion order won't matter at all
        Map<String, List<Field>> byCategory = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(ConfigEntry.class))
                .collect(Collectors.groupingBy(f -> f.getAnnotation(ConfigEntry.class).category()));

        for (var catEntry : byCategory.entrySet()) {
            String category = catEntry.getKey();
            if (!category.isEmpty()) {
                if (categoryBanner) {
                    String[] bannerLines = ConfigManager.buildCategoryBanner(category, style).split("\n");
                    b.comment(bannerLines);
                }

                b.push(category);
            }

            for (Field field : catEntry.getValue()) {
                ConfigEntry meta = field.getAnnotation(ConfigEntry.class);
                field.setAccessible(true);
                Object defaultVal;

                try {
                    defaultVal = field.get(null);
                } catch (IllegalAccessException e) {
                    defaultVal = null;
                }

                // Raw comments and such
                String rawComment = ConfigManager.buildEntryComment(meta, defaultVal, style);
                String wrapped = ConfigManager.wrapText(rawComment);
                b.comment(wrapped.split("\n"));

                // We 'simulate' the actual entry typedef for the config stack
                ForgeConfigSpec.ConfigValue<?> cfgValue = defineValue(b, field, meta);
                VALUES.put(field, cfgValue);
            }

            if (!category.isEmpty()) {
                b.pop();
            }
        }

    }

    private static ForgeConfigSpec.ConfigValue<?> defineValue(ForgeConfigSpec.Builder b, Field field, ConfigEntry entry) {
        try {
            String name = field.getName();
            Class<?> type = field.getType();

            // Cap predicates to hard bypass forge's stack adding duplicated comments
            Predicate<Object> inIntRange = v -> v instanceof Integer i && i >= entry.min() && i <= entry.max();
            Predicate<Object> inLongRange = v -> v instanceof Long l && l >= (long) entry.min() && l <= (long) entry.max();
            Predicate<Object> inFloatRange = v -> v instanceof Float f && f >= (float) entry.min() && f <= (float) entry.max();
            Predicate<Object> inDoubleRange = v -> v instanceof Double d && d >= entry.min() && d <= entry.max();

            if (type == int.class) {
                return b.define(name, field.getInt(null), inIntRange);
            }

            if (type == double.class) {
                return b.define(name, field.getDouble(null), inDoubleRange);
            }

            // float types may not work correctly on some gui config mods
            if (type == float.class) {
                return b.define(name, field.getFloat(null), inFloatRange);
            }

            if (type == long.class) {
                return b.define(name, field.getLong(null), inLongRange);
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