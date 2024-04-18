package dev.niqumu.npclite;

import dev.niqumu.npclite.command.NPCCommand;
import dev.niqumu.npclite.listener.PlayerListener;
import dev.niqumu.npclite.npc.NPCManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPCLite extends JavaPlugin {

	public static String VERSION = "0.0.1-SNAPSHOT";
	public static String AUTHORS = "niqumu";

	private static NPCLite instance;

	@Getter
	private NPCManager npcManager;

	@Override
	public void onEnable() {
		NPCLite.instance = this;

		this.npcManager = new NPCManager();

		// Register the /npc command
		this.getCommand("npc").setExecutor(new NPCCommand());

		// Register the player listener
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	public static NPCLite getInstance() {
		return instance;
	}
}
