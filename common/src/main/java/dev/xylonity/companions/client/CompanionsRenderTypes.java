package dev.xylonity.companions.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.xylonity.companions.mixin.RenderStateShardAccessor;
import dev.xylonity.companions.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderType;

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

    public static RenderType shadeMawLandingRing() {
        return SHADE_MAW_LANDING_RING;
    }

}
