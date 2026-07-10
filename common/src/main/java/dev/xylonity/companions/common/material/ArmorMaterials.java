package dev.xylonity.companions.common.material;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.knightlib.api.armor.KnightLibArmorMaterial;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;

public final class ArmorMaterials {

    public static void init() {
        ;;
    }

    public static final KnightLibArmorMaterial MAGE_STATS = fromConfig("mage", CompanionsConfig.MAGE_SET_STATS);
    public static final KnightLibArmorMaterial HOLY_ROBE_STATS = fromConfig("holy_robe", CompanionsConfig.HOLY_ROBE_SET_STATS);
    public static final KnightLibArmorMaterial CRYSTALLIZED_BLOOD_STATS = fromConfig("crystallized_blood", CompanionsConfig.CRYSTALLIZED_BLOOD_SET_STATS);

    public static final Holder<ArmorMaterial> MAGE = Holder.direct(MAGE_STATS.get());
    public static final Holder<ArmorMaterial> HOLY_ROBE = Holder.direct(HOLY_ROBE_STATS.get());
    public static final Holder<ArmorMaterial> CRYSTALLIZED_BLOOD = Holder.direct(CRYSTALLIZED_BLOOD_STATS.get());

    private static KnightLibArmorMaterial fromConfig(String name, String configEntry) {
        final String[] parts = configEntry.trim().split("\\s*,\\s*");
        if (parts.length != 7) {
            throw new IllegalArgumentException("[Companions] Invalid armor stats: " + configEntry);
        }

        return KnightLibArmorMaterial.builder(name, Companions.MOD_ID)
                .defense(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])
                )
                .toughness(Float.parseFloat(parts[4]))
                .knockbackResistance(Float.parseFloat(parts[5]))
                .durabilityMultiplier(Integer.parseInt(parts[6]))
                .enchantmentValue(20)
                .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
                .repairItem(Items.DIAMOND)
                .build();
    }

}
