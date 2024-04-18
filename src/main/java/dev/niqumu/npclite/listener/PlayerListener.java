package dev.niqumu.npclite.listener;

import dev.niqumu.npclite.NPCLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		NPCLite.getInstance().getNpcManager().getNpcs().values().forEach(npc -> {
			if (npc.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
				npc.spawn(event.getPlayer());
			}
		});
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		NPCLite.getInstance().getNpcManager().getNpcs().values().forEach(npc -> {
			npc.remove(event.getPlayer());

			if (npc.getLocation().getWorld().equals(event.getTo().getWorld())) {
				npc.spawn(event.getPlayer());
			}
		});
	}
}
