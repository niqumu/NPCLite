package dev.niqumu.npclite.command;

import dev.niqumu.npclite.NPCLite;
import dev.niqumu.npclite.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class NPCCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

		// If no arguments were provided, print the version information
		if (args.length == 0) {
			this.sendLine(sender);
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + " NPCLite version " + NPCLite.VERSION);
			sender.sendMessage(ChatColor.YELLOW + " By " + NPCLite.AUTHORS);
			this.sendLine(sender);
			return true;
		}

		switch (args[0]) {
			case "add": {
				return this.handleAddCommand(sender, args);
			}
			case "edit": {
				return this.handleEditCommand(sender, args);
			}
			case "list": {
				return this.handleListCommand(sender, args);
			}
			case "remove": {
				return this.handleRemoveCommand(sender, args);
			}
			case "select": {
				return this.handleSelectCommand(sender, args);
			}
		}

		return false;
	}

	private boolean handleAddCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			return false; // TODO require explicit coords when coming from console
		}

		if (args.length <= 1) {
			sender.sendMessage(ChatColor.RED + "You must provide a name for the NPC!");
			return false;
		}

		String npcName = args[1];

		if (NPCLite.getInstance().getNpcManager().getNpcs().containsKey(npcName)) {
			sender.sendMessage(ChatColor.RED + "An NPC by the name \"" + npcName + "\" already exists!");
			return false;
		}

		NPCLite.getInstance().getNpcManager().add(npcName, ((Player) sender).getLocation());
		sender.sendMessage(ChatColor.GREEN + "Added NPC \"" + npcName + "\"!");

		return true;
	}

	private boolean handleEditCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) { // TODO
			sender.sendMessage(ChatColor.RED + "Editing is currently only supported in-game!");
			return false;
		}

		NPC selectedNPC = NPCLite.getInstance().getNpcManager().getSelectedNPC((Player) sender);

		// Ensure the player has an NPC selected
		if (selectedNPC == null) {
			sender.sendMessage(ChatColor.RED + "You do not have an NPC selected for editing! " +
				"Select one using the /npc select command!");
			return false;
		}

		// If the sender didn't provide an operation, tell them their selected NPC and return
		if (args.length <= 1) {
			sender.sendMessage(ChatColor.GREEN + "You are currently editing \"" +
				selectedNPC.getName() + "\"!");
			return true;
		}

		switch (args[1]) {
			case "move": {
				selectedNPC.setLocation(((Player) sender).getLocation());
				selectedNPC.refresh();

				sender.sendMessage(ChatColor.GREEN + "Moved \"" + selectedNPC.getName() +
					"\" to your current location!");
				return true;
			}
		}

		return false;
	}

	private boolean handleListCommand(CommandSender sender, String[] args) {

		// Whether we're listing all NPCs, not just the ones in the current world
		boolean listAll = args.length > 1 && args[1].equals("all") || !(sender instanceof Player);

		ArrayList<NPC> npcs = new ArrayList<>(NPCLite.getInstance().getNpcManager().getNpcs().values());
		npcs.removeIf(npc -> !listAll && !npc.sharesWorldWithPlayer((Player) sender));

		this.sendLine(sender);
		sender.sendMessage(ChatColor.YELLOW + " NPCs matching selection: " + ChatColor.WHITE + npcs.size());

		npcs.forEach(npc -> sender.sendMessage(String.format(ChatColor.GRAY + "  - " + ChatColor.WHITE + "" +
			ChatColor.BOLD + "" + ChatColor.RESET + "%s: " + ChatColor.GRAY + "%d, %d, %d", npc.getName(),
			npc.getLocation().getBlockX(), npc.getLocation().getBlockY(), npc.getLocation().getBlockZ())));

		this.sendLine(sender);

		return true;
	}

	private boolean handleRemoveCommand(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(ChatColor.RED + "You must specify an NPC!");
			return false;
		}

		String npcName = args[1];

		if (!NPCLite.getInstance().getNpcManager().getNpcs().containsKey(npcName)) {
			sender.sendMessage(ChatColor.RED + "An NPC by the name \"" + npcName + "\" doesn't exist!");
			return false;
		}

		NPCLite.getInstance().getNpcManager().remove(npcName);
		sender.sendMessage(ChatColor.GREEN + "Removed NPC \"" + npcName + "\"!");

		return true;
	}

	private boolean handleSelectCommand(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(ChatColor.RED + "You must specify an NPC!");
			return false;
		}

		String npcName = args[1];

		if (!NPCLite.getInstance().getNpcManager().getNpcs().containsKey(npcName)) {
			sender.sendMessage(ChatColor.RED + "An NPC by the name \"" + npcName + "\" doesn't exist!");
			return false;
		}

		NPCLite.getInstance().getNpcManager().getSelectedNpcs().remove(sender.getName());
		NPCLite.getInstance().getNpcManager().getSelectedNpcs().put(sender.getName(), npcName);
		sender.sendMessage(ChatColor.GREEN + "Selected NPC \"" + npcName + "\" for editing!");

		NPC npc = NPCLite.getInstance().getNpcManager().getNpcs().get(npcName);

		// Warn the editor if they aren't in the same world as the NPC
		if (!(sender instanceof Player) || !((Player) sender).getWorld().equals(npc.getLocation().getWorld())) {
			sender.sendMessage(ChatColor.YELLOW + "Warning: You are editing from another world, or " +
				"from the server console! Proceed with caution.");
		}

		return true;
	}

	private void sendLine(CommandSender sender) {
		sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH +
			"---------------------------------------------------------");
	}
}
