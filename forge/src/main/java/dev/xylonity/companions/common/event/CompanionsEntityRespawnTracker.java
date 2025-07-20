package dev.xylonity.companions.common.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

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

        MinecraftServer mcServer = dead.level().getServer();
        if (mcServer == null) return;

        // We search for the original level the respawn totem is at
        ServerLevel totemLevel = mcServer.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimId)));
        if (totemLevel == null) return;

        // Now we check if the totem exists in that position
        if (!(totemLevel.getBlockEntity(pos) instanceof RespawnTotemBlockEntity totem)) return;

        if (totem.getCharges() <= 0) {
            totem.savedEntities.remove(dead.getUUID());
            totem.setChanged();
            return;
        }

        // clear/update some nbts
        CompoundTag nbt = new CompoundTag();
        dead.save(nbt);
        nbt.remove("DeathTime");
        nbt.remove("HurtByTimestamp");
        nbt.remove("HurtTime");
        nbt.remove("FallFlying");
        nbt.remove("Motion");
        nbt.putFloat("Health", 1f);

        totem.queueRespawn(nbt, 20);
        totem.setChanged();

        if (dead instanceof TamableAnimal tame) {
            UUID ownerId = tame.getOwnerUUID();
            if (ownerId != null) {
                Player owner = totemLevel.getPlayerByUUID(ownerId);
                if (owner != null) {
                    owner.sendSystemMessage(Component.translatable("totem.companions.charges_remaining", pos.toShortString(), totem.getCharges()));
                }
            }
        }
    }

}
