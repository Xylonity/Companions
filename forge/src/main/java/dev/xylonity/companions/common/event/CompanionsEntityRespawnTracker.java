package dev.xylonity.companions.common.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import dev.xylonity.companions.common.tick.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Companions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CompanionsEntityRespawnTracker {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        Entity dead = event.getEntity();

        CompoundTag originalTag = dead.getPersistentData();
        if (!originalTag.contains("RespawnTotemPos")) return;
        if (!originalTag.contains("RespawnTotemDim")) return;

        BlockPos pos = BlockPos.of(originalTag.getLong("RespawnTotemPos"));
        String dimId = originalTag.getString("RespawnTotemDim");

        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimId));

        MinecraftServer mcServer = dead.level().getServer();
        if (mcServer == null) return;

        // We search for the original level the respawn totem is at
        ServerLevel totemLevel = mcServer.getLevel(dimKey);
        if (totemLevel == null) return;

        // Now we check if the totem exists in that position
        BlockEntity be = totemLevel.getBlockEntity(pos);
        if (!(be instanceof RespawnTotemBlockEntity totem)) return;

        // We clean irrelevant nbts
        CompoundTag nbt = new CompoundTag();
        dead.save(nbt);
        nbt.remove("DeathTime");
        nbt.remove("HurtByTimestamp");
        nbt.remove("HurtTime");
        nbt.remove("Health");
        nbt.remove("FallFlying");

        Entity respawned = EntityType.loadEntityRecursive(nbt, totemLevel, e -> {
            e.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            return e;
        });

        if (respawned == null) return;

        if (respawned instanceof TamableAnimal animal) {
            animal.setOrderedToSit(true);
        }

        // This SHOULD be delayed since the entity that triggers this event is still alive
        TickScheduler.scheduleServer(totemLevel, () -> totemLevel.addFreshEntity(respawned), 20);

        totem.setChanged();
    }

}
