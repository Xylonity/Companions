package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import dev.xylonity.companions.common.blockentity.container.SoulFurnaceContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CompanionsMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Companions.MOD_ID);

    public static final RegistryObject<MenuType<SoulFurnaceContainerMenu>> SOUL_FURNACE =
            MENU_TYPES.register("soul_furnace_container_menu", () -> IForgeMenuType.create(
                    (int id, Inventory inv, FriendlyByteBuf buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        BlockEntity be = inv.player.level().getBlockEntity(pos);
                        if (be instanceof SoulFurnaceBlockEntity) {
                            return new SoulFurnaceContainerMenu(id, inv, (SoulFurnaceBlockEntity) be, new SimpleContainerData(3));
                        }
                        throw new IllegalStateException("Se esperaba un SoulFurnaceBlockEntity en " + pos);
                    }
            ));

}
