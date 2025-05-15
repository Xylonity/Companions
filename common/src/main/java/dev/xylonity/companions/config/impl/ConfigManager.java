package dev.xylonity.companions.config.impl;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.config.api.*;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public final class ConfigManager {
    private static Path CONFIG_DIR = Path.of("config");
    private static final Set<Class<?>> REGISTERED = new HashSet<>();

    public static void init(Path configDir, Class<?>... configs) {
        CONFIG_DIR = configDir;
        for (Class<?> clazz : configs) {
            loadOrCreate(clazz);
        }
    }

    private static void loadOrCreate(Class<?> clazz) {
        if (!REGISTERED.add(clazz)) return;

        AutoConfig meta = clazz.getAnnotation(AutoConfig.class);
        if (meta == null) return;

        DecorationType style = meta.style();
        String fileName = meta.file() + ".toml";
        Path tomlPath = CONFIG_DIR.resolve(fileName);

        CommentedFileConfig cfg = CommentedFileConfig.builder(tomlPath, TomlFormat.instance()).autosave().preserveInsertionOrder().sync().build();

        // We create the dedicated config file
        cfg.load();

        // 'Cache' to avoid category duplication
        Set<String> seenCats = new HashSet<>();

        // Parses every single entry no matter what the order is, and we avoid category duplication (comment above)
        for (Field field : clazz.getDeclaredFields()) {
            ConfigEntry e = field.getAnnotation(ConfigEntry.class);
            if (e == null) continue;

            field.setAccessible(true);

            String category = e.category();
            // We search the entry name through reflexion, it's not saved directly inside the annotation per se
            String entry = field.getName();
            String path = category.isEmpty() ? entry : category + "." + entry;

            String target = category.isEmpty() ? entry : category;
            if (seenCats.add(category)) {
                if (meta.categoryBanner()) {
                    // We add a comment above the category's name
                    cfg.setComment(target, wrapAndIndent(buildCategoryBanner(category, style)));
                } else {
                    // Or we set a blank comment if the categoryBanner is off
                    cfg.setComment(target, "");
                }
            }

            Object def;
            try {
                // We access the value (the default one) from the static att this way
                def = field.get(null);
            } catch (Exception exception) {
                continue;
            }

            if (!cfg.contains(path)) {
                cfg.set(path, def);
            }

            // Now we build the current entry
            String entryComment = buildEntryComment(e, def, style);
            cfg.setComment(path, wrapAndIndent(entryComment));

            Object raw = cfg.get(path);
            // Obtains the min/max value to avoid crashes in case the player decides to break the limits of the entry
            Object val = clamp(raw, e, field.getType());
            try {
                // If we set the value directly, the compiler would probably throw an exception, so we check the primitive type
                // the original value belongs to
                setPrimitive(field, val);
            } catch (Exception exception) {
                CompanionsCommon.LOGGER.error("[CONFIG] Couldn't assign {}: {}", entry, exception.getMessage());
            }
        }

        cfg.save();
    }

    public static String buildCategoryBanner(String cat, DecorationType style) {
        String title = (cat.isEmpty() ? "GENERAL" : cat.toUpperCase()) + " SETTINGS";
        return switch (style) {
            case VERBOSE -> {
                String line = "=".repeat(70);
                yield String.join("\n", line, centerText(title, 70), line);
            }
            case RUSTIC -> ">>>> [" + title + "] <<<<";
            case STARSET -> {
                String line = "=".repeat(70);
                yield String.join("\n", line, centerText("«✦»  " + title + "  «✦»", 70), line);
            }
            default -> title.toLowerCase().replace(" settings", "") + " §§";
        };

    }

    public static String buildEntryComment(ConfigEntry entry, Object defaultRawValue, DecorationType style) {
        String base = entry.comment().trim();
        String note = entry.note().trim();

        // We check the kind of value defaultValue is, and thus we add decimals or not
        boolean isDouble = defaultRawValue instanceof Double;
        String defaultValue = isDouble ? hasDecimals(((Number) defaultRawValue).doubleValue(), true) : defaultRawValue.toString();
        String minValue = hasDecimals(entry.min(), isDouble);
        String maxValue = hasDecimals(entry.max(), isDouble);

        // We prevent printing infinity caps if the entry is a boolean
        boolean showRange = !(defaultRawValue instanceof Boolean);

        switch (style) {
            case VERBOSE -> {
                String border = "-".repeat(64);
                StringBuilder sb = new StringBuilder(border);
                sb.append("\n").append(base).append("\n");

                if (!note.isEmpty()) {
                    sb.append("\nNote: ").append(note).append("\n");
                }

                sb.append("\n- Default: ").append(defaultValue);

                if (showRange) {
                    sb.append("\n").append("- Range: " + minValue + " ~ " + maxValue);
                }

                sb.append("\n").append(border);

                return sb.toString();
            }
            case RUSTIC -> {
                StringBuilder sb = new StringBuilder();
                sb.append("$> ").append(base).append("\n")
                        .append("$> Default: ").append(defaultValue);

                if (showRange) {
                    sb.append(" | Min: " + minValue + " ~ Max: " + maxValue);
                }

                if (!note.isEmpty()) {
                    sb.append("\n$> Note: ").append(note);
                }

                return sb.toString();
            }
            case STARSET -> {
                StringBuilder b = new StringBuilder();
                b.append("✦ ").append(base).append("\n")
                        .append("✦ Default: ").append(defaultValue);

                if (showRange) {
                    b.append(" | Range: ").append(minValue).append(" -~- ").append(maxValue);
                }

                if (!note.isEmpty()) {
                    b.append("\n✦ Note: ").append(note);
                }

                return b.toString();
            }
            default -> { // SIMPLE
                StringBuilder b = new StringBuilder(base)
                        .append("\n\nDefault: ").append(defaultValue);

                if (showRange) {
                    b.append("\nRange: ").append(minValue).append(" ~ ").append(maxValue);
                }

                if (!note.isEmpty()) {
                    b.append("\n\nNote: ").append(note);
                }

                return b.toString();
            }
        }

    }

    private static String hasDecimals(double d, boolean forceDecimal) {
        if (Double.isInfinite(d) || Double.isNaN(d)) return Double.toString(d);

        long asLong = (long) d;

        if (d == asLong) {
            return forceDecimal ? asLong + ".0" : Long.toString(asLong);
        }

        return Double.toString(d);
    }

    public static String wrapText(String text) {
        // Parser that jumps to the next line each 100 characters (mostly used by normal comments)
        StringBuilder out = new StringBuilder();
        for (String paragraph : text.split("\n")) {
            String[] words = paragraph.split(" ");
            int col = 0;
            for (String w : words) {
                if (col + w.length() > 100) {
                    out.append("\n");
                    col = 0;
                } else if (col > 0) {
                    out.append(" ");
                    col++;
                }

                out.append(w);
                col += w.length();
            }

            out.append("\n");
        }

        return out.toString().trim();
    }

    private static String wrapAndIndent(String comment) {
        String wrapped = wrapText(comment);
        StringBuilder s = new StringBuilder();

        // We add an extra space before each comment line (banner comments too) so it looks better and reduces visual noise
        for (String line : wrapped.split("\n")) {
            s.append(" ").append(line).append("\n");
        }

        return s.substring(0, s.length() - 1);
    }

    private static String centerText(String text, int width) {
        int pad = (width - text.length())/2;
        return " ".repeat(Math.max(0, pad)) + text;
    }

    private static Object clamp(Object raw, ConfigEntry e, Class<?> type) {
        if (!(raw instanceof Number num)) return raw;

        double d = Math.max(e.min(), Math.min(e.max(), num.doubleValue()));
        return switch (type.getName()) {
            case "int" -> (int) d;
            case "long" -> (long) d;
            case "float" -> (float) d;
            case "double" -> d;
            default -> raw;
        };

    }

    private static void setPrimitive(Field f, Object v) throws Exception {
        switch (f.getType().getName()) {
            case "int" -> f.setInt(null, (Integer) v);
            case "long" -> f.setLong(null, (Long) v);
            case "float" -> f.setFloat(null, (Float) v);
            case "double" -> f.setDouble(null,(Double) v);
            case "boolean" -> f.setBoolean(null, (Boolean) v);
            default -> f.set(null, v);
        }

    }

}