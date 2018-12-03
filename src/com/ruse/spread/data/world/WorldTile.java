package com.ruse.spread.data.world;

import net.lintford.library.core.maths.Vector3f;

public class WorldTile {

	/*
	 * each tile is an short: 0b00000000 00000xxx - tile type 0b00000000 0xxxx000 - tile height [0-15] 0bxxxxxxxx x0000000 - spare
	 */

	public static final char TILE_TYPE_GRASS = 0b00000001;
	public static final char TILE_TYPE_DIRT = 0b00000010;
	public static final char TILE_TYPE_SAND = 0b00000100;

	private static Vector3f DIRT_COLOR = new Vector3f(124f / 255f, 75f / 255f, 42f / 255f);
	private static Vector3f GRASS_COLOR = new Vector3f(54f / 255f, 104f / 255f, 59f / 255f);
	private static Vector3f SAND_COLOR = new Vector3f(165f / 255f, 146f / 255f, 71f / 255f);

	public static Vector3f getTileColor(int pTile) {
		int tileType = getTileType(pTile);
		switch (tileType) {

		case TILE_TYPE_SAND:
			return SAND_COLOR;

		case TILE_TYPE_DIRT:
			return DIRT_COLOR;

		case TILE_TYPE_GRASS:
		default:
			return GRASS_COLOR;
		}
	}

	public static int getTileHeight(int pTile) {
		int result = ((pTile >> 3) & 0b000001111);
		return result;

	}

	public static int getTileType(int pTile) {
		return pTile & 0b00000111;
	}

}
