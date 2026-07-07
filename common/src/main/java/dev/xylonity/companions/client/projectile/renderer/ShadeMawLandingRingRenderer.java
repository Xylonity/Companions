package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.CompanionsRenderTypes;
import dev.xylonity.companions.common.entity.projectile.ShadeMawLandingRingProjectile;
import dev.xylonity.knightlib.api.util.KnightLibEasings;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Arrays;

public class ShadeMawLandingRingRenderer extends EntityRenderer<ShadeMawLandingRingProjectile> {

    private static final float TWO_PI = (float) (Math.PI * 2.0);
    private static final float FRAGMENT_STEP = (float) (Math.PI / 4.0);

    private static final float SPEED = 2.85f;
    private static final float INTENSITY = 1.0f;
    private static final float START_RADIUS = 0.5f;
    private static final float FLASH_STRENGTH = 0.1f;

    private static final float COLOR_A_R = 200 / 255f;
    private static final float COLOR_A_G = 73 / 255f;
    private static final float COLOR_A_B = 39 / 255f;

    private static final float COLOR_B_R = 255 / 255f;
    private static final float COLOR_B_G = 73 / 255f;
    private static final float COLOR_B_B = 39 / 255f;

    private static final int SEGMENTS = 64;
    private static final int RING_RADIAL_SAMPLES = 12;

    public ShadeMawLandingRingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShadeMawLandingRingProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        final float strength = entity.getStrength();
        if (strength > 0f) {
            renderRing(entity, strength, partialTick, poseStack, bufferSource);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderRing(ShadeMawLandingRingProjectile entity, float strength, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        final int durationTicks = 28 + (int) (10 * strength);
        final float durationSec = Math.max(0.05f, durationTicks / 20f);
        final float ringWidth = 3f + 0.5f * strength;
        final float endRadius = 10f * strength;

        final float ageSec = (entity.tickCount + partialTick) / 20f;
        final float progress = saturate(ageSec * SPEED / durationSec);

        final float life = smoothstep(0f, 0.05f, progress) * (1f - smoothstep(0.50f, 0.95f, progress));
        if (life * INTENSITY <= 0.002f) {
            return;
        }

        final float radius = Mth.lerp(easeOut(progress), START_RADIUS, endRadius);
        final float width = Math.max(0.08f, ringWidth * Mth.lerp(smoothstep(0f, 1f, progress), 0.95f, 0.08f));

        final float timeSec = ((entity.level().getGameTime() % 24000L) + partialTick) / 20f;
        final float pulse = 1f + Mth.sin(timeSec * 20f + radius * 1.7f) * 0.035f * FLASH_STRENGTH;

        poseStack.pushPose();
        poseStack.translate(0d, 0.05d, 0d);
        final Matrix4f matrix = poseStack.last().pose();
        final VertexConsumer buffer = bufferSource.getBuffer(CompanionsRenderTypes.shadeMawLandingRing());

        ringBand(buffer, matrix, radius, width, life * pulse);
        halo(buffer, matrix, radius, width, life);
        fragments(buffer, matrix, radius, life);

        poseStack.popPose();
    }

    private static void ringBand(VertexConsumer buffer, Matrix4f matrix, float radius, float width, float alphaScale) {
        final float outerFeather = Math.max(0.08f, Math.min(0.35f, width * 0.18f));
        final float innerFeather = Math.max(0.15f, width * 0.55f);
        final float innerRadius = Math.max(radius - width, 0f);

        final float[] radii = radialSamples(innerRadius, radius + outerFeather, new float[] {
                innerRadius + innerFeather * 0.33f,
                innerRadius + innerFeather * 0.66f,
                innerRadius + innerFeather,
                radius,
                radius + outerFeather * 0.5f
        });

        final float[] alphas = new float[radii.length];
        for (int i = 0; i < radii.length; i++) {
            alphas[i] = saturate(ringBand(radii[i], radius, innerRadius, innerFeather, outerFeather) * alphaScale * INTENSITY);
        }

        annulus(buffer, matrix, radii, alphas, COLOR_A_R, COLOR_A_G, COLOR_A_B);
    }

    private static float ringBand(float dist, float radius, float innerRadius, float innerFeather, float outerFeather) {
        final float outer = 1f - smoothstep(radius, radius + outerFeather, dist);
        final float inner = innerRadius <= 0f ? 1f : smoothstep(innerRadius, innerRadius + innerFeather, dist);
        return outer * inner;
    }

    private static void halo(VertexConsumer buffer, Matrix4f matrix, float radius, float width, float life) {
        final float haloGap = width * 0.05f;
        final float haloThickness = width * 0.15f;
        final float haloInner = radius + haloGap;
        final float haloOuter = haloInner + haloThickness;
        final float haloFeather = haloThickness * 0.45f;

        final float rStart = Math.max(0f, haloInner - haloFeather);
        final float rEnd = haloOuter + haloFeather;
        final float[] radii = { rStart, (rStart + haloInner) * 0.5f, haloInner, haloOuter, (haloOuter + rEnd) * 0.5f, rEnd };

        final float[] alphas = new float[radii.length];
        for (int i = 0; i < radii.length; i++) {
            final float dist = radii[i];
            final float halo = (1f - smoothstep(haloOuter, haloOuter + haloFeather, dist)) * smoothstep(haloInner - haloFeather, haloInner, dist);
            alphas[i] = saturate(halo * life * INTENSITY);
        }

        annulus(buffer, matrix, radii, alphas, COLOR_B_R, COLOR_B_G, COLOR_B_B);
    }

    private static void fragments(VertexConsumer buffer, Matrix4f matrix, float radius, float life) {
        for (int sector = 0; sector < 8; sector++) {
            final boolean cardinal = sector % 2 == 0;
            final float center = sector * FRAGMENT_STEP;
            final float radialSize = cardinal ? 1.42f : 1f;
            final float angularSize = cardinal ? 1.16f : 1f;
            final float boost = cardinal ? 1.42f : 1.12f;

            final float fragmentLength = Math.max(0.55f, radius * 0.18f) * radialSize;
            final float outerReach = fragmentLength * 0.82f;
            final float innerReach = fragmentLength * 1.12f;

            final int rows = 12;
            final int cols = 8;
            final float[][] xs = new float[rows + 1][cols + 1];
            final float[][] zs = new float[rows + 1][cols + 1];
            final float[][] alphas = new float[rows + 1][cols + 1];

            for (int i = 0; i <= rows; i++) {
                final float signedDistance = Mth.lerp(i / (float) rows, -innerReach, outerReach);

                final float tipDistance = signedDistance >= 0f ? signedDistance / Math.max(outerReach, 0.001f) : -signedDistance / Math.max(innerReach, 0.001f);
                final float taper = saturate(tipDistance);
                final float angularLimit = Mth.lerp(taper, 0.045f, 0.014f) * angularSize;

                final float radial = signedDistance >= 0f ? 1f - smoothstep(0f, outerReach, signedDistance) : 1f - smoothstep(0f, innerReach, -signedDistance);
                final float radialFactor = (float) Math.pow(saturate(radial), 0.78);

                final float distance = Math.max(0f, radius + signedDistance);
                for (int j = 0; j <= cols; j++) {
                    final float offset = Mth.lerp(j / (float) cols, -angularLimit, angularLimit);
                    final float angular = 1f - smoothstep(angularLimit * 0.35f, angularLimit, Math.abs(offset));
                    final float value = (float) Math.pow(saturate(angular), 1.45) * radialFactor * boost;

                    final float angle = center + offset;
                    xs[i][j] = Mth.cos(angle) * distance;
                    zs[i][j] = Mth.sin(angle) * distance;
                    alphas[i][j] = saturate(value * life * INTENSITY);
                }

            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (alphas[i][j] <= 0.002f && alphas[i][j + 1] <= 0.002f && alphas[i + 1][j + 1] <= 0.002f && alphas[i + 1][j] <= 0.002f) {
                        continue;
                    }

                    buffer.vertex(matrix, xs[i][j], 0f, zs[i][j]).color(COLOR_B_R, COLOR_B_G, COLOR_B_B, alphas[i][j]).endVertex();
                    buffer.vertex(matrix, xs[i][j + 1], 0f, zs[i][j + 1]).color(COLOR_B_R, COLOR_B_G, COLOR_B_B, alphas[i][j + 1]).endVertex();
                    buffer.vertex(matrix, xs[i + 1][j + 1], 0f, zs[i + 1][j + 1]).color(COLOR_B_R, COLOR_B_G, COLOR_B_B, alphas[i + 1][j + 1]).endVertex();
                    buffer.vertex(matrix, xs[i + 1][j], 0f, zs[i + 1][j]).color(COLOR_B_R, COLOR_B_G, COLOR_B_B, alphas[i + 1][j]).endVertex();
                }

            }

        }

    }

