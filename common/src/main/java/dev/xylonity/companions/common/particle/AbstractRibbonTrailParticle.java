package dev.xylonity.companions.common.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Arrays;

public abstract class AbstractRibbonTrailParticle extends Particle {
    private static final int MAX_TRAIL_POINTS = 64;
    private final Vec3[] positionBuffer = new Vec3[MAX_TRAIL_POINTS];
    private int bufferIdx = -1;
    public float r;
    public float g;
    public float b;
    protected float ribbonAlpha = 1.0f;

    public AbstractRibbonTrailParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float r, float g, float b) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.r = r;
        this.g = g;
        this.b = b;
        Arrays.fill(positionBuffer, new Vec3(x, y, z));
    }

    @Override
    public void tick() {

        savePosInBuffer();

        xo = x;
        yo = y;
        zo = z;
        move(xd, yd, zd);
        xd *= 0.99;
        yd *= 0.99;
        zd *= 0.99;

        yd -= gravity;

        if (++age >= lifetime) remove();
    }

    protected void savePosInBuffer() {
        Vec3 current = new Vec3(x, y, z);
        if (bufferIdx == -1) {
            Arrays.fill(positionBuffer, current);
        }

        if (++bufferIdx >= MAX_TRAIL_POINTS) {
            bufferIdx = 0;
        }

        positionBuffer[bufferIdx] = current;
    }

    @Override
    public void render(@NotNull VertexConsumer ignored, @NotNull Camera camera, float partialTicks) {

        if (bufferIdx < 0) return; // not init

        BufferSource buff = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buff.getBuffer(RenderType.entityTranslucent(getRibbonSprite()));

        PoseStack mPose = new PoseStack();

        mPose.pushPose();

        mPose.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        Vec3 from = new Vec3(Mth.lerp(partialTicks, xo, x), Mth.lerp(partialTicks, yo, y), Mth.lerp(partialTicks, zo, z));

        float yaw = -Mth.DEG_TO_RAD * camera.getYRot();
        Vec3 topOffset = new Vec3(0, getRibbonHeight() / 2.0F, 0).yRot(yaw);
        Vec3 botOffset = new Vec3(0, -getRibbonHeight() / 2.0F, 0).yRot(yaw);

        // Building ribbon quads here
        int segments = totalSegments();
        for (int i = 0; i < segments; i++) {
            Vec3 samplex = sampleTrailPoint(i * segmentInterval(), partialTicks);
            float u1 = (float) i / segments;
            float u2 = (float) (i + 1) / segments;

            Pose pose = mPose.last();
            Matrix4f mat = pose.pose();
            Matrix3f norm = pose.normal();

            // bott left
            vertexConsumer.vertex(mat, (float) from.x + (float) botOffset.x, (float) from.y + (float) botOffset.y, (float) from.z + (float) botOffset.z)
                    .color(r, g, b, ribbonAlpha)
                    .uv(u1, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(getLightColor(partialTicks))
                    .normal(norm, 0, 1, 0)
                    .endVertex();

            // bott right
            vertexConsumer.vertex(mat, (float) samplex.x + (float) botOffset.x, (float) samplex.y + (float) botOffset.y, (float) samplex.z + (float) botOffset.z)
                    .color(r, g, b, ribbonAlpha)
                    .uv(u2, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(getLightColor(partialTicks))
                    .normal(norm, 0, 1, 0)
                    .endVertex();

            // top right
            vertexConsumer.vertex(mat, (float) samplex.x + (float) topOffset.x, (float) samplex.y + (float) topOffset.y, (float) samplex.z + (float) topOffset.z)
                    .color(r, g, b, ribbonAlpha)
                    .uv(u2, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(getLightColor(partialTicks))
                    .normal(norm, 0, 1, 0)
                    .endVertex();

            // top left
            vertexConsumer.vertex(mat, (float) from.x + (float) topOffset.x, (float) from.y + (float) topOffset.y, (float) from.z + (float) topOffset.z)
                    .color(r, g, b, ribbonAlpha)
                    .uv(u1, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(getLightColor(partialTicks))
                    .normal(norm, 0, 1, 0)
                    .endVertex();

            from = samplex;
        }

        mPose.popPose();
        buff.endBatch(); // flush to prevent acid
    }

    protected Vec3 sampleTrailPoint(int idx, float tick) {
        Vec3 position = positionBuffer[(bufferIdx - idx - 1) & (MAX_TRAIL_POINTS - 1)];
        Vec3 subtract = positionBuffer[(bufferIdx - idx) & (MAX_TRAIL_POINTS - 1)].subtract(position);
        return position.add(subtract.scale(tick));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    protected int totalSegments() {
        return 20;
    }

    protected int segmentInterval() {
        return 1;
    }

    protected abstract float getRibbonHeight();
    protected abstract ResourceLocation getRibbonSprite();

}
