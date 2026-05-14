package dev.xylonity.companions.client.shader;

import dev.xylonity.knightlib.client.shader.post.interop.PostShaderSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public record ShadeMawLandingPostShaderSettings(
        Vec3 origin,
        int durationTicks,
        float waveSpeed,
        float ringWidth,
        float glowStrength,
        float chromaStrength,
        float intensity,
        float startRadius,
        float endRadius,
        Vec3 colorA,
        Vec3 colorB,
        float verticalColumnHeight,
        float flashStrength
) implements PostShaderSettings {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Vec3 origin = Vec3.ZERO;
        private int durationTicks = 60;
        private float waveSpeed = 1.0f;
        private float ringWidth = 0.72f;
        private float glow = 0.95f;
        private float chroma = 1.35f;
        private float intensity = 1.0f;

        private float startRadius = 0.5f;
        private float endRadius = 8.0f;

        private Vec3 colorA = new Vec3(0.68, 0.06, 0.10);
        private Vec3 colorB = new Vec3(0.06, 0.01, 0.025);

        private float verticalColumnHeight = 0.62f;
        private float flashStrength = 0.85f;

        public Builder origin(Vec3 origin) {
            this.origin = origin;
            return this;
        }

        public Builder durationTicks(int durationTicks) {
            this.durationTicks = durationTicks;
            return this;
        }

        public Builder speed(float speed) {
            this.waveSpeed = speed;
            return this;
        }

        public Builder width(float ringWidth) {
            this.ringWidth = ringWidth;
            return this;
        }

        public Builder glow(float glow) {
            this.glow = glow;
            return this;
        }

        public Builder chroma(float chroma) {
            this.chroma = chroma;
            return this;
        }

        public Builder intensity(float intensity) {
            this.intensity = intensity;
            return this;
        }

        public Builder radii(float start, float end) {
            this.startRadius = start;
            this.endRadius = end;
            return this;
        }

        public Builder colors(Vec3 a, Vec3 b) {
            this.colorA = a;
            this.colorB = b;
            return this;
        }

        public Builder column(float height) {
            this.verticalColumnHeight = height;
            return this;
        }

        public Builder flash(float flash) {
            this.flashStrength = flash;
            return this;
        }

        public ShadeMawLandingPostShaderSettings build() {
            return new ShadeMawLandingPostShaderSettings(
                    origin,
                    durationTicks,
                    waveSpeed,
                    ringWidth,
                    glow,
                    chroma,
                    intensity,
                    startRadius,
                    endRadius,
                    colorA,
                    colorB,
                    verticalColumnHeight,
                    flashStrength
            );

        }

    }

    public static void encode(ShadeMawLandingPostShaderSettings settings, FriendlyByteBuf buf) {
        buf.writeDouble(settings.origin.x);
        buf.writeDouble(settings.origin.y);
        buf.writeDouble(settings.origin.z);

        buf.writeVarInt(settings.durationTicks);
        buf.writeFloat(settings.waveSpeed);

        buf.writeFloat(settings.ringWidth);
        buf.writeFloat(settings.glowStrength);
        buf.writeFloat(settings.chromaStrength);
        buf.writeFloat(settings.intensity);

        buf.writeFloat(settings.startRadius);
        buf.writeFloat(settings.endRadius);

        buf.writeFloat((float) settings.colorA.x);
        buf.writeFloat((float) settings.colorA.y);
        buf.writeFloat((float) settings.colorA.z);

        buf.writeFloat((float) settings.colorB.x);
        buf.writeFloat((float) settings.colorB.y);
        buf.writeFloat((float) settings.colorB.z);

        buf.writeFloat(settings.verticalColumnHeight);
        buf.writeFloat(settings.flashStrength);
    }

    public static ShadeMawLandingPostShaderSettings decode(FriendlyByteBuf buf) {
        final Vec3 origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

        final int duration = buf.readVarInt();
        final float speed = buf.readFloat();

        final float width = buf.readFloat();
        final float glow = buf.readFloat();
        final float chroma = buf.readFloat();
        final float intensity = buf.readFloat();

        final float startRadius = buf.readFloat();
        float endRadius = buf.readFloat();

        final Vec3 colorA = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
        final Vec3 colorB = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());

        final float height = buf.readFloat();
        final float flash = buf.readFloat();

        return new ShadeMawLandingPostShaderSettings(
                origin,
                duration,
                speed,
                width,
                glow,
                chroma,
                intensity,
                startRadius,
                endRadius,
                colorA,
                colorB,
                height,
                flash
        );

    }

}