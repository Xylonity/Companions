package dev.xylonity.companions.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.xylonity.companions.Companions;
import dev.xylonity.knightlib.client.shader.post.AbstractPostShader;
import dev.xylonity.knightlib.client.shader.post.interop.PostShaderRenderContext;
import dev.xylonity.knightlib.client.shader.post.interop.PostShaderRenderStage;
import dev.xylonity.knightlib.mixin.PostChainAccessor;
import dev.xylonity.knightlib.mixin.PostPassAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;

public class ShadeMawLandingPostShader extends AbstractPostShader<ShadeMawLandingPostShaderSettings> {

    public static final ResourceLocation ID = Companions.of("shade_maw_landing");
    private static final ResourceLocation POST_JSON = Companions.of("shaders/post/shade_maw_landing/shade_maw_landing.json");

    private static final int MAX_RINGS = 6;
    private static final int MAX_RINGS_KEEP = 16;

    public static final ShadeMawLandingPostShader INSTANCE = new ShadeMawLandingPostShader();

    private final Deque<Ring> rings = new ArrayDeque<>();
    private PostChain shader;
    private int clientTicks = 0;

    private ShadeMawLandingPostShader() {
        ;;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public EnumSet<PostShaderRenderStage> stages() {
        return EnumSet.of(PostShaderRenderStage.DEPTH_READY);
    }

    @Override
    public void start(ShadeMawLandingPostShaderSettings settings) {
        if (settings == null) {
            return;
        }

        rings.addLast(new Ring(clientTicks, settings));

        while (rings.size() > MAX_RINGS_KEEP) {
            rings.removeFirst();
        }

    }

    @Override
    public void clear() {
        rings.clear();
    }

    @Override
    public void clientTick() {
        if (shader == null) {
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            clear();
            return;
        }
        if (minecraft.isPaused()) {
            return;
        }

        clientTicks++;

        rings.removeIf(ring -> ring.ended(clientTicks));
    }

    @Override
    public void initOrReload(TextureManager textures, ResourceManager resources, RenderTarget target) {
        super.initOrReload(textures, resources, target);
        closeChain(shader);
        try {
            this.shader = new PostChain(textures, resources, target, POST_JSON);
        }
        catch (Exception e) {
            this.shader = null;
            Companions.LOGGER.error("Failed to load Shade Maw landing post shader: {}", POST_JSON, e);
        }

    }

    @Override
    public void dispose() {
        closeChain(shader);
        shader = null;
        super.dispose();
    }

    @Override
    public void onLogout() {
        clear();
    }

    @Override
    public void renderStage(PostShaderRenderContext context) {
        if (shader == null) {
            return;
        }
        if (context.stage != PostShaderRenderStage.DEPTH_READY) {
            return;
        }
        if (rings.isEmpty()) {
            return;
        }
        if (context.inverseProjection == null || context.modelView == null) {
            return;
        }

        ensureSized(shader);

        final Ring[] batch = new Ring[MAX_RINGS];
        int count = 0;
        for (final Iterator<Ring> iterator = rings.descendingIterator(); iterator.hasNext() && count < MAX_RINGS; ) {
            batch[count++] = iterator.next();
        }

        if (count == 0) {
            return;
        }

        float timeSec = (clientTicks + context.partialTicks) / 20f;

        final Matrix4f inverseModelView  = new Matrix4f(context.modelView).invert(new Matrix4f());
        for (final PostPass pass : ((PostChainAccessor) shader).knightlib$getPasses()) {
            final EffectInstance effectInstance = ((PostPassAccessor) pass).knightlib$getEffect();
            if (effectInstance == null) {
                continue;
            }

            setUniformMat4(effectInstance, "InverseTransformMatrix", context.inverseProjection);
            setUniformMat4(effectInstance, "InverseModelViewMat", inverseModelView);
            setUniformVec3(effectInstance, "CameraPosition", context.cameraPosition);

            setUniformFloat(effectInstance, "time", timeSec);

            final Ring newestRing = batch[0];
            final ShadeMawLandingPostShaderSettings settings = newestRing.settings;

            setUniformFloat(effectInstance, "intensity", settings.intensity());
            setUniformFloat(effectInstance, "waveDurationSec", Math.max(0.05f, settings.durationTicks() / 20.0f));
            setUniformFloat(effectInstance, "waveSpeed", settings.waveSpeed());

            setUniformFloat(effectInstance, "ringWidth", settings.ringWidth());
            setUniformFloat(effectInstance, "glowStrength", settings.glowStrength());
            setUniformFloat(effectInstance, "chromaStrength", settings.chromaStrength());

            setUniformFloat(effectInstance, "waveStartRadius", settings.startRadius());
            setUniformFloat(effectInstance, "waveEndRadius", settings.endRadius());

            setUniformVec3(effectInstance, "ringColorA", settings.colorA());
            setUniformVec3(effectInstance, "ringColorB", settings.colorB());

            setUniformFloat(effectInstance, "verticalColumnHeight", settings.verticalColumnHeight());
            setUniformFloat(effectInstance, "flashStrength", settings.flashStrength());

            for (int i = 0; i < MAX_RINGS; i++) {
                final Ring ring = batch[i];
                if (ring == null) {
                    setUniformVec3(effectInstance, "waveOrigin" + i, Vec3.ZERO);
                    setUniformFloat(effectInstance, "waveAge" + i, -1f);
                }
                else {
                    setUniformVec3(effectInstance, "waveOrigin" + i, ring.settings.origin());
                    setUniformFloat(effectInstance, "waveAge" + i, ring.ageSeconds(clientTicks, context.partialTicks));
                }

            }

        }

        process(shader, context.partialTicks, () -> {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.resetTextureMatrix();
        });
    }

    private record Ring(
            int startTick,
            ShadeMawLandingPostShaderSettings settings
    ) {

        float ageSeconds(int currentTick, float partialTicks) {
            final float delta = (currentTick - startTick) + partialTicks;
            return delta / 20.0f;
        }

        boolean ended(int currentTick) {
            final int age = currentTick - startTick;
            return age >= settings.durationTicks();
        }

    }

}