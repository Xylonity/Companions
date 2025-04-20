package dev.xylonity.companions.common.entity.ai.dinamo;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class DinamoSearchTargetsGoal extends Goal {
    private final DinamoEntity dinamo;
    private final int MAX_ATTACK_RADIUS;

    public DinamoSearchTargetsGoal(DinamoEntity dinamo, int radius) {
        this.dinamo = dinamo;
        this.MAX_ATTACK_RADIUS = radius;
    }

    @Override
    public boolean canUse() {
        if (this.dinamo.isTame() && this.dinamo.isActiveForAttack()) {
            LivingEntity owner = this.dinamo.getOwner();
            return owner != null;
        }

        return true;
    }

    @Override
    public void tick() {
        Vec3 pos = dinamo.position();
        AABB searchBox = new AABB(
                pos.x - MAX_ATTACK_RADIUS, pos.y - MAX_ATTACK_RADIUS, pos.z - MAX_ATTACK_RADIUS,
                pos.x + MAX_ATTACK_RADIUS, pos.y + MAX_ATTACK_RADIUS, pos.z + MAX_ATTACK_RADIUS
        );

        // We search for visible monsters
        // TODO: add extra conditions
        dinamo.visibleEntities =
                //dinamo.level().getEntitiesOfClass(LivingEntity.class, searchBox, e -> {
//
                //    if (e instanceof Player player) {
                //        return !player.isCreative() && !player.isSpectator() && player.equals(dinamo.getOwner());
                //    }
//
                //    if (dinamo.getOwner() != null && dinamo.getOwner().getLastHurtMob() == e) {
                //        return true;
                //    }
//
                //    if (e instanceof TamableAnimal) {
                //        return false;
                //    }
//
                //    return e instanceof Monster;
                //})
                //.stream().filter(dinamo::hasLineOfSight)
                //.collect(Collectors.toList());
                dinamo.level().getEntitiesOfClass(Monster.class, searchBox);

    }

    @Override
    public void start() {
        this.dinamo.visibleEntities.clear();
    }

}
