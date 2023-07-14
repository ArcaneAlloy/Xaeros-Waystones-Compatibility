package fr.shoqapik.w2w2;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.common.settings.ModSettings;
import xaero.minimap.XaeroMinimap;

import java.io.IOException;
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

        IXaeroMinimapClientPlayNetHandler clientLevel = (IXaeroMinimapClientPlayNetHandler) (Minecraft.getInstance().player.connection);
        XaeroMinimapSession session = clientLevel.getXaero_minimapSession();
        WaypointsManager waypointsManager = session.getWaypointsManager();
        Waypoint instant = new Waypoint(pos.getX(), pos.getY() + 2, pos.getZ(), name, name.substring(0, 1), (int)(Math.random() * ModSettings.ENCHANT_COLORS.length), 0, false);
        waypointsManager.getWaypoints().getList().add(instant);

        try {
            XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
        } catch (IOException error) {
            error.printStackTrace();
        }
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
