package dev.xylonity.companions.common.entity.projectile.trigger;

import dev.xylonity.companions.common.entity.projectile.FloorCakeCreamProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class CakeCreamTriggerProjectile extends GenericTriggerProjectile {
    private final float GRAVITY = 0.015f;
    private static final EntityDataAccessor<String> ARMOR_NAME = SynchedEntityData.defineId(CakeCreamTriggerProjectile.class, EntityDataSerializers.STRING);

    public CakeCreamTriggerProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.onGround()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * 0.98, motion.y - GRAVITY, motion.z * 0.98);
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            FloorCakeCreamProjectile cakeCreamProjectile = CompanionsEntities.FLOOR_CAKE_CREAM.get().create(level());
            if (cakeCreamProjectile != null) {
                cakeCreamProjectile.moveTo(getX(), getY(), getZ());
                cakeCreamProjectile.setArmorName(getArmorName());
                level().addFreshEntity(cakeCreamProjectile);
            }

            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARMOR_NAME, "default");
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ArmorName")) {
            setArmorName(pCompound.getString("ArmorName"));
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("ArmorName", getArmorName());
    }

    public void setArmorName(String name) {
        this.entityData.set(ARMOR_NAME, name);
    }

    public String getArmorName() {
        return this.entityData.get(ARMOR_NAME);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
    }

}