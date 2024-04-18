package dev.niqumu.npclite.npc;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NPCManager {

	/**
	 * A map of NPCs, using their names as keys
	 */
	@Getter
	private final Map<String, NPC> npcs = new ConcurrentHashMap<>();

	/**
	 * A map of NPCs that players have selected
	 */
	@Getter
	private final Map<String, String> selectedNpcs = new ConcurrentHashMap<>();

	public void add(String name, Location location) {
		NPC npc = new NPC(name, location);
		npc.spawnAll();

		this.npcs.put(name, npc);
	}

	public void remove(String name) {
		NPC npc = this.getNpcs().get(name);
		npc.removeAll();

		this.npcs.remove(name);
	}

	/**
	 * Gets the currently selected NPC for editing of a current player
	 * @param player The player to lookup
	 */
	public NPC getSelectedNPC(Player player) {

		if (!this.selectedNpcs.containsKey(player.getName())) {
			return null;
		}

		String selectedNPCName = this.selectedNpcs.get(player.getName());
		return this.npcs.getOrDefault(selectedNPCName, null);
	}
}
