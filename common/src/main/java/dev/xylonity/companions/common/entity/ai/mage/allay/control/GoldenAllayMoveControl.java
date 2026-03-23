package dev.xylonity.companions.common.entity.ai.mage.allay.control;

import dev.xylonity.companions.common.entity.companion.GoldenAllayEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class GoldenAllayMoveControl extends MoveControl {
    private final GoldenAllayEntity allay;

    public GoldenAllayMoveControl(GoldenAllayEntity allay) {
        super(allay);
        this.allay = allay;
    }

    public void tick() {
        if (this.operation == Operation.WAIT) {
            allay.setDeltaMovement(Vec3.ZERO);
            return;
        }

        if (this.operation == Operation.MOVE_TO) {
            Vec3 vv = new Vec3(this.wantedX - allay.getX(), this.wantedY - allay.getY(), this.wantedZ - allay.getZ());
            double length = vv.length();
            if (length < allay.getBoundingBox().getSize()) {
                this.operation = Operation.WAIT;
                allay.setDeltaMovement(allay.getDeltaMovement().scale(0.5));
            } else {
                allay.setDeltaMovement(allay.getDeltaMovement().add(vv.scale(this.speedModifier * 0.05 / length)));
                if (allay.getTarget() == null) {
                    Vec3 mov = allay.getDeltaMovement();
                    allay.setYRot(-((float) Mth.atan2(mov.x, mov.z)) * 57.295776F);
                } else {
                    double x = allay.getTarget().getX() - allay.getX();
                    double z = allay.getTarget().getZ() - allay.getZ();
                    allay.setYRot(-((float)Mth.atan2(x, z)) * 57.295776F);
                }
                allay.yBodyRot = allay.getYRot();
            }

        }
    }

}