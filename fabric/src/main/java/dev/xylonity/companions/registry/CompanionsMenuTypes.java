package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.container.CorneliusContainerMenu;
import dev.xylonity.companions.common.container.PuppetContainerMenu;
import dev.xylonity.companions.common.container.SoulFurnaceContainerMenu;
import dev.xylonity.companions.common.container.SoulMageContainerMenu;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class CompanionsMenuTypes {

    public static void init() { ;; }

    public static final MenuType<SoulFurnaceContainerMenu> SOUL_FURNACE =
            Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "cornelius_container_menu"),
                    new ExtendedScreenHandlerType<>(SoulFurnaceContainerMenu::new, BlockPos.STREAM_CODEC)
            );

    public static final MenuType<SoulMageContainerMenu> SOUL_MAGE_CONTAINER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "cornelius_container_menu"),
                    new ExtendedScreenHandlerType<>(
                            (syncId, inv, entityId) -> {
                                SoulMageEntity e = (SoulMageEntity) inv.player.level().getEntity(entityId);
                                if (e != null) {
                                    return new SoulMageContainerMenu(syncId, inv, e);
                                }

                                throw new IllegalStateException("[Companions!] Trying to load a menu on the wrong entity: " + entityId);
                            },
                            StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt)
                    )
            );

    public static final MenuType<PuppetContainerMenu> PUPPET_CONTAINER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "puppet_container_menu"),
                    new ExtendedScreenHandlerType<>(
                            (syncId, inv, entityId) -> {
                                PuppetEntity e = (PuppetEntity) inv.player.level().getEntity(entityId);
                                if (e != null) {
                                    return new PuppetContainerMenu(syncId, inv, e);
                                }

                                throw new IllegalStateException("[Companions!] Trying to load a menu on the wrong entity: " + entityId);
                            },
                            StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt)
                    )
            );

    public static final MenuType<CorneliusContainerMenu> CORNELIUS_CONTAINER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "cornelius_container_menu"),
                    new ExtendedScreenHandlerType<>(
                            (syncId, inv, entityId) -> {
                                CorneliusEntity e = (CorneliusEntity) inv.player.level().getEntity(entityId);
                                if (e != null) {
                                    return new CorneliusContainerMenu(syncId, inv, e);
                                }

                                throw new IllegalStateException("[Companions!] Trying to load a menu on the wrong entity: " + entityId);
                            },
                            StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt)
                    )
            );

}
