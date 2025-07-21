package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.AbstractShadeAltarBlockEntity;
import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ShadowBellItem extends TooltipItem {

    private static final String ST_DIM = "stored_dim";
    private static final String ST_X = "stored_x";
    private static final String ST_Y = "stored_y";
    private static final String ST_Z = "stored_z";
    private static final String BELL_CURR = "bell_curr";
    private static final String BELL_MAX = "bell_max";
    private static final String UUID_SHADE = "cached_shade";
    private static final String ALTAR_NAME = "altar_name";

    public ShadowBellItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag t = customData.copyTag();
        return t != null && t.contains(BELL_CURR) && t.contains(BELL_MAX);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag t = customData.copyTag();
        int curr = t != null ? t.getInt(BELL_CURR) : 0;
        int max = t != null ? t.getInt(BELL_MAX) : 1;
        return Math.round((float) curr * 13f / (float) max);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0xAA0077;    // Somewhat red lol
    }

    // Checks if the linked altar is still active (and updates the charges of this bell)
    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!(pEntity instanceof Player)) return;
        if (pLevel.isClientSide) return;
        CustomData customData = pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (tag == null || !tag.contains(ST_DIM)) return;

        ServerLevel altarLevel = null;
        if (pLevel.getServer() != null) {
            ServerLevel maybe = pLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(ST_DIM))));
            if (maybe instanceof ServerLevel) altarLevel = maybe;
        }

        BlockPos pos = new BlockPos(tag.getInt(ST_X), tag.getInt(ST_Y), tag.getInt(ST_Z));
        if (altarLevel == null || !(altarLevel.getBlockEntity(pos) instanceof AbstractShadeAltarBlockEntity altar)) {
            clearLink(pStack);
            return;
        }

        if (altar.getCharges() <= 0) {
            pStack.shrink(1);
            return;
        }

        // Updates this charge count
        if (tag.getInt(BELL_CURR) != altar.getCharges() || tag.getInt(BELL_MAX) != altar.getMaxCharges()) {
            tag.putInt(BELL_CURR, altar.getCharges());
            tag.putInt(BELL_MAX,  altar.getMaxCharges());
            pStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        String currName = altarLevel.getBlockState(pos).getBlock().getName().getString();
        if (!currName.equals(tag.getString(ALTAR_NAME))) {
            tag.putString(ALTAR_NAME, currName);
            pStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag t = customData.copyTag();
        if (t == null || !t.contains(ST_DIM) || !t.contains(ST_X) ||!t.contains(ST_Y) || !t.contains(ST_Z)) {
            tooltipComponents.add(Component.translatable("tooltip.item.companions.shadow_bell.default").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return;
        }

        if (t.contains(BELL_CURR) && t.contains(ALTAR_NAME)) {
            tooltipComponents.add(Component.translatable("tooltip.item.companions.shadow_bell.linked_to", t.getString(ALTAR_NAME)));
            tooltipComponents.add(Component.translatable("tooltip.item.companions.shadow_bell.charges_remaining", t.getInt(BELL_CURR)));

            if (t.hasUUID(UUID_SHADE)) {
                Entity e = CompanionsEntityTracker.getEntityByUUID(t.getUUID(UUID_SHADE));
                if (e instanceof ShadeEntity shade) {
                    tooltipComponents.add(Component.translatable("tooltip.item.companions.shadow_bell.lifetime", shade.getLifetime() / 20));
                } else {
                    t.remove(UUID_SHADE);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(t));
                }
            }
        }
    }

    @Override
    protected String tooltipName() {
        return "";
    }

    // If the interaction is done on an altar, its relevant data is written in memory
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
                ctx.getPlayer().displayClientMessage(Component.translatable("shadow_bell.companions.client_message.altar_empty"), true);
            }

            return InteractionResult.FAIL;
        }

        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putString(ST_DIM, world.dimension().location().toString());
        tag.putInt(ST_X, pos.getX());
        tag.putInt(ST_Y, pos.getY());
        tag.putInt(ST_Z, pos.getZ());
        tag.putInt(BELL_CURR, altar.getCharges());
        tag.putInt(BELL_MAX, altar.getMaxCharges());
        tag.putString(ALTAR_NAME, world.getBlockState(pos).getBlock().getName().getString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        if (ctx.getPlayer() != null) {
            ctx.getPlayer().displayClientMessage(Component.translatable("shadow_bell.companions.client_message.altar_saved"), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        CustomData customData = pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (tag != null && tag.hasUUID(UUID_SHADE)) {
            if (pInteractionTarget.getUUID().equals(tag.getUUID(UUID_SHADE)) && pInteractionTarget instanceof ShadeEntity) {
                pInteractionTarget.discard();
                tag.remove(UUID_SHADE);
                pStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                return InteractionResult.SUCCESS;
            }
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (pLevel.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (tag == null || !tag.contains(ST_DIM) || !tag.contains(ST_X) || !tag.contains(ST_Y) || !tag.contains(ST_Z) || pLevel.getServer() == null) {
            return InteractionResultHolder.pass(stack);
        }

        ServerLevel world = pLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(ST_DIM))));
        if (world == null) {
            pPlayer.displayClientMessage(Component.translatable("shadow_bell.companions.client_message.couldnt_find"), true);

            clearLink(stack);
            return InteractionResultHolder.pass(stack);
        }

        if (world.getBlockEntity(new BlockPos(tag.getInt(ST_X), tag.getInt(ST_Y), tag.getInt(ST_Z))) instanceof AbstractShadeAltarBlockEntity altar) {

            if (altar.getCharges() <= 0) {
                pPlayer.displayClientMessage(Component.translatable("shadow_bell.companions.client_message.no_charges"), true);
                clearLink(stack);
                return InteractionResultHolder.pass(stack);
            }

            UUID oldShadeUUID = altar.activeShadeUUID;
            if (oldShadeUUID != null) {
                Entity oldShade = world.getEntity(oldShadeUUID);
                if (oldShade instanceof ShadeEntity) {
                    oldShade.discard();
                }

                altar.activeShadeUUID = null;
            }

            ShadeEntity entity = altar.spawnShade(pPlayer.level(), pPlayer, pUsedHand);
            if (entity != null) {
                tag.putUUID(UUID_SHADE, entity.getUUID());
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }

            if (altar.consumeCharge()) {
                altar.sync();
                tag.putInt(BELL_CURR, altar.getCharges());
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            } else {
                pPlayer.displayClientMessage(Component.translatable("shadow_bell.companions.client_message.no_charges"), true);
                clearLink(stack);
            }

            pPlayer.level().playSound(null, pPlayer.blockPosition(), CompanionsSounds.SHADE_BELL_SUMMON.get(), pPlayer.getSoundSource(), 0.3f, 1);
        } else {
            clearLink(stack);
            pPlayer.displayClientMessage(Component.translatable("shadow_bell.companions.client_message.couldnt_find"), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    private void clearLink(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        if (tag == null) return;

        tag.remove(ST_DIM);
        tag.remove(ST_X);
        tag.remove(ST_Y);
        tag.remove(ST_Z);
        tag.remove(BELL_CURR);
        tag.remove(BELL_MAX);
        tag.remove(UUID_SHADE);
        tag.remove(ALTAR_NAME);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

}