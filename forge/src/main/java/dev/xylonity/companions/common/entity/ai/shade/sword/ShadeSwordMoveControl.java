package dev.xylonity.companions.common.entity.ai.shade.sword;

import dev.xylonity.companions.common.entity.companion.ShadeSwordEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class ShadeSwordMoveControl extends MoveControl {
    private final ShadeSwordEntity teddy;

    public ShadeSwordMoveControl(ShadeSwordEntity teddy) {
        super(teddy);
        this.teddy = teddy;
    }

    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            Vec3 $$0 = new Vec3(this.wantedX - teddy.getX(), this.wantedY - teddy.getY(), this.wantedZ - teddy.getZ());
            double $$1 = $$0.length();
            if ($$1 < teddy.getBoundingBox().getSize()) {
                this.operation = Operation.WAIT;
                teddy.setDeltaMovement(teddy.getDeltaMovement().scale(0.5));
            } else {
                teddy.setDeltaMovement(teddy.getDeltaMovement().add($$0.scale(this.speedModifier * 0.05 / $$1)));
                if (teddy.getTarget() == null) {
                    Vec3 $$2 = teddy.getDeltaMovement();
                    teddy.setYRot(-((float) Mth.atan2($$2.x, $$2.z)) * 57.295776F);
                } else {
                    double $$3 = teddy.getTarget().getX() - teddy.getX();
                    double $$4 = teddy.getTarget().getZ() - teddy.getZ();
                    teddy.setYRot(-((float)Mth.atan2($$3, $$4)) * 57.295776F);
                }
                teddy.yBodyRot = teddy.getYRot();
            }

        }
    }
}