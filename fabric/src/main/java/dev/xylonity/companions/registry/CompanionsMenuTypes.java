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
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CompanionsMenuTypes {

    public static void init() { ;; }

    public static final MenuType<SoulFurnaceContainerMenu> SOUL_FURNACE =
            ScreenHandlerRegistry.registerExtended(
                    new ResourceLocation(Companions.MOD_ID, "soul_furnace_container_menu"),
                    (syncId, inv, buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        BlockEntity be = inv.player.level().getBlockEntity(pos);
                        if (be instanceof SoulFurnaceBlockEntity furnace) {
                            return new SoulFurnaceContainerMenu(syncId, inv, furnace, new SimpleContainerData(3));
                        }

                        throw new IllegalStateException("TileEntity en " + pos + " no es SoulFurnace");
                    }
            );

    public static final MenuType<SoulMageContainerMenu> SOUL_MAGE_CONTAINER =
            ScreenHandlerRegistry.registerExtended(
                    new ResourceLocation(Companions.MOD_ID, "soul_mage_container_menu"),
                    (syncId, inv, buf) -> {
                        int entityId = buf.readInt();
                        Entity e = inv.player.level().getEntity(entityId);
                        if (e instanceof SoulMageEntity mage) {
                            return new SoulMageContainerMenu(syncId, inv, mage);
                        }

                        throw new IllegalStateException("Entidad " + entityId + " no es SoulMage");
                    }
            );

    public static final MenuType<PuppetContainerMenu> PUPPET_CONTAINER =
            ScreenHandlerRegistry.registerExtended(
                    new ResourceLocation(Companions.MOD_ID, "puppet_container_menu"),
                    (syncId, inv, buf) -> {
                        int entityId = buf.readInt();
                        Entity e = inv.player.level().getEntity(entityId);
                        if (e instanceof PuppetEntity puppet) {
                            return new PuppetContainerMenu(syncId, inv, puppet);
                        }

                        throw new IllegalStateException("Entidad " + entityId + " no es Puppet");
                    }
            );

    public static final MenuType<CorneliusContainerMenu> CORNELIUS_CONTAINER =
            ScreenHandlerRegistry.registerExtended(
                    new ResourceLocation(Companions.MOD_ID, "cornelius_container_menu"),
                    (syncId, inv, buf) -> {
                        int entityId = buf.readInt();
                        Entity e = inv.player.level().getEntity(entityId);
                        if (e instanceof CorneliusEntity cor) {
                            return new CorneliusContainerMenu(syncId, inv, cor);
                        }

                        throw new IllegalStateException("Entidad " + entityId + " no es Cornelius");
                    }
            );

}
