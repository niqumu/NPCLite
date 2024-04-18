package dev.niqumu.npclite.npc;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPC {

	/**
	 * The internal name of the NPC
	 */
	@Getter
	private final String name;

	/**
	 * The location of the NPC, including position, rotation, and world information
	 */
	@Getter @Setter
	private Location location;

	/**
	 * The display name of the NPC
	 */
	@Getter @Setter
	private String displayName;

	private EntityPlayer entity;

	public NPC(String name, Location location) {
		this.name = name;
		this.location = location;
		this.displayName = name;

		this.createEntity();
	}

	private void createEntity() {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) this.location.getWorld()).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(), this.displayName);

		this.entity = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
	}

	public boolean sharesWorldWithPlayer(Player player) {
		return this.location.getWorld().equals(player.getWorld());
	}

	public void refresh() {
		this.removeAll();
		this.createEntity();
		this.spawnAll();
	}

	/**
	 * Spawns the NPC for all players in the correct world via packets
	 */
	public void spawnAll() {
		this.location.getWorld().getPlayers().forEach(this::spawn);
	}

	/**
	 * Spawns the NPC for the provided player via packets
	 * @param player The player to spawn the NPC for
	 */
	public void spawn(Player player) {
		this.entity.setLocation(this.location.getX(), this.location.getY(), this.location.getZ(),
			this.location.getYaw(), this.location.getPitch());

		PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;

		// Send the player info
		con.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.entity));

		// Spawn the physical NPC
		con.sendPacket(new PacketPlayOutNamedEntitySpawn(this.entity));

		// Rotate the physical NPC (not sure why this is needed, but it is)
		con.sendPacket(new PacketPlayOutEntityHeadRotation(this.entity, (byte) (this.entity.yaw * 256 / 360)));
	}

	/**
	 * Removes the NPC for all players in the correct world via packets
	 */
	public void removeAll() {
		this.location.getWorld().getPlayers().forEach(this::remove);
	}

	/**
	 * Removes the NPC for the provided player via packets
	 * @param player The player to removes the NPC for
	 */
	public void remove(Player player) {
		PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;

		// Remove the physical NPC
		con.sendPacket(new PacketPlayOutEntityDestroy(this.entity.getId()));

		// Remove the NPC from the player list (tab)
		con.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.entity));
	}
}
