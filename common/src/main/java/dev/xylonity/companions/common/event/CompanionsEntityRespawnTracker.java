package dev.xylonity.companions.common.event;

import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import dev.xylonity.companions.common.entity.CompanionEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public final class CompanionsEntityRespawnTracker {
    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity dead, DamageSource ignored) -> {
            if (!(dead instanceof CompanionEntity companion)) return;
            long posLong = companion.getRespawnTotemPosLong();

            ResourceLocation dim = companion.getRespawnTotemDim();
            if (posLong == Long.MIN_VALUE || dim == null) return;

            MinecraftServer server = dead.level().getServer();
            if (server == null) return;

            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, dim);

            ServerLevel totemLevel = server.getLevel(key);
            if (!(totemLevel != null && totemLevel.getBlockEntity(BlockPos.of(posLong)) instanceof RespawnTotemBlockEntity totem)) return;

            if (totem.getCharges() <= 0) {
                totem.savedEntities.remove(dead.getUUID());
                totem.setChanged();
                return;
            }

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
            if (dead instanceof CompanionEntity tame) {
               UUID owner = tame.getOwnerUUID();
               if (owner != null) {
                   Player player = totemLevel.getPlayerByUUID(owner);
                   if (player != null) {
                       player.sendSystemMessage(Component.translatable("respawn_totem.companions.charges_remaining", totem.getCharges() - 1));
                   }
               }
            }

        });
    }

}
