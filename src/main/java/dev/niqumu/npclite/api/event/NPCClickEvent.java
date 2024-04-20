package dev.niqumu.npclite.api.event;

import dev.niqumu.npclite.npc.NPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class NPCClickEvent implements Event {

	/**
	 * The player that triggered the event
	 */
	private final Player player;

	/**
	 * The NPC the player clicked on
	 */
	private final NPC npc;

	/**
	 * The type of click
	 * @see ClickType
	 */
	private ClickType type;

	public enum ClickType {
		ATTACK,
		INTERACT
	}
}
