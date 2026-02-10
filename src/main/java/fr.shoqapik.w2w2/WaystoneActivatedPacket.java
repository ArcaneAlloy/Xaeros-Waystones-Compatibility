package fr.shoqapik.w2w2;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WaystoneActivatedPacket {
    public static String invalid = "invalid";
    private BlockPos pos;
    private String name;

    public WaystoneActivatedPacket(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public static void handle(WaystoneActivatedPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(msg, ctx))
        );
        ctx.get().setPacketHandled(true);
    }

    private static void handlePacket(WaystoneActivatedPacket msg, Supplier<NetworkEvent.Context> ctx) {
        String name = msg.name;
        if(name.isEmpty()){
            name = "Unnamed Waystone";
        }
        BlockPos pos = msg.pos;
        if(name.equals(invalid)) return;

        if (!ModList.get().isLoaded("xaerominimap")){
            return;
        }
        XaeronCompatibility.addWaypoint(pos,name);
    }


    public static WaystoneActivatedPacket decode(FriendlyByteBuf packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        String name = packetBuffer.readUtf();
        return new WaystoneActivatedPacket(pos, name);
    }

    public static void encode(WaystoneActivatedPacket msg, FriendlyByteBuf packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        packetBuffer.writeUtf(msg.name);
    }

}
