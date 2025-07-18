package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.HolinessNaginataProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class NaginataBook extends AbstractMagicBook {

    public NaginataBook(Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "naginata_book";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable("tooltip.item.companions.naginata_book_2"));
        super.appendHoverText(itemStack, level, components, flag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 endPos = eyePos.add(player.getLookAngle().scale(80));

            AABB bb = new AABB(eyePos, endPos).inflate(2.0);

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, eyePos, endPos, bb, e -> !Util.areEntitiesLinked(player, e));
            if (entityHit == null) {
                HitResult blockHit = level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (blockHit.getType() == HitResult.Type.BLOCK) {
                    spawnNaginatas(player, blockHit.getLocation(), level);
                }
            } else {
                spawnNaginatas(player, entityHit.getEntity().position(), level);
            }

            player.getCooldowns().addCooldown(this, (int)(CompanionsConfig.NAGINATA_COOLDOWN * (1 - (Util.hasFullSetOn(player, ArmorMaterials.MAGE) * CompanionsConfig.MAGE_SET_COOLDOWN_REDUCTION))));
        }

        return super.use(level, player, hand);
    }

    @Override
    protected void playSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CompanionsSounds.SPELL_RELEASE_SPEARS.get(), player.getSoundSource(), 1.0F, 1.0F);
    }

    private void spawnNaginatas(Player player, Vec3 targetPos, Level level) {
        if (player.isShiftKeyDown()) {
            for (int i = 0; i < 2; i++) {
                double dx = (new Random().nextDouble() - 0.5) * 20;
                double dz = (new Random().nextDouble() - 0.5) * 20;
                double x = targetPos.x + dx;
                double z = targetPos.z + dz;
                Vec3 spawnPos = new Vec3(x, 20 + targetPos.y + new Random().nextDouble() * 10, z);
                spawnNaginata(player, spawnPos, targetPos, level, 3.35);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                double dx = (new Random().nextDouble() - 0.5) * 30;
                double dz = (new Random().nextDouble() - 0.5) * 30;
                double x = targetPos.x + dx;
                double z = targetPos.z + dz;
                Vec3 spawnPos = new Vec3(x, 20 + targetPos.y + new Random().nextDouble() * 10, z);
                spawnNaginata(player, spawnPos, targetPos, level, 1.5 + new Random().nextDouble() * 0.5);
            }
        }

    }

    private void spawnNaginata(Player player, Vec3 spawnPos, Vec3 targetPos, Level level, double speed) {
        HolinessNaginataProjectile naginata = CompanionsEntities.HOLINESS_NAGINATA.get().create(level);
        if (naginata != null) {
            naginata.setOwner(player);
            naginata.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            naginata.setDeltaMovement(targetPos.subtract(spawnPos).normalize().scale(speed));
            naginata.refreshOrientation();
            naginata.setInvisible(true);
            level.addFreshEntity(naginata);
        }
    }

}