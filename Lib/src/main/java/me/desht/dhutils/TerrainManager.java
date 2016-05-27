package me.desht.dhutils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.registry.WorldData;
import org.bukkit.Location;
import org.bukkit.World;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

/**
 * @author desht
 *
 * A wrapper class for the WorldEdit terrain loading & saving API to make things a little
 * simple for other plugins to use.
 */
public class TerrainManager {
	private static final String EXTENSION = "schematic";

	private final WorldEdit we;
	private final LocalSession localSession;
	private final EditSession editSession;
	private final Player wePlayer;

	/**
	 * Constructor
	 * 
	 * @param wep	the WorldEdit plugin instance
	 * @param player	the player to work with
	 */
	public TerrainManager(WorldEditPlugin wep, org.bukkit.entity.Player player) {
		we = wep.getWorldEdit();
		wePlayer = wep.wrapPlayer(player);
		localSession = we.getSessionManager().get(wePlayer);
		editSession = localSession.createEditSession(wePlayer);
	}

	/**
	 * Constructor
	 * 
	 * @param wep	the WorldEdit plugin instance
	 * @param world	the world to work in
	 */
	public TerrainManager(WorldEditPlugin wep, World world) {
		we = wep.getWorldEdit();
		wePlayer = null;
		localSession = new LocalSession(we.getConfiguration());
		editSession = we.getEditSessionFactory().getEditSession((com.sk89q.worldedit.world.World) new BukkitWorld(world), we.getConfiguration().maxChangeLimit);
	}

	/**
	 * Write the terrain bounded by the given locations to the given file as a MCedit format
	 * schematic.
	 * 
	 * @param saveFile	a File representing the schematic file to create
	 * @param l1	one corner of the region to save
	 * @param l2	the corner of the region to save, opposite to l1
	 * @throws DataException
	 * @throws IOException
	 */
	public void saveTerrain(File saveFile, Location l1, Location l2) throws FilenameException, DataException, IOException {
		Vector min = getMin(l1, l2);
		Vector max = getMax(l1, l2);

		saveFile = we.getSafeSaveFile(wePlayer,
				saveFile.getParentFile(), saveFile.getName(),
				EXTENSION, EXTENSION);

		editSession.enableQueue();
		CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
		clipboard.copy(editSession);
		SchematicFormat.MCEDIT.save(clipboard, saveFile);
		editSession.flushQueue();
	}

	/**
	 * Load the data from the given schematic file and paste it at the given location.  If the location is null, then
	 * paste it at the saved data's origin.
	 * 
	 * @param saveFile	a File representing the schematic file to load
	 * @param loc		the location to paste the clipboard at (may be null)
	 * @throws FilenameException
	 * @throws DataException
	 * @throws IOException
	 * @throws MaxChangedBlocksException
	 * @throws EmptyClipboardException
	 */
	public void loadSchematic(File saveFile, Location loc) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException {
		saveFile = we.getSafeOpenFile(wePlayer,
				saveFile.getParentFile(), saveFile.getName(),
				EXTENSION, EXTENSION);

		editSession.enableQueue();
		ClipboardFormat format = ClipboardFormat.findByAlias("schematic");
		Closer closer = Closer.create();
		WorldData worldData = wePlayer.getWorld().getWorldData();
		try {
			FileInputStream fis = closer.register(new FileInputStream(saveFile));
			BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
			ClipboardReader reader = format.getReader(bis);
			Clipboard board = reader.read(worldData);
			ClipboardHolder holder = new ClipboardHolder(board, worldData);
			localSession.setClipboard(holder);
			localSession.getClipboard().createPaste(editSession, editSession.getWorld().getWorldData()).to(getPastePosition(loc)).ignoreAirBlocks(false).build();
			editSession.flushQueue();
			we.flushBlockBag(wePlayer, editSession);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load the data from the given schematic file and paste it at the saved clipboard's origin.
	 * 
	 * @param saveFile
	 * @throws FilenameException
	 * @throws DataException
	 * @throws IOException
	 * @throws MaxChangedBlocksException
	 * @throws EmptyClipboardException
	 */
	public void loadSchematic(File saveFile) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException {
		loadSchematic(saveFile, null);
	}

	private Vector getPastePosition(Location loc) throws EmptyClipboardException {
		if (loc == null)
			return localSession.getClipboard().getClipboard().getOrigin();
		else 
			return new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	private Vector getMin(Location l1, Location l2) {
		return new Vector(
		                  Math.min(l1.getBlockX(), l2.getBlockX()),
		                  Math.min(l1.getBlockY(), l2.getBlockY()),
		                  Math.min(l1.getBlockZ(), l2.getBlockZ())
				);
	}

	private Vector getMax(Location l1, Location l2) {
		return new Vector(
		                  Math.max(l1.getBlockX(), l2.getBlockX()),
		                  Math.max(l1.getBlockY(), l2.getBlockY()),
		                  Math.max(l1.getBlockZ(), l2.getBlockZ())
				);
	}
}
