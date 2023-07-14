package fr.shoqapik.w2w2;

import com.mojang.logging.LogUtils;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.common.settings.ModSettings;
import xaero.minimap.XaeroMinimap;

import java.io.IOException;
import java.util.ArrayList;

@Mod(W2w2Mod.MODID)
public class W2w2Mod
{
    public static final String MODID = "w2w2";

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public W2w2Mod()
    {
        INSTANCE.registerMessage(0, WaystoneActivatedPacket.class, WaystoneActivatedPacket::encode, WaystoneActivatedPacket::decode, WaystoneActivatedPacket::handle);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWaystoneActivated(WaystoneActivatedEvent event){
        if (!(event.getPlayer()).isLocalPlayer()) {
            IWaystone waystone = event.getWaystone();
            WaystoneActivatedPacket packet = new WaystoneActivatedPacket(waystone.getPos(), waystone.getName());
            sendToClient(packet, (ServerPlayer)event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onItemFishhed(ItemFishedEvent event){

    }
    @SubscribeEvent
    public void onWaystoneUpdated(WaystoneUpdateReceivedEvent event){
        IWaystone waystone = event.getWaystone();
        String name = waystone.getName();
        BlockPos pos = waystone.getPos();
        IXaeroMinimapClientPlayNetHandler clientLevel = (IXaeroMinimapClientPlayNetHandler) (Minecraft.getInstance().player.connection);
        XaeroMinimapSession session = clientLevel.getXaero_minimapSession();
        WaypointsManager waypointsManager = session.getWaypointsManager();
        Waypoint waypoint = getWaypoint(waypointsManager.getWaypoints().getList(), waystone);
        if(waypoint == null){
            Waypoint instant = new Waypoint(pos.getX(), pos.getY() + 2, pos.getZ(), name, name.substring(0, 1), (int)(Math.random() * ModSettings.ENCHANT_COLORS.length), 0, false);
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

    public static <MSG>  void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    private Waypoint getWaypoint(ArrayList<Waypoint> waypoints, IWaystone waystone){
        BlockPos waystonePos = waystone.getPos();
        for(Waypoint waypoint : waypoints){
            if(waypoint.getX() == waystonePos.getX() && waypoint.getY() == waystonePos.getY() + 2 && waypoint.getZ() == waystonePos.getZ()){
                return waypoint;
            }
        }
        return null;
    }

}
