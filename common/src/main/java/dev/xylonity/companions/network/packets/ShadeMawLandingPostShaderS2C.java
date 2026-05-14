package dev.xylonity.companions.network.packets;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.shader.ShadeMawLandingPostShader;
import dev.xylonity.companions.client.shader.ShadeMawLandingPostShaderSettings;
import dev.xylonity.knightlib.network.ClientboundPacketType;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ShadeMawLandingPostShaderS2C(
        ShadeMawLandingPostShaderSettings settings
) {

    public static final ResourceLocation ID = Companions.of("shade_maw_landing");

    public static final ClientboundPacketType<ShadeMawLandingPostShaderS2C> TYPE =
            PacketType.clientbound(
                    ID,
                    ShadeMawLandingPostShaderS2C.class,
                    PacketCodec.of(ShadeMawLandingPostShaderS2C::encode, ShadeMawLandingPostShaderS2C::decode),
                    message -> ShadeMawLandingPostShader.INSTANCE.start(message.settings())
            );

    public static void encode(ShadeMawLandingPostShaderS2C packet, FriendlyByteBuf buf) {
        ShadeMawLandingPostShaderSettings.encode(packet.settings, buf);
    }

    public static ShadeMawLandingPostShaderS2C decode(FriendlyByteBuf buf) {
        return new ShadeMawLandingPostShaderS2C(ShadeMawLandingPostShaderSettings.decode(buf));
    }

}