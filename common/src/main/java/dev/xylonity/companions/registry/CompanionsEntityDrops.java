package dev.xylonity.companions.registry;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.tag.CompanionsTags;
import dev.xylonity.knightlib.api.loot.KnightLibLoot;

public class CompanionsEntityDrops {

    public static void init() {
        KnightLibLoot.builder()
                .tag(CompanionsTags.DEMON_FLESH_DROP)
                .chance(() -> (float) CompanionsConfig.DEMON_FLESH_DROP_RATE)
                .item(CompanionsItems.DEMON_FLESH)
                .submit();
    }

}
