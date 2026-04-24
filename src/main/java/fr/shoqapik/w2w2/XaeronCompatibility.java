package fr.shoqapik.w2w2;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorldManager;

import java.io.IOException;

public class XaeronCompatibility {

    public static void addWaypoint(BlockPos pos, String name) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;

        // Obtener la sesión del minimapa
        IXaeroMinimapClientPlayNetHandler netHandler =
                (IXaeroMinimapClientPlayNetHandler) mc.player.connection;
        XaeroMinimapSession xaeroSession = netHandler.getXaero_minimapSession();
        if (xaeroSession == null) return;

        // En 26.1.2: MinimapSession via BuiltInHudModules
        MinimapSession minimapSession = (MinimapSession)
                xaero.hud.minimap.BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) return;

        // Obtener el WaypointSet actual via WorldManager
        MinimapWorldManager worldManager = minimapSession.getWorldManager();
        if (worldManager == null || worldManager.getCurrentWorld() == null) return;

        WaypointSet currentSet = worldManager.getCurrentWorld().getCurrentWaypointSet();
        if (currentSet == null) return;

        // Buscar si ya existe un waypoint en esa posición
        Waypoint existing = getWaypointAtPos(currentSet, pos);
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
            currentSet.add(waypoint, false);
        }

        // Guardar via WorldManagerIO
        try {
            minimapSession.getWorldManagerIO().saveAllWorlds(minimapSession);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Waypoint getWaypointAtPos(WaypointSet set, BlockPos pos) {
        for (Object obj : set.getWaypoints()) {
            if (obj instanceof Waypoint waypoint) {
                if (waypoint.getX() == pos.getX()
                        && waypoint.getY() == pos.getY() + 2
                        && waypoint.getZ() == pos.getZ()) {
                    return waypoint;
                }
            }
        }
        return null;
    }
}
