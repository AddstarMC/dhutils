package me.desht.dhutils.nms.pre;

import net.minecraft.server.Block;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EnumSkyBlock;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.desht.dhutils.nms.api.NMSAbstraction;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class NMSHandler implements NMSAbstraction {

	@Override
	public boolean setBlockFast(@NotNull World world, int x, int y, int z, int blockId, byte data) {
		net.minecraft.server.World w = ((CraftWorld) world).getHandle();
		Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		return chunk.a(x & 0x0f, y, z & 0x0f, blockId, data);
	}

	@Override
	public void forceBlockLightLevel(@NotNull World world, int x, int y, int z, int level) {
		net.minecraft.server.World w = ((CraftWorld) world).getHandle();
		w.b(EnumSkyBlock.BLOCK, x, y, z, level);
	}

	@Override
	public int getBlockLightEmission(int blockId) {
		return Block.lightEmission[blockId];
	}

	@Override
	public int getBlockLightBlocking(int blockId) {
		return Block.lightBlock[blockId];
	}

	@SuppressWarnings("unchecked")
	@Override
	public void queueChunkForUpdate(@NotNull Player player, int cx, int cz) {
		((CraftPlayer) player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
	}

	@NotNull
	@Override
	public Vector[] getBlockHitbox(org.bukkit.block.Block block) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void recalculateBlockLighting(@NotNull World world, int x, int y, int z) {
		net.minecraft.server.World w = ((CraftWorld) world).getHandle();
		w.z(x, y, z);
	}
}
