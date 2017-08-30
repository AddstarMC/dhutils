package me.desht.dhutils.nms.v1_12_R1;

import me.desht.dhutils.nms.api.NMSAbstraction;
import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.EnumSkyBlock;
import net.minecraft.server.v1_12_R1.IBlockData;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class NMSHandler implements NMSAbstraction {

    private Method d;
    private Method e;

    @Override
    public boolean setBlockFast(@NotNull World world, int x, int y, int z, int blockId, byte data) {
        net.minecraft.server.v1_12_R1.World w = ((CraftWorld) world).getHandle();
        Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        chunk.a(new BlockPosition(x & 0xF, y, z & 0xF), Block.getById(blockId).fromLegacyData(data));
        return true;
    }

    @Override
    public void forceBlockLightLevel(@NotNull World world, int x, int y, int z, int level) {
        net.minecraft.server.v1_12_R1.World w = ((CraftWorld) world).getHandle();
        w.a(EnumSkyBlock.BLOCK, new BlockPosition(x, y, z), level);
    }

    @Override
    public int getBlockLightEmission(int blockId) {
        Block block = Block.getById(blockId);
        return block.o(block.getBlockData());
    }

    @Override
    public int getBlockLightBlocking(int blockId) {
        Block block = Block.getById(blockId);
        return block.m(block.getBlockData());
    }

    @Override
    @Deprecated
    public void queueChunkForUpdate(@NotNull Player player, int cx, int cz) {
        player.getWorld().refreshChunk(cx, cz);
    }

    @NotNull
    @Override
    public Vector[] getBlockHitbox(@NotNull org.bukkit.block.Block block) {
        net.minecraft.server.v1_12_R1.World w = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData b = w.getType(pos);
        AxisAlignedBB bounds = b.d(w, pos); //avoid the @Nullable version.
        return new Vector[]{
                new Vector(bounds.a, bounds.b, bounds.c),
                new Vector(bounds.d, bounds.e, bounds.f)
        };
    }

    @Override
    public void recalculateBlockLighting(@NotNull World world, int x, int y, int z) {
        // Don't consider blocks that are completely surrounded by other non-transparent blocks
        if (!canAffectLighting(world, x, y, z)) {
            return;
        }

        int i = x & 0x0F;
        int j = y & 0xFF;
        int k = z & 0x0F;

        BlockPosition pos = new BlockPosition(i, j, k);
        CraftChunk craftChunk = (CraftChunk) world.getChunkAt(x >> 4, z >> 4);
        Chunk nmsChunk = craftChunk.getHandle();

        int i1 = k << 4 | i;
        int maxY = nmsChunk.heightMap[i1];

        IBlockData block = nmsChunk.getBlockData(new BlockPosition(i, j, k));
        int j2 = block.c();

        if (j2 > 0) {
            if (j >= maxY) {
                invokeNmsD(nmsChunk, i, j + 1, k);
            }
        } else if (j == maxY - 1) {
            invokeNmsD(nmsChunk, i, j, k);
        }

        if (nmsChunk.getBrightness(EnumSkyBlock.SKY, pos) > 0 || nmsChunk.getBrightness(EnumSkyBlock.BLOCK, pos) > 0) {
            invokeNmsD(nmsChunk, i, k);
        }

        net.minecraft.server.v1_12_R1.World w = ((CraftWorld) world).getHandle();
        w.c(EnumSkyBlock.BLOCK, pos);
    }

    private void invokeNmsD(Chunk nmsChunk, int i, int j, int k) {
        try {
            if (d == null) {
                Class[] classes = {int.class, int.class, int.class};
                d = Chunk.class.getDeclaredMethod("c", classes);
                d.setAccessible(true);
            }
            d.invoke(nmsChunk, i, j, k);
        } catch (Exception e) {
            System.out.println("Reflection exception: " + e);
        }
    }

    private void invokeNmsD(Chunk nmsChunk, int i, int j) {
        try {
            if (e == null) {
                Class[] classes = {int.class, int.class};
                e = Chunk.class.getDeclaredMethod("d", classes);
                e.setAccessible(true);
            }
            e.invoke(nmsChunk, i, j);
        } catch (Exception e) {
            System.out.println("Reflection exception: " + e);
        }
    }

    private boolean canAffectLighting(@NotNull World world, int x, int y, int z) {
        org.bukkit.block.Block base = world.getBlockAt(x, y, z);
        org.bukkit.block.Block east = base.getRelative(BlockFace.EAST);
        org.bukkit.block.Block west = base.getRelative(BlockFace.WEST);
        org.bukkit.block.Block up = base.getRelative(BlockFace.UP);
        org.bukkit.block.Block down = base.getRelative(BlockFace.DOWN);
        org.bukkit.block.Block south = base.getRelative(BlockFace.SOUTH);
        org.bukkit.block.Block north = base.getRelative(BlockFace.NORTH);

        return east.getType().isTransparent() ||
                west.getType().isTransparent() ||
                up.getType().isTransparent() ||
                down.getType().isTransparent() ||
                south.getType().isTransparent() ||
                north.getType().isTransparent();
    }
}