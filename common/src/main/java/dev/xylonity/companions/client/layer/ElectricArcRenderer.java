package dev.xylonity.companions.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;

/**
 * Shared util that renders a single textured electric-arc quad between two points.
 *
 * Code of the 3D plate rendering adapted from mim1q's work
 * https://github.com/mim1q/MineCells/blob/1.20.x/src/main/java/com/github/mim1q/minecells/client/render/ProtectorEntityRenderer.java
 */
public final class ElectricArcRenderer {

    private ElectricArcRenderer() {
        ;;
    }

    public static void renderArc(MultiBufferSource bufferSource, PoseStack poseStack, Vec3 origin, Vec3 direction, int frame, int totalFrames, ResourceLocation texture, boolean fullBright, int packedLight) {

        final VertexConsumer vertexConsumer = bufferSource.getBuffer(AutoGlowingTexture.getRenderType(texture));
        final Matrix4f pose = poseStack.last().pose();
        final Matrix3f normal = poseStack.last().normal();

        final float x0 = (float) origin.x;
        final float y0 = (float) origin.y;
        final float z0 = (float) origin.z;
        final float x1 = (float) direction.x;
        final float y1 = (float) direction.y;
        final float z1 = (float) direction.z;

        float dx = x1 - x0;
        final float dy = y1 - y0;
        final float dz = z1 - z0;
        if (dx == 0.0F) {
            dx = 0.001F;
        }

        final float dHorizontal = Mth.sqrt(dx * dx + dz * dz);
        final float length = Mth.sqrt(dHorizontal * dHorizontal + dy * dy);

        final float halfWidth = 0.5F;
        final float yOff = halfWidth * (dHorizontal / length);
        final float xOff = halfWidth * (dy / length) * (dx / dHorizontal);
        final float zOff = halfWidth * (dy / length) * (dz / dHorizontal);

        final float frameSize = 1F / totalFrames;
        final float v0 = frame * frameSize;
        final float v1 = v0 + frameSize;

        final int light = fullBright ? LightTexture.FULL_BRIGHT : packedLight;

        final float[][] verts = {
                { x0 + xOff, y0 - yOff, z0 + zOff, 0.0F, v1 },
                { x1 + xOff, y1 - yOff, z1 + zOff, 1.0F, v1 },
                { x1 - xOff, y1 + yOff, z1 - zOff, 1.0F, v0 },
                { x0 - xOff, y0 + yOff, z0 - zOff, 0.0F, v0 },
        };

        final int[] indices = { 0, 1, 2, 3, 3, 2, 1, 0 };
        for (final int i : indices) {
            vertexConsumer.addVertex(pose, verts[i][0], verts[i][1], verts[i][2])
                    .setColor(255, 255, 255, 255)
                    .setUv(verts[i][3], verts[i][4])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
        }

    }

}