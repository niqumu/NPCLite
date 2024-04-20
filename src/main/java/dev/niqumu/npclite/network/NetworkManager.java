package dev.niqumu.npclite.network;

import dev.niqumu.npclite.NPCLite;
import dev.niqumu.npclite.api.event.NPCClickEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NetworkManager {

	public NetworkManager() {

		// Inject all currently connected players
		Bukkit.getOnlinePlayers().forEach(this::ejectPlayer);
		Bukkit.getOnlinePlayers().forEach(this::injectPlayer);
	}

	public void injectPlayer(Player player) {

		// Create a new netty handler
		ChannelDuplexHandler handler = new ChannelDuplexHandler() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {

				handlePacket(player, (Packet<?>) packet);
				super.channelRead(ctx, packet);
			}

		};

		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().
			playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), handler);
	}

	public void ejectPlayer(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> channel.pipeline().remove(player.getName()));
	}

	@SneakyThrows
	private void handlePacket(Player sender, Packet<?> packet) {

		// We're only interested in PacketPlayInUseEntity packets
		if (!(packet instanceof PacketPlayInUseEntity)) {
			return;
		}

		PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;

		// When the player right-clicks an NPC, both an INTERACT and INTERACT_AT packet are sent. This filter
		//   avoids triggering two events from a single click
		if (useEntity.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT)) {
			return;
		}

		// Get the entity ID of the target via reflection
		Field targetIDField = PacketPlayInUseEntity.class.getDeclaredField("a");
		targetIDField.setAccessible(true);
		int targetID = targetIDField.getInt(useEntity);

		// Iterate over NPCs to see if any of them match
		NPCLite.getInstance().getNpcManager().getNpcs().values().forEach(npc -> {

			if (npc.getEntity().getId() == targetID) {

				NPCClickEvent.ClickType type = useEntity.a().equals(PacketPlayInUseEntity.
					EnumEntityUseAction.ATTACK) ? NPCClickEvent.ClickType.ATTACK :
					NPCClickEvent.ClickType.INTERACT;

				npc.handleClick(new NPCClickEvent(sender, npc, type));
			}
		});
	}
}