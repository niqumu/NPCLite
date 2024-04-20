package dev.niqumu.npclite.listener;

import dev.niqumu.npclite.NPCLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		// Spawn all NPCs for the player, as long as they're in the correct world
		NPCLite.getInstance().getNpcManager().getNpcs().values().forEach(npc -> {
			if (npc.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
				npc.spawn(event.getPlayer());
			}
		});
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {

		// Spawn all NPCs for the player, as long as they're in the (new) correct world
		NPCLite.getInstance().getNpcManager().getNpcs().values().forEach(npc -> {

			// Remove all NPCs first
			npc.remove(event.getPlayer());

			if (npc.getLocation().getWorld().equals(event.getTo().getWorld())) {
				npc.spawn(event.getPlayer());
			}
		});
	}
}
