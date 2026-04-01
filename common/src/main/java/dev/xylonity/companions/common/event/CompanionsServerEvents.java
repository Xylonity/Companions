package dev.xylonity.companions.common.event;

import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import dev.xylonity.companions.common.entity.companion.*;
import dev.xylonity.companions.common.entity.hostile.*;
import dev.xylonity.companions.common.entity.projectile.PontiffFireRingProjectile;
import dev.xylonity.companions.common.entity.summon.*;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.knightlib.api.entity.data.PersistentData;
import dev.xylonity.knightlib.api.event.RegisterEvent;
import dev.xylonity.knightlib.api.event.impl.server.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.lang.ref.WeakReference;
import java.util.UUID;

public final class CompanionsServerEvents {

    @RegisterEvent
    public static void registerEntityAttributes(final EntityAttributeRegistrationEvent event) {
        event.register(CompanionsEntities.CORNELIUS, CorneliusEntity::setAttributes);
        event.register(CompanionsEntities.TEDDY, TeddyEntity::setAttributes);
        event.register(CompanionsEntities.ANTLION, AntlionEntity::setAttributes);
        event.register(CompanionsEntities.DINAMO, DinamoEntity::setAttributes);
        event.register(CompanionsEntities.BROKEN_DINAMO, BrokenDinamoEntity::setAttributes);
        event.register(CompanionsEntities.MINION, MinionEntity::setAttributes);
        event.register(CompanionsEntities.GOLDEN_ALLAY, GoldenAllayEntity::setAttributes);
        event.register(CompanionsEntities.SOUL_MAGE, SoulMageEntity::setAttributes);
        event.register(CompanionsEntities.LIVING_CANDLE, LivingCandleEntity::setAttributes);
        event.register(CompanionsEntities.CROISSANT_DRAGON, CroissantDragonEntity::setAttributes);
        event.register(CompanionsEntities.PUPPET, PuppetEntity::setAttributes);
        event.register(CompanionsEntities.PUPPET_GLOVE, PuppetGloveEntity::setAttributes);
        event.register(CompanionsEntities.SHADE_SWORD, ShadeSwordEntity::setAttributes);
        event.register(CompanionsEntities.SHADE_MAW, ShadeMawEntity::setAttributes);
        event.register(CompanionsEntities.MANKH, MankhEntity::setAttributes);
        event.register(CompanionsEntities.CLOAK, CloakEntity::setAttributes);
        event.register(CompanionsEntities.ILLAGER_GOLEM, IllagerGolemEntity::setAttributes);
        event.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE, HostilePuppetGloveEntity::setAttributes);
        event.register(CompanionsEntities.SACRED_PONTIFF, SacredPontiffEntity::setAttributes);
        event.register(CompanionsEntities.WILD_ANTLION, WildAntlionEntity::setAttributes);
        event.register(CompanionsEntities.HOSTILE_IMP, HostileImpEntity::setAttributes);
        event.register(CompanionsEntities.FIREWORK_TOAD, FireworkToadEntity::setAttributes);
        event.register(CompanionsEntities.NETHER_BULLFROG, NetherBullfrogEntity::setAttributes);
        event.register(CompanionsEntities.ENDER_FROG, EnderFrogEntity::setAttributes);
        event.register(CompanionsEntities.EMBER_POLE, EmberPoleEntity::setAttributes);
        event.register(CompanionsEntities.BUBBLE_FROG, BubbleFrogEntity::setAttributes);
    }

    @RegisterEvent
    public static void registerSpawnPlacements(final SpawnPlacementRegistrationEvent event) {
        event.register(CompanionsEntities.GOLDEN_ALLAY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoldenAllayEntity::checkGoldenAllaySpawnRules);
        event.register(CompanionsEntities.WILD_ANTLION.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WildAntlionEntity::checkMonsterSpawnRules);
        event.register(CompanionsEntities.CORNELIUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CorneliusEntity::checkCorneliusSpawnRules);
    }

    @RegisterEvent
    public static void onEntityLeaveLevelEvent(final ServerEntityLeaveLevelEvent event) {
        CompanionsEntityTracker.ENTITIES.remove(event.getEntity().getUUID());
    }

    @RegisterEvent
    public static void onEntityJoinLevelEvent(final ServerEntityJoinLevelEvent event) {
        CompanionsEntityTracker.ENTITIES.put(event.getEntity().getUUID(), new WeakReference<>(event.getEntity()));
    }

    @RegisterEvent
    public static void onLivingHurt(final LivingHurtEvent event) {
        final LivingEntity livingEntity = event.getEntity();

        final float amount = event.getAmount();

        final int amountHoly = Util.hasFullSetOn(livingEntity, ArmorMaterials.HOLY_ROBE.get());
        final int amountMage = Util.hasFullSetOn(livingEntity, ArmorMaterials.MAGE.get());
        final int amountBlood = Util.hasFullSetOn(livingEntity, ArmorMaterials.CRYSTALLIZED_BLOOD.get());

        // Crystallized blood set reduction
        if (amountBlood != 0 && livingEntity.getHealth() <= livingEntity.getMaxHealth() * CompanionsConfig.CRYSTALLIZED_BLOOD_SET_MIN_HEALTH) {
            float reduction = (float) CompanionsConfig.CRYSTALLIZED_BLOOD_SET_REDUCTION * amountBlood;
            event.setAmount(applyDamageRedution(amount, reduction));
        }

        // Magic set reduction
        if (amountMage != 0 && event.getSource().is(DamageTypes.MAGIC)) {
            float reduction = (float) CompanionsConfig.MAGE_SET_DAMAGE_REDUCTION * amountMage;
            event.setAmount(applyDamageRedution(amount, reduction));
        }

        // Holy robe set reduction
        if (amountHoly != 0) {
            float reduction = (float) CompanionsConfig.HOLY_ROBE_DAMAGE_REDUCTION * amountHoly;
            event.setAmount(applyDamageRedution(amount, reduction));

            // Ocassionally summons a fire ring
            if (livingEntity.getRandom().nextFloat() <= CompanionsConfig.HOLY_ROBE_FIRE_RING_SPAWN_CHANCE * amountHoly) {
                if (!livingEntity.level().isClientSide && (event.getSource().getEntity() != null || event.getSource().is(DamageTypes.EXPLOSION))) {
                    PontiffFireRingProjectile ring = CompanionsEntities.PONTIFF_FIRE_RING.get().create(livingEntity.level());
                    if (ring != null) {
                        ring.moveTo(livingEntity.position());
                        ring.setOwner(livingEntity);
                        livingEntity.level().addFreshEntity(ring);
                    }
                }
            }

        }

    }

    private static float applyDamageRedution(float amount, float reduction) {
        return amount * (1f - reduction);
    }

    @RegisterEvent
    public static void onLootTableModify(final LootTableModifyEvent event) {
        if (event.isChestTable() && event.getId().getNamespace().equals("minecraft")) {
            float chance;
            NumberProvider count;
            Item coin;
            String path = event.getId().getPath();
            if (path.contains("nether")) {
                chance = 0.45f;
                count = UniformGenerator.between(1, 3);
                coin = CompanionsBlocks.NETHER_COIN.get().asItem();
            }
            else if (path.contains("end")) {
                chance = 0.8f;
                count = ConstantValue.exactly(1);
                coin = CompanionsBlocks.END_COIN.get().asItem();
            }
            else {
                chance = 0.075f;
                count = UniformGenerator.between(1, 5);
                coin = CompanionsBlocks.COPPER_COIN.get().asItem();
            }

            final LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(coin)
                            .apply(SetItemCountFunction.setCount(count))
                            .when(LootItemRandomChanceCondition.randomChance(chance)))
                    .add(LootItem.lootTableItem(CompanionsItems.BOOK_BLACK_HOLE.get())
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .when(LootItemRandomChanceCondition.randomChance(0.045f)))
                    .add(LootItem.lootTableItem(CompanionsItems.BOOK_MAGIC_RAY.get())
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .when(LootItemRandomChanceCondition.randomChance(0.045f)));

            event.addPool(pool);
        }

    }

    @RegisterEvent
    public static void onDeath(final LivingDeathEvent event) {
        final Entity entity = event.getEntity();

        final CompoundTag entityTag = PersistentData.get(entity);
        if (!entityTag.contains("RespawnTotemPos")) {
            return;
        }
        if (!entityTag.contains("RespawnTotemDim")) {
            return;
        }

        final BlockPos respawnTotemPos = BlockPos.of(entityTag.getLong("RespawnTotemPos"));
        final String dimensionId = entityTag.getString("RespawnTotemDim");

        MinecraftServer minecraftServer = entity.level().getServer();
        if (minecraftServer == null) {
            return;
        }

        // Search for the original level the respawn totem is located into
        ServerLevel totemLevel = minecraftServer.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimensionId)));
        if (totemLevel == null) {
            return;
        }
        if (!(totemLevel.getBlockEntity(respawnTotemPos) instanceof RespawnTotemBlockEntity totem)) {
            return;
        }

        if (totem.getCharges() <= 0) {
            totem.savedEntities.remove(entity.getUUID());
            totem.setChanged();
            return;
        }

        // Clears/updates some nbts
        CompoundTag nbt = new CompoundTag();
        entity.save(nbt);
        nbt.remove("DeathTime");
        nbt.remove("HurtByTimestamp");
        nbt.remove("HurtTime");
        nbt.remove("FallFlying");
        nbt.remove("Motion");
        nbt.putFloat("Health", 1f);

        totem.queueRespawn(nbt, 20);
        totem.setChanged();

        if (entity instanceof TamableAnimal tame) {
            final UUID ownerId = tame.getOwnerUUID();
            if (ownerId != null) {
                final Player owner = totemLevel.getPlayerByUUID(ownerId);
                if (owner != null) {
                    owner.sendSystemMessage(Component.translatable("respawn_totem.companions.charges_remaining", totem.getCharges() - 1));
                }

            }

        }

    }

    @RegisterEvent
    public static void onServerWorldUnload(final ServerWorldUnloadEvent event) {
        TeslaNetwork.clearAll();
    }

}