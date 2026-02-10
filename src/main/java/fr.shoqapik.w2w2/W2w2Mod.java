package fr.shoqapik.w2w2;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


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

    public W2w2Mod() {
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


    public static <MSG>  void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }


}
