package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.item.blockitem.GenericBlockItem;
import dev.xylonity.companions.common.item.gecko.GeckoBlockItem;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class HolyPorcelainPottery extends GenericBlockItem {

    private static final String STORED_ENTITY = "stored_entity";
    private static final String STORED_NAME = "stored_entity_name";

    public HolyPorcelainPottery(Block pBlock, Properties pProperties, String resourceKey) {
        super(pBlock, pProperties, resourceKey);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (hasStoredEntity(stack)) {
            final String name = stack.getOrCreateTag().getString(STORED_NAME);
            tooltip.add(Component.translatable("tooltip.item.companions.holy_porcelain_pottery.stored", name).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        final Level level = player.level();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (hasStoredEntity(stack)) {
            return InteractionResult.PASS;
        }
        if (target instanceof Player) {
            return InteractionResult.PASS;
        }

        final ResourceLocation id = EntityType.getKey(target.getType());
        if (!isAllowed(id)) {
            player.displayClientMessage(Component.translatable("holy_porcelain_pottery.companions.client_message.blacklisted"), true);
            return InteractionResult.FAIL;
        }

        final double maxBBox = CompanionsConfig.HOLY_PORCELAIN_POTTERY_MAX_BBOX;
        if (target.getBbWidth() > maxBBox || target.getBbHeight() > maxBBox) {
            player.displayClientMessage(Component.translatable("holy_porcelain_pottery.companions.client_message.too_big"), true);
            return InteractionResult.FAIL;
        }

        final CompoundTag entityTag = new CompoundTag();
        if (!target.save(entityTag)) {
            return InteractionResult.PASS;
        }

        final ItemStack storedStack = stack.copy();
        storedStack.setCount(1);

        final CompoundTag tag = storedStack.getOrCreateTag();
        tag.put(STORED_ENTITY, entityTag);
        tag.putString(STORED_NAME, target.getDisplayName().getString());
        storedStack.setTag(tag);

        target.discard();
        player.setItemInHand(hand, storedStack);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        final Level level = context.getLevel();
        final ItemStack stack = context.getItemInHand();
        final Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!hasStoredEntity(stack)) {
            return super.useOn(context);
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        final CompoundTag entityTag = tag.getCompound(STORED_ENTITY).copy();

        final BlockPos position = context.getClickedPos().relative(context.getClickedFace());
        final Direction face = context.getClickedFace();
        final double x = position.getX() + 0.5;
        final double y = face == Direction.DOWN ? context.getClickedPos().getY() - 0.01 : position.getY();
        final double z = position.getZ() + 0.5;

        final Entity spawnedEntity = EntityType.loadEntityRecursive(entityTag, serverLevel, entity -> {
            entity.moveTo(x, y, z, entity.getYRot(), entity.getXRot());
            return entity;
        });

        if (spawnedEntity == null) {
            return InteractionResult.FAIL;
        }
        if (!serverLevel.addFreshEntity(spawnedEntity)) {
            return InteractionResult.FAIL;
        }

        tag.remove(STORED_ENTITY);
        tag.remove(STORED_NAME);
        stack.setTag(tag);

        return InteractionResult.SUCCESS;
    }

    private static boolean isAllowed(ResourceLocation id) {
        if (id == null) {
            return true;
        }
        if (listContains(CompanionsConfig.HOLY_PORCELAIN_POTTERY_BLACKLIST, id)) {
            return false;
        }

        final String whitelist = CompanionsConfig.HOLY_PORCELAIN_POTTERY_WHITELIST;
        return whitelist == null || whitelist.isBlank() || listContains(whitelist, id);
    }

    private static boolean listContains(String raw, ResourceLocation id) {
        if (raw == null || raw.isBlank()) {
            return false;
        }

        return Arrays.stream(raw.split("[,;]"))
                .map(String::trim)
                .anyMatch(string -> string.equals(id.toString()));
    }

    public static boolean hasStoredEntity(ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(STORED_ENTITY);
    }

}