package dev.xylonity.companions.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.xylonity.companions.mixin.RenderStateShardAccessor;
import dev.xylonity.companions.mixin.RenderTypeAccessor;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class CompanionsRenderTypes {

    private CompanionsRenderTypes() {
        ;;
    }

    private static final RenderType SHADE_MAW_LANDING_RING = RenderTypeAccessor.companions$create(
            "companions_shade_maw_landing_ring",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            4096,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShardAccessor.getPositionColorShader())
                    .setTransparencyState(RenderStateShardAccessor.getTranslucentTransparency())
                    .setCullState(RenderStateShardAccessor.getNoCull())
                    .setDepthTestState(RenderStateShardAccessor.getLequalDepthTest())
                    .setWriteMaskState(RenderStateShardAccessor.getColorWrite())
                    .createCompositeState(false)
    );

    private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_DEPTH = Util.memoize(texture -> RenderTypeAccessor.companions$create(
            "companions_entity_translucent_emissive_depth",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShardAccessor.getEntityTranslucentEmissiveShader())
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setCullState(RenderStateShardAccessor.getNoCull())
                    .setWriteMaskState(RenderStateShardAccessor.getDepthWrite())
                    .createCompositeState(false)
    ));

    public static RenderType shadeMawLandingRing() {
        return SHADE_MAW_LANDING_RING;
    }

    public static RenderType entityTranslucentEmissiveDepth(ResourceLocation texture) {
        return ENTITY_TRANSLUCENT_EMISSIVE_DEPTH.apply(texture);
    }

}
