package fr.shoqapik.w2w2;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.minimap.XaeroMinimap;

import java.io.IOException;
import java.util.List;

public class XaeronCompatibility {

    public static void addWaypoint(BlockPos pos, String name) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;

        IXaeroMinimapClientPlayNetHandler clientLevel =
                (IXaeroMinimapClientPlayNetHandler) mc.player.connection;
        XaeroMinimapSession session = clientLevel.getXaero_minimapSession();
        WaypointsManager waypointsManager = session.getWaypointsManager();

        List<Waypoint> waypoints = waypointsManager.getWaypoints().getList();

        Waypoint existing = getWaypointAtPos(waypoints, pos);
        if (existing != null) {
            existing.setName(name);
            existing.setSymbol(name.substring(0, 1));
            existing.setX(pos.getX());
            existing.setY(pos.getY() + 2);
            existing.setZ(pos.getZ());
        } else {
            Waypoint waypoint = new Waypoint(
                    pos.getX(), pos.getY() + 2, pos.getZ(),
                    name, name.substring(0, 1),
                    (int) (Math.random() * 16), 0, false);
            waypoints.add(waypoint);
        }

        try {
            XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Waypoint getWaypointAtPos(List<Waypoint> waypoints, BlockPos pos) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.getX() == pos.getX()
                    && waypoint.getY() == pos.getY() + 2
                    && waypoint.getZ() == pos.getZ()) {
                return waypoint;
            }
        }
        return null;
    }
}
