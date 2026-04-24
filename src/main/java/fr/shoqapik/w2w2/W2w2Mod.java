package fr.shoqapik.w2w2;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(W2w2Mod.MODID)
public class W2w2Mod {

    public static final String MODID = "w2w2";

    public W2w2Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::registerPayloads);

        // Waystone activada por el jugador (ya tenía nombre)
        WaystoneActivatedEvent.EVENT.register(this::onWaystoneActivated);

        // Waystone actualizada — incluye cuando se le asigna nombre por primera vez
        WaystoneUpdatedEvent.EVENT.register(this::onWaystoneUpdated);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                WaystoneActivatedPayload.TYPE,
                WaystoneActivatedPayload.STREAM_CODEC,
                (payload, ctx) -> {
                    if (!ModList.get().isLoaded("xaerominimap")) return;
                    XaeronCompatibility.addWaypoint(payload.pos(), payload.name());
                }
        );
    }

    private void onWaystoneActivated(WaystoneActivatedEvent event) {
        if (event.player().level().isClientSide()) return;
        if (!(event.player() instanceof ServerPlayer serverPlayer)) return;

        Waystone waystone = event.waystone();
        if (!waystone.hasName()) return;

        sendWaypointPacket(serverPlayer, waystone);
    }

    private void onWaystoneUpdated(WaystoneUpdatedEvent event) {
        // WaystoneUpdatedEvent no tiene player — hay que enviar a todos los jugadores online
        // que hayan activado esta waystone. Sin acceso al server directo, enviamos a todos.
        Waystone waystone = event.waystone();
        if (!waystone.hasName()) return;

        // Acceder al server via MinecraftServer
        net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendWaypointPacket(player, waystone);
        }
    }

    private void sendWaypointPacket(ServerPlayer player, Waystone waystone) {
        String name = waystone.getName().getString();
        BlockPos pos = waystone.getPos();
        PacketDistributor.sendToPlayer(player, new WaystoneActivatedPayload(pos, name));
    }
}
