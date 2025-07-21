package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.List;
import java.util.Random;

public class FloorCakeCreamProjectile extends BaseProjectile implements GeoEntity {
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(FloorCakeCreamProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> ARMOR_NAME = SynchedEntityData.defineId(FloorCakeCreamProjectile.class, EntityDataSerializers.STRING);

    public FloorCakeCreamProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!this.level().isClientSide) {
            this.entityData.set(SIZE, new Random().nextFloat(1, 2));
            this.setLifetime(new Random().nextInt(80, 180));
        }
    }

    @Override
    public void tick() {

        super.tick();

        AABB effectArea = new AABB(
                getX() - getSize(), getY() - 0.5, getZ() - getSize(),
                getX() + getSize(), getY() + 0.5, getZ() + getSize()
        );

        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, effectArea);

        for (LivingEntity entity : entities) {
            if (getOwner() != null && !Util.areEntitiesLinked(entity, this)) {
                Vec3 v = entity.getDeltaMovement();
                entity.setDeltaMovement(v.x * 0.2, v.y, v.z * 0.2);
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
            } else {
                switch (getArmorName()) {
                    case "chocolate":
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 0));
                        break;
                    case "strawberry":
                        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0));
                        break;
                    case "vanilla":
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 0));
                        break;
                    default:
                        break;
                }
            }

        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ArmorName")) {
            setArmorName(pCompound.getString("ArmorName"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("ArmorName", getArmorName());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, 1.2f);
        builder.define(ARMOR_NAME, "default");
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void setArmorName(String name) {
        this.entityData.set(ARMOR_NAME, name);
    }

    public String getArmorName() {
        return this.entityData.get(ARMOR_NAME);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    protected int baseLifetime() {
        return 200;
    }

}
