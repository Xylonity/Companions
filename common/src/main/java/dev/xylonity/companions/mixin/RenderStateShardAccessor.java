package dev.xylonity.companions.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {

    @Accessor("POSITION_COLOR_SHADER")
    static RenderStateShard.ShaderStateShard getPositionColorShader() {
        throw new AssertionError();
    }

    @Accessor("TRANSLUCENT_TRANSPARENCY")
    static RenderStateShard.TransparencyStateShard getTranslucentTransparency() {
        throw new AssertionError();
    }

    @Accessor("NO_CULL")
    static RenderStateShard.CullStateShard getNoCull() {
        throw new AssertionError();
    }

    @Accessor("LEQUAL_DEPTH_TEST")
    static RenderStateShard.DepthTestStateShard getLequalDepthTest() {
        throw new AssertionError();
    }

    @Accessor("COLOR_WRITE")
    static RenderStateShard.WriteMaskStateShard getColorWrite() {
        throw new AssertionError();
    }

}
