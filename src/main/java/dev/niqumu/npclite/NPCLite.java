package dev.niqumu.npclite;

import dev.niqumu.npclite.command.NPCCommand;
import dev.niqumu.npclite.listener.PlayerListener;
import dev.niqumu.npclite.network.NetworkManager;
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

	@Getter
	private NetworkManager networkManager;

	@Override
	public void onEnable() {
		NPCLite.instance = this;

		this.npcManager = new NPCManager();
		this.networkManager = new NetworkManager();

		// Register the /npc command
		this.getCommand("npc").setExecutor(new NPCCommand());

		// Register the player listener
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public static NPCLite getInstance() {
		return instance;
	}
}
