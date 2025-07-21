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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CompanionsMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Companions.MOD_ID);

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String name, MenuFactory<T> factory) {
        return MENU_TYPES.register(name,
                () -> IMenuTypeExtension.create(factory::create)
        );
    }

    @FunctionalInterface
    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int windowId, Inventory inv, FriendlyByteBuf extraData);
    }

    public static final Supplier<MenuType<SoulFurnaceContainerMenu>> SOUL_FURNACE =
            registerMenu("soul_furnace_container_menu", (id, inv, buf) -> {
                BlockPos pos = buf.readBlockPos();
                BlockEntity be = inv.player.level().getBlockEntity(pos);
                if (be instanceof SoulFurnaceBlockEntity furnace) {
                    return new SoulFurnaceContainerMenu(id, inv, furnace, new SimpleContainerData(3));
                }
                throw new IllegalStateException("" + pos);
            });

    public static final Supplier<MenuType<SoulMageContainerMenu>> SOUL_MAGE_CONTAINER =
            registerMenu("soul_mage_container_menu", (id, inv, buf) -> {
                int entityId = buf.readInt();
                Entity entity = inv.player.level().getEntity(entityId);
                if (entity instanceof SoulMageEntity soulMage) {
                    return new SoulMageContainerMenu(id, inv, soulMage);
                }
                throw new IllegalStateException("" + entityId);
            });

    public static final Supplier<MenuType<PuppetContainerMenu>> PUPPET_CONTAINER =
            registerMenu("puppet_container_menu", (id, inv, buf) -> {
                int entityId = buf.readInt();
                Entity entity = inv.player.level().getEntity(entityId);
                if (entity instanceof PuppetEntity puppet) {
                    return new PuppetContainerMenu(id, inv, puppet);
                }
                throw new IllegalStateException("" + entityId);
            });

    public static final Supplier<MenuType<CorneliusContainerMenu>> CORNELIUS_CONTAINER =
            registerMenu("cornelius_container_menu", (id, inv, buf) -> {
                int entityId = buf.readInt();
                Entity entity = inv.player.level().getEntity(entityId);
                if (entity instanceof CorneliusEntity puppet) {
                    return new CorneliusContainerMenu(id, inv, puppet);
                }
                throw new IllegalStateException("" + entityId);
            });

}
