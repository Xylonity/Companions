package dev.xylonity.companions.common.entity.ai.shade.sword;

import dev.xylonity.companions.common.entity.ShadeEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class ShadeSwordMoveControl extends MoveControl {
    private final ShadeEntity shadeEntity;

    public ShadeSwordMoveControl(ShadeEntity shadeEntity) {
        super(shadeEntity);
        this.shadeEntity = shadeEntity;
    }

    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            final Vec3 wantedPosition = new Vec3(this.wantedX - shadeEntity.getX(), this.wantedY - shadeEntity.getY(), this.wantedZ - shadeEntity.getZ());
            final double distance = wantedPosition.length();
            if (distance < shadeEntity.getBoundingBox().getSize()) {
                this.operation = Operation.WAIT;
                shadeEntity.setDeltaMovement(shadeEntity.getDeltaMovement().scale(0.5));
            }
            else {
                shadeEntity.setDeltaMovement(shadeEntity.getDeltaMovement().add(wantedPosition.scale(this.speedModifier * 0.05 / distance)));
                if (shadeEntity.getTarget() == null) {
                    final Vec3 deltaMovement = shadeEntity.getDeltaMovement();
                    shadeEntity.setYRot(-((float) Mth.atan2(deltaMovement.x, deltaMovement.z)) * 57.295776F);
                }
                else {
                    final double x = shadeEntity.getTarget().getX() - shadeEntity.getX();
                    final double z = shadeEntity.getTarget().getZ() - shadeEntity.getZ();
                    shadeEntity.setYRot(-((float)Mth.atan2(x, z)) * 57.295776F);
                }

                shadeEntity.yBodyRot = shadeEntity.getYRot();
            }

        }

    }

}