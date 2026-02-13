package fr.shoqapik.w2w2;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.LogManager;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.common.settings.ModSettings;
import xaero.minimap.XaeroMinimap;

import java.io.IOException;
import java.util.ArrayList;

public class XaeronCompatibility {
    public static void addWaypoint(BlockPos pos, String name, IWaystone waystone){
        IXaeroMinimapClientPlayNetHandler clientLevel = (IXaeroMinimapClientPlayNetHandler) (Minecraft.getInstance().player.connection);
        XaeroMinimapSession session = clientLevel.getXaero_minimapSession();
        WaypointsManager waypointsManager = session.getWaypointsManager();
        Waypoint waypoint = getWaypoint(waypointsManager.getWaypoints().getList(), waystone);
        if(waypoint == null){
            Waypoint instant = new Waypoint(pos.getX(), pos.getY() + 2, pos.getZ(), name, name.substring(0, 1), (int)(Math.random() * 16), 0, false);
            waypointsManager.getWaypoints().getList().add(instant);
        }else{
            waypoint.setName(name);
            waypoint.setSymbol(name.substring(0, 1));
            waypoint.setX(pos.getX());
            waypoint.setY(pos.getY() + 2);
            waypoint.setZ(pos.getZ());
        }

        try {
            XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static void addWaypoint(BlockPos pos, String name){
        IXaeroMinimapClientPlayNetHandler clientLevel = (IXaeroMinimapClientPlayNetHandler) (Minecraft.getInstance().player.connection);
        XaeroMinimapSession session = clientLevel.getXaero_minimapSession();

        WaypointsManager waypointsManager = session.getWaypointsManager();
        Waypoint instant = new Waypoint(pos.getX(), pos.getY() + 2, pos.getZ(), name, name.substring(0, 1), (int)(Math.random() * 16), 0, false);
        waypointsManager.getWaypoints().getList().add(instant);

        try {
            XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
    private static Waypoint getWaypoint(ArrayList<Waypoint> waypoints, IWaystone waystone){
        BlockPos waystonePos = waystone.getPos();
        for(Waypoint waypoint : waypoints){
            if(waypoint.getX() == waystonePos.getX() && waypoint.getY() == waystonePos.getY() + 2 && waypoint.getZ() == waystonePos.getZ()){
                return waypoint;
            }
        }
        return null;
    }
}
