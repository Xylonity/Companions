package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import dev.xylonity.companions.common.container.CorneliusContainerMenu;
import dev.xylonity.companions.common.container.PuppetContainerMenu;
import dev.xylonity.companions.common.container.SoulFurnaceContainerMenu;
import dev.xylonity.companions.common.container.SoulMageContainerMenu;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CompanionsMenuTypes {

    public static final ResourceRegistry<MenuType<?>> MENUS = ResourceDispatcher.create(BuiltInRegistries.MENU, Companions.MOD_ID);

    public static final ResourceEntry<MenuType<SoulFurnaceContainerMenu>> SOUL_FURNACE_MENU =
            MENUS.registerMenu("soul_furnace_container_menu", (syncId, playerInv, buf) -> {
                final BlockPos pos = buf.readBlockPos();
                final BlockEntity blockEntity = playerInv.player.level().getBlockEntity(pos);
                if (blockEntity instanceof SoulFurnaceBlockEntity furnace) {
                    return new SoulFurnaceContainerMenu(syncId, playerInv, furnace, new SimpleContainerData(3));
                }

                throw new IllegalStateException("[Companions!] Tried to open a menu for a wrong entity: " + syncId);
            });

    public static final ResourceEntry<MenuType<SoulMageContainerMenu>> SOUL_MAGE_MENU =
            MENUS.registerMenu("soul_mage_container_menu", (syncId, playerInv, buf) -> {
                final int entityId = buf.readInt();
                final Entity entity = playerInv.player.level().getEntity(entityId);
                if (entity instanceof SoulMageEntity mage) {
                    return new SoulMageContainerMenu(syncId, playerInv, mage);
                }

                throw new IllegalStateException("[Companions!] Tried to open a menu for a wrong entity: " + syncId);
            });

    public static final ResourceEntry<MenuType<PuppetContainerMenu>> PUPPET_MENU =
            MENUS.registerMenu("puppet_container_menu", (syncId, playerInv, buf) -> {
                final int entityId = buf.readInt();
                final Entity entity = playerInv.player.level().getEntity(entityId);
                if (entity instanceof PuppetEntity puppet) {
                    return new PuppetContainerMenu(syncId, playerInv, puppet);
                }

                throw new IllegalStateException("[Companions!] Tried to open a menu for a wrong entity: " + syncId);
            });

    public static final ResourceEntry<MenuType<CorneliusContainerMenu>> CORNELIUS_MENU =
            MENUS.registerMenu("cornelius_container_menu", (syncId, playerInv, buf) -> {
                final int entityId = buf.readInt();
                final Entity entity = playerInv.player.level().getEntity(entityId);
                if (entity instanceof CorneliusEntity cor) {
                    return new CorneliusContainerMenu(syncId, playerInv, cor);
                }

                throw new IllegalStateException("[Companions!] Tried to open a menu for a wrong entity: " + syncId);
            });

}
