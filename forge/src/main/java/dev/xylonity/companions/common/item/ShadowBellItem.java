package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.AbstractShadeAltarBlockEntity;
import dev.xylonity.companions.common.blockentity.ShadeSwordAltarBlockEntity;
import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShadowBellItem extends Item {

    private static final String ST_DIM = "stored_dim";
    private static final String ST_X = "stored_x";
    private static final String ST_Y = "stored_y";
    private static final String ST_Z = "stored_z";

    public ShadowBellItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        CompoundTag t = stack.getTag();
        return t != null && t.contains("bell_curr") && t.contains("bell_max");
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        CompoundTag t = stack.getTag();
        int curr = t != null ? t.getInt("bell_curr") : 0;
        int max  = t != null ? t.getInt("bell_max")  : 1;
        return Math.round((float) curr * 13f / (float) max);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0xAA0077;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide) return;
        CompoundTag tag = pStack.getTag();
        if (tag == null || !tag.contains(ST_DIM)) return;

        if (!pLevel.dimension().location().toString().equals(tag.getString(ST_DIM)))
            return;

        BlockPos altarPos = new BlockPos(tag.getInt(ST_X), tag.getInt(ST_Y), tag.getInt(ST_Z));
        if (!(pLevel.getBlockEntity(altarPos) instanceof AbstractShadeAltarBlockEntity altar)) {
            clearLink(pStack);
            return;
        }

        if (altar.getCharges() <= 0) {
            pStack.shrink(1);
            return;
        }

        if (tag.getInt("bell_curr") != altar.getCharges() || tag.getInt("bell_max") != altar.getMaxCharges()) {
            tag.putInt("bell_curr", altar.getCharges());
            tag.putInt("bell_max",  altar.getMaxCharges());
            pStack.setTag(tag);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        CompoundTag t = stack.getTag();
        if (t != null && t.contains("bell_curr")) {
            tooltip.add(Component.literal("Cargas: " + t.getInt("bell_curr") + " / " + t.getInt("bell_max")));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        ItemStack stack = ctx.getItemInHand();

        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof AbstractShadeAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (altar.getCharges() <= 0) {
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().displayClientMessage(Component.translatable("item.companions.shadow_bell.altar_empty"), true);
            }

            return InteractionResult.FAIL;
        }

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(ST_DIM, world.dimension().location().toString());
        tag.putInt(ST_X, pos.getX());
        tag.putInt(ST_Y, pos.getY());
        tag.putInt(ST_Z, pos.getZ());
        tag.putInt("bell_curr", altar.getCharges());
        tag.putInt("bell_max",  altar.getMaxCharges());
        stack.setTag(tag);

        if (ctx.getPlayer() != null) {
            ctx.getPlayer().displayClientMessage(Component.translatable("item.companions.shadow_bell.altar_saved"), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        CompoundTag tag = stack.getTag();
        if (pLevel.isClientSide || tag == null || !tag.contains(ST_DIM) || !tag.contains(ST_X) || !tag.contains(ST_Y) || !tag.contains(ST_Z) || pLevel.getServer() == null) {
            return InteractionResultHolder.pass(stack);
        }

        Level world = pLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString(ST_DIM))));
        if (world == null) {
            pPlayer.sendSystemMessage(Component.translatable("item.companions.shadow_bell.dimension_doesnt_exist"));

            clearLink(stack);
            return InteractionResultHolder.pass(stack);
        }

        if (world.getBlockEntity(new BlockPos(tag.getInt(ST_X), tag.getInt(ST_Y), tag.getInt(ST_Z))) instanceof ShadeSwordAltarBlockEntity altar) {

            if (altar.getCharges() <= 0) {
                pPlayer.sendSystemMessage(Component.translatable("item.companions.shadow_bell.no_charges"));
                clearLink(stack);
                return InteractionResultHolder.pass(stack);
            }

            ShadeEntity entity = CompanionsEntities.SHADE_SWORD.get().create(pLevel);
            if (entity != null) {
                entity.tame(pPlayer);

                double r1 = 2, r2 = 3;
                double u = pLevel.random.nextDouble();
                double r = Math.sqrt(u * (r2 * r2 - r1 * r1) + r1 * r1);
                float randYaw = pPlayer.getYRot() + (pLevel.random.nextFloat() * 2.0F - 1.0F) * 30f;
                float randPitch = pPlayer.getXRot() + (pLevel.random.nextFloat() * 2.0F - 1.0F) * 20f;

                Vec3 target = pPlayer.getEyePosition(1f).add(Vec3.directionFromRotation(randPitch, randYaw).scale(r));
                int blockX = Mth.floor(target.x);
                int blockZ = Mth.floor(target.z);

                int topY = pLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, blockX, blockZ);
                double px = blockX + 0.5;
                double pz = blockZ + 0.5;

                entity.moveTo(px, topY, pz);

                for (int i = 0; i < 20; i++) {
                    double vx = (pLevel.random.nextDouble() - 0.5) * entity.getBbWidth();
                    double vy = (pLevel.random.nextDouble() - 0.5) * entity.getBbHeight();
                    double vz = (pLevel.random.nextDouble() - 0.5) * entity.getBbWidth();
                    if (pLevel instanceof ServerLevel level) {
                        level.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), px, topY, pz, 1, vx, vy, vz, 0.15);
                        if (i % 3 == 0) level.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), px, topY, pz, 1, vx, vy, vz, 0.35);
                    }
                }

                if (altar.isBloodUpgradeActive()) {
                    entity.setIsBlood(true);
                }

                double dx = pPlayer.getX() - px;
                double dy = (pPlayer.getY() + pPlayer.getEyeHeight()) - (topY + entity.getEyeHeight());
                double dz = pPlayer.getZ() - pz;
                float yaw = (float) (Math.atan2(dz, dx) * (180F / Math.PI)) - 90F;
                float pitch = (float) (-(Math.atan2(dy,  Math.sqrt(dx * dx + dz * dz)) * (180F / Math.PI)));
                entity.setYRot(yaw);
                entity.yBodyRot = yaw;
                entity.yBodyRotO = yaw;
                entity.yHeadRot = yaw;
                entity.yHeadRotO = yaw;
                entity.setXRot(pitch);
                entity.xRotO = pitch;

                entity.setInvisible(true);
                pLevel.addFreshEntity(entity);
            }

            if (altar.consumeCharge()) {
                altar.sync();
                tag.putInt("bell_curr", altar.getCharges());
                stack.setTag(tag);
            } else {
                pPlayer.sendSystemMessage(Component.translatable("item.companions.shadow_bell.no_charges"));
                clearLink(stack);
            }

        } else {
            clearLink(stack);
            pPlayer.sendSystemMessage(Component.translatable("item.companions.shadow_bell.couldnt_find"));
        }

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    private void clearLink(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag == null) return;

        tag.remove(ST_DIM);
        tag.remove(ST_X);
        tag.remove(ST_Y);
        tag.remove(ST_Z);
        tag.remove("bell_curr");
        tag.remove("bell_max");
        stack.setTag(tag);
    }

}