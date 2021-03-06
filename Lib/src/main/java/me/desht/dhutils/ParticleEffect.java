package me.desht.dhutils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

/**
 * ParticleEffect
 *
 * This particle effect library is based on code created by DarkBlade12 based off content from microgeek
 * You are free to use it, modify it and redistribute it under the condition to give credit to me and microgeek
 * Modified by desht:
 * - got rid of various name/id methods from the enum (the id had no meaning to the protocol)
 * - use ProtocolLib instead of reflection - safer and more concise
 * - use Bukkit Material instead of int for icon/tile crack effects
 */
public enum ParticleEffect {

	HUGE_EXPLOSION("hugeexplosion"),
	LARGE_EXPLODE("largeexplode"),
	FIREWORKS_SPARK("fireworksSpark"),
	BUBBLE("bubble"),
	SUSPEND("suspend"),
	DEPTH_SUSPEND("depthSuspend"),
	TOWN_AURA("townaura"),
	CRIT("crit"),
	MAGIC_CRIT("magicCrit"),
	MOB_SPELL("mobSpell"),
	MOB_SPELL_AMBIENT("mobSpellAmbient"),
	SPELL("spell"),
	INSTANT_SPELL("instantSpell"),
	WITCH_MAGIC("witchMagic"),
	NOTE("note"),
	PORTAL("portal"),
	ENCHANTMENT_TABLE("enchantmenttable"),
	EXPLODE("explode"),
	FLAME("flame"),
	LAVA("lava"),
	FOOTSTEP("footstep"),
	SPLASH("splash"),
	LARGE_SMOKE("largesmoke"),
	CLOUD("cloud"),
	RED_DUST("reddust"),
	SNOWBALL_POOF("snowballpoof"),
	DRIP_WATER("dripWater"),
	DRIP_LAVA("dripLava"),
	SNOW_SHOVEL("snowshovel"),
	SLIME("slime"),
	HEART("heart"),
	ANGRY_VILLAGER("angryVillager"),
	HAPPY_VILLAGER("happyVillager");

	private String name;

	ParticleEffect(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}

	/**
	 * Plays a particle effect at a location which is only shown to a specific player.
	 * @param p the Player
	 * @param loc the location
	 * @param offsetX  the x offset to start the animation
	 * @param offsetY the y offset as above
	 * @param offsetZ the z offset as above
	 * @param amount  the amount of effects.
	 * @param speed speed of effect
	 */
	public void play(Player p, @NotNull Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		 sendPacket(p, createNormalPacket(this, loc, offsetX, offsetY, offsetZ, speed, amount));
	 }

	 /**
	  * Plays a particle effect at a location which is shown to all players in the current world.
	  * @param loc the location
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  * @param speed speed of effect
	  */
	 public void play(@NotNull Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		 PacketContainer packet = createNormalPacket(this, loc, offsetX, offsetY, offsetZ, speed, amount);
		 for (Player p : loc.getWorld().getPlayers()) {
			 sendPacket(p, packet);
		 }
	 }

	 /**
	  * Plays a particle effect at a location which is shown to all players within a certain range in the current world.
	  * @param loc the location
	  * @param range the range to show the animation
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  * @param speed speed of effect
	  */
	 public void play(@NotNull Location loc, double range, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		 PacketContainer packet = createNormalPacket(this, loc, offsetX, offsetY, offsetZ, speed, amount);
		 range *= range;
		 for (Player p : loc.getWorld().getPlayers()) {
			 if (p.getLocation().distanceSquared(loc) <= range) {
				 sendPacket(p, packet);
			 }
		 }
	 }

	 /**
	  * Plays a tilecrack effect at a location which is only shown to a specific player.
	  * @param p the Player
	  * @param loc the location
	  * @param mat Material to crack
	  * @param data byte
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  */
	 public static void playTileCrack(Player p, @NotNull Location loc, @NotNull Material mat, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
		 sendPacket(p, createTileCrackPacket(mat, data, loc, offsetX, offsetY, offsetZ, amount));
	 }

	 /**
	  * Plays a tilecrack effect at a location which is shown to all players in the current world.
	  * @param loc the location
	  * @param mat Material to crack
	  * @param data byte
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  */
	 public static void playTileCrack(@NotNull Location loc, @NotNull Material mat, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
		 PacketContainer packet = createTileCrackPacket(mat, data, loc, offsetX, offsetY, offsetZ, amount);
		 for (Player p : loc.getWorld().getPlayers()) {
			 sendPacket(p, packet);
		 }
	 }