    private static void annulus(VertexConsumer buffer, Matrix4f matrix, float[] radii, float[] alphas, float red, float green, float blue) {
        for (int i = 0; i < radii.length - 1; i++) {
            final float r0 = radii[i];
            final float r1 = radii[i + 1];
            final float a0 = alphas[i];
            final float a1 = alphas[i + 1];
            if (r1 - r0 <= 1.0e-5f || (a0 <= 0.002f && a1 <= 0.002f)) {
                continue;
            }

            for (int j = 0; j < SEGMENTS; j++) {
                final float angle0 = j * TWO_PI / SEGMENTS;
                final float angle1 = (j + 1) * TWO_PI / SEGMENTS;
                final float cos0 = Mth.cos(angle0);
                final float sin0 = Mth.sin(angle0);
                final float cos1 = Mth.cos(angle1);
                final float sin1 = Mth.sin(angle1);

                buffer.vertex(matrix, cos0 * r0, 0f, sin0 * r0).color(red, green, blue, a0).endVertex();
                buffer.vertex(matrix, cos1 * r0, 0f, sin1 * r0).color(red, green, blue, a0).endVertex();
                buffer.vertex(matrix, cos1 * r1, 0f, sin1 * r1).color(red, green, blue, a1).endVertex();
                buffer.vertex(matrix, cos0 * r1, 0f, sin0 * r1).color(red, green, blue, a1).endVertex();
            }

        }

    }

    private static float[] radialSamples(float start, float end, float[] knots) {
        final float[] samples = new float[RING_RADIAL_SAMPLES + knots.length];
        for (int i = 0; i < RING_RADIAL_SAMPLES; i++) {
            samples[i] = Mth.lerp(i / (RING_RADIAL_SAMPLES - 1f), start, end);
        }

        for (int i = 0; i < knots.length; i++) {
            samples[RING_RADIAL_SAMPLES + i] = Mth.clamp(knots[i], start, end);
        }

        Arrays.sort(samples);
        return samples;
    }

    private static float saturate(float x) {
        return Mth.clamp(x, 0f, 1f);
    }

    private static float smoothstep(float edge0, float edge1, float x) {
        if (edge1 - edge0 <= 1.0e-6f) {
            return x < edge0 ? 0f : 1f;
        }

        return KnightLibEasings.CLAMPED_SMOOTHSTEP.apply((x - edge0) / (edge1 - edge0));
    }

    private static float easeOut(float x) {
        return 1f - (float) Math.pow(1f - saturate(x), 2.45);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ShadeMawLandingRingProjectile entity) {
        return Companions.of("textures/entity/fire_mark_ring.png");
    }

}
