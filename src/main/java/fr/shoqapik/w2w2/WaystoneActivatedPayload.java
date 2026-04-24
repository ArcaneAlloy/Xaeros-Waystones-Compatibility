package fr.shoqapik.w2w2;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WaystoneActivatedPayload(BlockPos pos, String name) implements CustomPacketPayload {

    public static final Type<WaystoneActivatedPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(W2w2Mod.MODID, "waystone_activated"));

    public static final StreamCodec<FriendlyByteBuf, WaystoneActivatedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, WaystoneActivatedPayload::pos,
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, WaystoneActivatedPayload::name,
                    WaystoneActivatedPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