	 /**
	  * Plays a tilecrack effect at a location which is shown to all players within a certain range in the current world
	  * @param loc the location
	  * @param range the range to show the effect
	  * @param mat Material to crack
	  * @param data byte
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  */
	 public static void playTileCrack(@NotNull Location loc, double range, @NotNull Material mat, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
		 PacketContainer packet = createTileCrackPacket(mat, data, loc, offsetX, offsetY, offsetZ, amount);
		 range *= range;
		 for (Player p : loc.getWorld().getPlayers()) {
			 if (p.getLocation().distanceSquared(loc) <= range) {
				 sendPacket(p, packet);
			 }
		 }
	 }

	 /**
	  * Plays an iconcrack effect at a location which is only shown to a specific player.
	  * @param p the Player
	  * @param mat the material
	  * @param loc the location
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  */
	 public static void playIconCrack(Player p, @NotNull Location loc, @NotNull Material mat, float offsetX, float offsetY, float offsetZ, int amount) {
		 sendPacket(p, createIconCrackPacket(mat, loc, offsetX, offsetY, offsetZ, amount));
	 }

	 /**
	  * Plays an iconcrack effect at a location which is shown to all players in the current world.
	  * @param mat the material
	  * @param loc the location
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  */
	 public static void playIconCrack(@NotNull Location loc, @NotNull Material mat, float offsetX, float offsetY, float offsetZ, int amount) {
		 PacketContainer packet = createIconCrackPacket(mat, loc, offsetX, offsetY, offsetZ, amount);
		 for (Player p : loc.getWorld().getPlayers()) {
			 sendPacket(p, packet);
		 }
	 }

	 /**
	  * Plays an iconcrack effect at a location which is shown to all players within a certain range in the current world.
	  * @param mat the material
	  * @param range the range
	  * @param loc the location
	  * @param offsetX  the x offset to start the animation
	  * @param offsetY the y offset as above
	  * @param offsetZ the z offset as above
	  * @param amount  the amount of effects.
	  */
	 public static void playIconCrack(@NotNull Location loc, double range, @NotNull Material mat, float offsetX, float offsetY, float offsetZ, int amount) {
		 PacketContainer packet = createIconCrackPacket(mat, loc, offsetX, offsetY, offsetZ, amount);
		 range *= range;
		 for (Player p : loc.getWorld().getPlayers()) {
			 if (p.getLocation().distanceSquared(loc) <= range) {
				 sendPacket(p, packet);
			 }
		 }
	 }

	@NotNull
	private PacketContainer createNormalPacket(@NotNull ParticleEffect effect, @NotNull Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		 return createPacket(effect.getName(), loc, offsetX, offsetY, offsetZ, speed, amount);
	 }

	@NotNull
	private static PacketContainer createTileCrackPacket(@NotNull Material mat, byte data, @NotNull Location loc, float offsetX, float offsetY, float offsetZ, int amount) {
		 return createPacket("tilecrack_" + mat.getId() + "_" + data, loc, offsetX, offsetY, offsetZ, 0.1F, amount);
	 }

	@NotNull
	private static PacketContainer createIconCrackPacket(@NotNull Material mat, @NotNull Location loc, float offsetX, float offsetY, float offsetZ, int amount) {
		 return createPacket("iconcrack_" + mat.getId(), loc, offsetX, offsetY, offsetZ, 0.1F, amount);
	 }

	@NotNull
	private static PacketContainer createPacket(String effectName, @NotNull Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		 PacketContainer particlePacket = new PacketContainer(63);
		 Validate.isTrue(amount > 0, "Amount of particles must be greater than 0");
		 particlePacket.getStrings().write(0, effectName);
		 particlePacket.getFloat().write(0, (float)loc.getX()).write(1, (float)loc.getY()).write(2, (float)loc.getZ());
		 particlePacket.getFloat().write(3, offsetX).write(4, offsetY).write(5, offsetZ);
		 particlePacket.getFloat().write(6, speed);
		 particlePacket.getIntegers().write(0, amount);
		 return particlePacket;
	 }

	 private static void sendPacket(Player p, PacketContainer packet) {
		 try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	 }
}
