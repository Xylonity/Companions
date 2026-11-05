package dev.xylonity.companions.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.CompanionsRenderTypes;
import dev.xylonity.knightlib.client.particle.AbstractRibbonTrailParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/// Based off my own pill particle implementation from Robots
public abstract class BloodRibbonTrailParticle extends AbstractRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = Companions.of("textures/particle/blood_trail.png");
    private static final int FADE_TICKS = 8;

    protected final int targetId;
    private final float width;
    private final int segments;
    private int fade;

    protected BloodRibbonTrailParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, int targetId, float width, int segments) {
        super(level, x, y, z, 0, 0, 0, r, g, b);
        this.targetId = targetId;
        this.width = width;
        this.segments = segments;
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = Integer.MAX_VALUE;
    }

    protected abstract Vec3 anchor(Entity target);

    protected void init() {
        final Entity target = getTarget();
        if (target != null) {
            Vec3 p = anchor(target);
            setPos(p.x, p.y, p.z);
            xo = x;
            yo = y;
            zo = z;
        }

    }

    @Override
    public void tick() {
        super.tick();

        final Entity target = getTarget();
        if (target != null && target.isAlive()) {
            final Vec3 p = anchor(target);
            setPos(p.x, p.y, p.z);
            return;
        }

        ribbonAlpha = 1f - ++fade / (float) FADE_TICKS;
        if (fade >= FADE_TICKS) {
            remove();
        }

    }

    @Override
    public void render(@NotNull VertexConsumer ignored, @NotNull Camera camera, float partialTicks) {
        if (bufferIdx < 0) {
            return;
        }

        final Vec3 cam = camera.getPosition();
        final Vec3[] pts = new Vec3[segments + 1];
        pts[0] = new Vec3(Mth.lerp(partialTicks, xo, x), Mth.lerp(partialTicks, yo, y), Mth.lerp(partialTicks, zo, z));
        for (int i = 1; i <= segments; i++) {
            pts[i] = sampleTrailPoint(i - 1, partialTicks);
        }

        final Vec3[] side = new Vec3[segments + 1];
        for (int i = 0; i <= segments; i++) {
            final float s = i / (float) segments;
            final Vec3 dir = pts[Math.max(0, i - 1)].subtract(pts[Math.min(segments, i + 1)]);
            final Vec3 normal = dir.cross(pts[i].subtract(cam));
            final double len = normal.length();
            final float half = width * 0.5f * (float) Math.pow(1f - s, 1.1f) * Math.min(1f, s * 8f + 0.25f);
            side[i] = len < 1.0E-6 ? Vec3.ZERO : normal.scale(half / len);
        }

        final MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        for (final RenderType type : new RenderType[] { RenderType.entityTranslucentEmissive(TEXTURE), CompanionsRenderTypes.entityTranslucentEmissiveDepth(TEXTURE) }) {
            final VertexConsumer consumer = buffer.getBuffer(type);
            for (int i = 0; i < segments; i++) {
                final float s0 = i / (float) segments;
                final float s1 = (i + 1) / (float) segments;
                vertex(consumer, pts[i].add(side[i]), cam, s0, 0f);
                vertex(consumer, pts[i + 1].add(side[i + 1]), cam, s1, 0f);
                vertex(consumer, pts[i + 1].subtract(side[i + 1]), cam, s1, 1f);
                vertex(consumer, pts[i].subtract(side[i]), cam, s0, 1f);
            }

            buffer.endBatch(type);
        }

    }

    private void vertex(VertexConsumer consumer, Vec3 p, Vec3 cam, float s, float v) {
        final float r;
        final float g;
        final float b;
        if (s < 0.25f) {
            final float t = s / 0.25f;
            r = 1f;
            g = Mth.lerp(t, 0.86f, 0.17f);
            b = Mth.lerp(t, 0.86f, 0.24f);
        }
        else {
            final float t = (s - 0.25f) / 0.75f;
            r = Mth.lerp(t, 1f, this.r);
            g = Mth.lerp(t, 0.17f, this.g);
            b = Mth.lerp(t, 0.24f, this.b);
        }

        final float near = Mth.clamp(((float) p.distanceTo(cam) - 1.2f) / 2.8f, 0f, 1f);
        final float a = ribbonAlpha * (float) Math.pow(1f - s, 1.25f) * near * near * (3f - 2f * near);

        consumer.vertex((float) (p.x - cam.x), (float) (p.y - cam.y), (float) (p.z - cam.z))
                .color(r, g, b, a)
                .uv(s, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(0, 1, 0)
                .endVertex();
    }

    @Nullable
    protected Entity getTarget() {
        return level.getEntity(targetId);
    }

    @Override
    protected float getRibbonHeight() {
        return width;
    }

    @Override
    protected ResourceLocation getRibbonSprite() {
        return TEXTURE;
    }

    @Override
    protected int totalSegments() {
        return segments;
    }

}
