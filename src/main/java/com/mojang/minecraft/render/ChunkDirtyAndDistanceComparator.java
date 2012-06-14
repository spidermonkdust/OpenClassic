package com.mojang.minecraft.render;

import com.mojang.minecraft.player.Player;
import java.util.Comparator;

public final class ChunkDirtyAndDistanceComparator implements Comparator<com.mojang.minecraft.render.Chunk> {

	private Player player;

	public ChunkDirtyAndDistanceComparator(Player player) {
		this.player = player;
	}

	@Override
	public int compare(Chunk chunk, Chunk other) {
		if (chunk.chunkDirty || !other.chunkDirty) {
			if (other.chunkDirty) {
				float sqDist = chunk.distanceSquared(this.player);
				float otherSqDist = other.distanceSquared(this.player);

				if (sqDist >= otherSqDist) {
					return -1;
				} else {
					return 1;	
				}
			} else {
				return -1;
			}
		} else {
			return 1;
		}
	}
	
}