package dev.xylonity.companions.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum CorneliusCurrency {
    COPPER,
    NETHER,
    END;

    private volatile ParsedCurrency cachedCurrency;

    public static boolean accepts(ItemStack stack) {
        for (final CorneliusCurrency currency : values()) {
            if (currency.matches(stack)) {
                return true;
            }

        }

        return false;
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        final ParsedCurrency parsed = parseConfig();
        for (final Item item : parsed.items()) {
            if (stack.is(item)) {
                return true;
            }

        }

        for (final TagKey<Item> tag : parsed.tags()) {
            if (stack.is(tag)) {
                return true;
            }

        }

        return false;
    }

    private ParsedCurrency parseConfig() {
        String raw = switch (this) {
            case COPPER -> CompanionsConfig.CORNELIUS_COPPER_CURRENCY;
            case NETHER -> CompanionsConfig.CORNELIUS_NETHER_CURRENCY;
            case END -> CompanionsConfig.CORNELIUS_END_CURRENCY;
        };

        if (raw == null) {
            raw = "";
        }

        final ParsedCurrency previous = cachedCurrency;
        if (previous != null && raw.equals(previous.config())) {
            return previous;
        }

        final List<Item> items = new ArrayList<>();
        final List<TagKey<Item>> tags = new ArrayList<>();

        for (final String part : raw.split(";")) {
            final String entry = part.trim();
            if (entry.isEmpty()) {
                continue;
            }

            final boolean isTag = entry.startsWith("#");
            final ResourceLocation id = ResourceLocation.tryParse(isTag ? entry.substring(1).trim() : entry);
            if (id == null) {
                continue;
            }

            if (isTag) {
                tags.add(TagKey.create(Registries.ITEM, id));
            }
            else {
                BuiltInRegistries.ITEM.getOptional(id).ifPresent(items::add);
            }

        }

        final ParsedCurrency parsed = new ParsedCurrency(raw, List.copyOf(items), List.copyOf(tags));

        cachedCurrency = parsed;

        return parsed;
    }

    private record ParsedCurrency(
            String config,
            List<Item> items,
            List<TagKey<Item>> tags
    ) {
        ;;
    }

}
