package com.ruse.spread.data.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.noise.ImprovedNoise;
import net.lintford.library.data.BaseData;

public class World extends BaseData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -4942760447159744932L;

	// TODO: Add to world settings
	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;

	public static final int NUM_CITIES = 4;

	public static final int TILE_SIZE = 32;

	// TODO: Move these into the WorldNode class
	public static final int TILE_TYPE_SPAWNER = 0b00000001;
	public static final int TILE_TYPE_CITY = 0b00000010;
	public static final int TILE_TYPE_MINE = 0b00000100;
	public static final int TILE_TYPE_FARM = 0b00001000;

	private static int region_index_counter = 0;

	public static final int INVALID_INDEX = -1;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public final int width;
	public final int height;
	public int[] ground; // stores type and height
	public int[] regions; // stores region ID
	public int[] timer; // variants/timers on tile animations
	public int[] variants; // variants/timers on tile animations

	public int[] spreaderDepth; // stores the depth of the spread at any one tile (region independent)
	public int[] regionHealth; // Stores the health at this tile

	private ImprovedNoise mNoise;

	public List<WorldRegion> mRegionInstances = new ArrayList<>();
	public List<WorldContourLine> mWorldContours = new ArrayList<>();

	public int mHQTileIndex;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int getLeftTileIndex(int pIndex) {
		if (pIndex % width == 0)
			return INVALID_INDEX; // already on left side of the map

		return pIndex - 1;
	}

	public int getRightTileIndex(int pIndex) {
		if (pIndex % width == width - 1)
			return INVALID_INDEX; // already on left side of the map

		return pIndex + 1;
	}

	public int getTopTileIndex(int pIndex) {
		if (pIndex / width == 0)
			return INVALID_INDEX; // already on left side of the map

		return pIndex - width;
	}

	public int getBottomTileIndex(int pIndex) {
		int l1 = pIndex / width;
		if (l1 == height - 1)
			return INVALID_INDEX; // already on left side of the map

		return pIndex + width;
	}

	public WorldRegion getWorldRegion(int pUID) {
		final int lNumRegions = mRegionInstances.size();
		for (int i = 0; i < lNumRegions; i++) {
			if (mRegionInstances.get(i).uid() == pUID) {
				return mRegionInstances.get(i);

			}

		}

		return null;

	}

	public boolean checkStillSpreader() {
		final int lNumRegionsCount = mRegionInstances.size();
		for (int i = 0; i < lNumRegionsCount; i++) {
			if (mRegionInstances.get(i).type() == World.TILE_TYPE_SPAWNER)
				return true;

		}

		return false;
	}

	public int getNumSpreaderRegions() {
		int lReturnResult = 0;
		final int lNumRegionsCount = mRegionInstances.size();
		for (int i = 0; i < lNumRegionsCount; i++) {
			if (mRegionInstances.get(i).type() == World.TILE_TYPE_SPAWNER)
				lReturnResult++;

		}

		return lReturnResult;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public World() {
		width = WIDTH;
		height = HEIGHT;

		ground = new int[width * height];
		regions = new int[width * height];
		spreaderDepth = new int[width * height];
		regionHealth = new int[width * height];
		variants = new int[width * height];
		timer = new int[width * height];

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void generateNewWorld() {
		Arrays.fill(ground, 0x0);
		Arrays.fill(regions, 0x0);
		Arrays.fill(spreaderDepth, 0x0);
		Arrays.fill(regionHealth, 0x0);
		Arrays.fill(timer, 0x0);
		Arrays.fill(variants, 0x0);

		region_index_counter = 1;

		mRegionInstances.clear();
		mWorldContours.clear();

		mNoise = new ImprovedNoise(System.nanoTime());

		createStartingArea();

		randomiseHeights();
		assignTiles();

		createSpawnerPositions();
		createMapRegions();

		generateContours();
		
		// Get HQ Tileindex
		mHQTileIndex = getHighestTile();

	}

	private int getHighestTile() {
		int lHighestPoint = 0;
		int lHighestIndex = 0;
		for (int i = 0; i < width * height; i++) {
			int lHeight = WorldTile.getTileHeight(ground[i]);
			if (lHeight > lHighestPoint) {
				lHighestPoint = lHeight;
				lHighestIndex = i;
			}

		}

		return lHighestIndex;

	}

	private void createStartingArea() {

		int lFarmTileIndex = getRandomWithinRange(mHQTileIndex, 5);
		for (int i = 0; i < 5; i++) {
			if (lFarmTileIndex != -1)
				break;
		}

		if (lFarmTileIndex != -1)
			createNewRegion(World.TILE_TYPE_FARM, lFarmTileIndex);

		int lMineTileIndex = getRandomWithinRange(mHQTileIndex, 5);
		for (int i = 0; i < 5; i++) {
			if (lMineTileIndex != -1)
				break;
		}

		if (lMineTileIndex != -1)
			createNewRegion(World.TILE_TYPE_MINE, lMineTileIndex);

	}

	private int getRandomWithinRange(int pTileCoord, int pRange) {
		int lTileX = pTileCoord % World.WIDTH;
		int lTileY = pTileCoord / World.WIDTH;

		int lHalfRange = pRange;

		final int lNumTries = 5;
		for (int i = 0; i < lNumTries; i++) {

			int lNewX = lTileX + RandomNumbers.random(0, pRange) - lHalfRange;
			if (lNewX < 0)
				lNewX = 0;
			if (lNewX >= WIDTH)
				lNewX = WIDTH - 1;

			int lNewY = lTileY + RandomNumbers.random(0, pRange) - lHalfRange;
			if (lNewY < 0)
				lNewY = 0;
			if (lNewY >= HEIGHT)
				lNewY = HEIGHT - 1;

			int lSuggestedIndex = lNewY * World.WIDTH + lNewX;

			if (lSuggestedIndex < 0 || lSuggestedIndex >= WIDTH * HEIGHT)
				continue;

			// Check regions collisions
			if (regions[lSuggestedIndex] != 0x0)
				continue;

			return lSuggestedIndex;

		}

		return -1;
	}

	private void assignTiles() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;
				int tileHeight = WorldTile.getTileHeight(ground[ti]);

				if (tileHeight < 8) {
					ground[ti] = (ground[ti] | WorldTile.TILE_TYPE_SAND);
				} else if (tileHeight < 6) {
					ground[ti] = (ground[ti] | WorldTile.TILE_TYPE_DIRT);
				} else {
					ground[ti] = (ground[ti] | WorldTile.TILE_TYPE_GRASS);
				}

			}

		}

	}

	private void createSpawnerPositions() {
		int lLowestPoint = 99;
		int lLowestIndex = -1;

		// Top row
		for (int i = 0; i < width; i++) {
			int lHeight = WorldTile.getTileHeight(ground[i]);
			if (lHeight < lLowestPoint) {
				lLowestPoint = lHeight;
				lLowestIndex = i;
			}
		}

		if (lLowestIndex != -1)
			createNewRegion(TILE_TYPE_SPAWNER, lLowestIndex);

		lLowestPoint = 99;
		lLowestIndex = -1;

		// Left row
		for (int i = 0; i < width * height; i += width) {
			int lHeight = WorldTile.getTileHeight(ground[i]);
			if (lHeight < lLowestPoint) {
				lLowestPoint = lHeight;
				lLowestIndex = i;
			}
		}

		if (lLowestIndex != -1)
			createNewRegion(TILE_TYPE_SPAWNER, lLowestIndex);

		lLowestPoint = 99;
		lLowestIndex = -1;

		// bottom row
		for (int i = width * height - width; i < width * height; i++) {
			int lHeight = WorldTile.getTileHeight(ground[i]);
			if (lHeight < lLowestPoint) {
				lLowestPoint = lHeight;
				lLowestIndex = i;
			}
		}

		if (lLowestIndex != -1)
			createNewRegion(TILE_TYPE_SPAWNER, lLowestIndex);

		lLowestPoint = 99;
		lLowestIndex = -1;

		// right row
		for (int i = width - 1; i < width * height; i += width) {
			int lHeight = WorldTile.getTileHeight(ground[i]);
			if (lHeight < lLowestPoint) {
				lLowestPoint = lHeight;
				lLowestIndex = i;
			}
		}

		if (lLowestIndex != -1)
			createNewRegion(TILE_TYPE_SPAWNER, lLowestIndex);

	}

	private WorldRegion createNewRegion(int pType, int pSpawnTileID) {
		WorldRegion lNewRegion = WorldRegion.createNewRegion(region_index_counter++, pType);

		lNewRegion.tiles().add(pSpawnTileID);

		regions[pSpawnTileID] = lNewRegion.uid();
		regionHealth[pSpawnTileID] = 100;

		if (pType == TILE_TYPE_SPAWNER) {
			spreaderDepth[pSpawnTileID] = 1;
		}

		mRegionInstances.add(lNewRegion);

		return lNewRegion;

	}

	private void createMapRegions() {

		for (int i = 0; i < NUM_CITIES; i++) {

			int lNewCityTileIndex = -1;
			boolean lFoundLocation = false;
			for (int j = 0; j < 10; j++) {
				lNewCityTileIndex = RandomNumbers.random(0, width * height - 1);
				if (regions[lNewCityTileIndex] == 0x0) {
					lFoundLocation = true;
					continue; // cannot build here, its taken

				}

			}

			if (!lFoundLocation)
				continue;

			createNewRegion(TILE_TYPE_CITY, lNewCityTileIndex);

			if (RandomNumbers.getRandomChance(50)) { // MINE
				for (int j = 0; j < 2; j++) {
					if (RandomNumbers.getRandomChance(50)) {
						continue;

					}

					final int tileIndex = getRandomWithinRange(lNewCityTileIndex, 2);
					if (tileIndex == -1)
						continue;
					if (regions[tileIndex] != 0x0) {
						continue; // cannot build here, its taken

					}

					createNewRegion(TILE_TYPE_MINE, tileIndex);

				}

			} else { // FARM
				for (int j = 0; j < 3; j++) {
					if (RandomNumbers.getRandomChance(50)) {
						continue;

					}

					final int tileIndex = getRandomWithinRange(lNewCityTileIndex, 2);
					if (tileIndex == -1)
						continue;
					if (regions[tileIndex] != 0x0) {
						continue;

					}

					createNewRegion(TILE_TYPE_FARM, tileIndex);

				}

			}

		}

	}

	private void randomiseHeights() {
		final float scale = 1f;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;

				final int maxHeight = 16;

				int tileHeight = (int) (mNoise.noise2(x * scale, y * scale) * maxHeight);
				ground[ti] = (int) (tileHeight << 3);

			}

		}

	}

	public WorldRegion getRegionByTileindex(int pTileIndex) {
		if (pTileIndex < 0 || pTileIndex > width * height - 1)
			return null;
		if (regions[pTileIndex] == 0x0)
			return null;

		return getRegionByUID(regions[pTileIndex]);

	}

	public WorldRegion getRegionByUID(int pRegionIndex) {
		final int lSize = mRegionInstances.size();
		for (int i = 0; i < lSize; i++) {
			if (mRegionInstances.get(i).uid() == pRegionIndex) {
				return mRegionInstances.get(i);

			}

		}

		return null;

	}

	public void deleteRegionByUID(int pRegionIndex) {
		WorldRegion lRegion = getRegionByUID(pRegionIndex);

		if (lRegion != null) {
			List<Integer> lTileIndices = lRegion.tiles;
			final int lNumTiles = lTileIndices.size();
			for (int i = 0; i < lNumTiles; i++) {
				int lTileIndex = lTileIndices.get(i);

				regionHealth[lTileIndex] = 0;
				spreaderDepth[lTileIndex] = 0;
				regions[lTileIndex] = 0x0;

			}

			lRegion.tiles.clear();
			mRegionInstances.remove(lRegion);
		}

	}

	// Each tile adds a line to the top or right
	public void generateContours() {
		boolean[] visited = new boolean[width * height];

		final float lTileSize = World.TILE_SIZE;

		float xOff = -width * lTileSize * 0.5f;
		float yOff = -height * lTileSize * 0.5f;

		// top edge contours
		for (int i = 0; i < width * height; i++) {
			if (visited[i])
				continue;

			visited[i] = true;
			int lCurrentHeight = WorldTile.getTileHeight(ground[i]);

			int topIndex = getTopTileIndex(i);

			float lPoint1X = 0;
			float lPoint1Y = 0;
			float lPoint2X = 0;
			float lPoint2Y = 0;

			{ // top lines
				if (topIndex == -1 || lCurrentHeight != WorldTile.getTileHeight(ground[topIndex])) { // on top left edge
					// Start in top left
					lPoint1X = (i % width) * lTileSize;
					lPoint1Y = (i / width) * lTileSize;

					lPoint2X = (i % width) * lTileSize + lTileSize;
					lPoint2Y = (i / width) * lTileSize;

					// see how far to the left we can go
					for (int j = i + 1; j < width; j++) {
						if (visited[j])
							break;

						if (lCurrentHeight != WorldTile.getTileHeight(ground[j]))
							break;

						visited[j] = true;
						lPoint2X = (j % width) * lTileSize + lTileSize;

					}

					// Finish the top line
					WorldContourLine lTopLine = new WorldContourLine();
					lTopLine.start.x = xOff + lPoint1X;
					lTopLine.start.y = yOff + lPoint1Y;
					lTopLine.end.x = xOff + lPoint2X;
					lTopLine.end.y = yOff + lPoint2Y;
					lTopLine.height = lCurrentHeight;

					mWorldContours.add(lTopLine);

				}

			}

		}

		Arrays.fill(visited, false);

		// left edge contours
		for (int i = 0; i < width * height; i++) {
			if (visited[i])
				continue;

			visited[i] = true;
			int lCurrentHeight = WorldTile.getTileHeight(ground[i]);

			int leftIndex = getLeftTileIndex(i);

			float lPoint1X = 0;
			float lPoint1Y = 0;
			float lPoint2X = 0;
			float lPoint2Y = 0;

			{ // left lines
				if ((leftIndex == -1 || lCurrentHeight != WorldTile.getTileHeight(ground[leftIndex]))) {
					// Start in top left
					lPoint1X = (i % width) * lTileSize;
					lPoint1Y = (i / width) * lTileSize;

					lPoint2X = (i % width) * lTileSize;
					lPoint2Y = (i / width) * lTileSize + lTileSize;

					// see how far down we can go
					for (int j = i + width; j < height * width; j += width) {
						if (visited[j])
							break;

						int lDownHeight = WorldTile.getTileHeight(ground[j]);

						int lDownLeftIndex = getLeftTileIndex(j);
						int lDownLeftHeight = lDownHeight;
						if (lDownLeftIndex != -1) {
							lDownLeftHeight = WorldTile.getTileHeight(ground[lDownLeftIndex]);
							;

						}

						if (lCurrentHeight != lDownHeight)
							break;

						if (lDownHeight == lDownLeftHeight)
							break;

						visited[j] = true;
						lPoint2Y = (j / width) * lTileSize + lTileSize;

					}

					// Finish the top line
					WorldContourLine lLeftLine = new WorldContourLine();
					lLeftLine.start.x = xOff + lPoint1X;
					lLeftLine.start.y = yOff + lPoint1Y;
					lLeftLine.end.x = xOff + lPoint2X;
					lLeftLine.end.y = yOff + lPoint2Y;
					lLeftLine.height = lCurrentHeight;

					mWorldContours.add(lLeftLine);

				}
			}

		}

	}

	public float getWorldPositionX(int pTileIndex) {
		float xOff = -World.WIDTH * 0.5f * World.TILE_SIZE;
		float xPos = pTileIndex % World.WIDTH * World.TILE_SIZE;

		return xOff + xPos;
	}

	public float getWorldPositionY(int pTileIndex) {
		float yOff = -World.HEIGHT * 0.5f * World.TILE_SIZE;
		float yPos = pTileIndex / World.WIDTH * World.TILE_SIZE;

		return yOff + yPos;
	}

	public int getTileFromWorldPosition(float pXPos, float pYPos) {
		float xOff = -World.WIDTH * 0.5f * World.TILE_SIZE;
		float yOff = -World.HEIGHT * 0.5f * World.TILE_SIZE;

		int lGridX = (int) (-xOff + pXPos) / World.TILE_SIZE;
		int lGridY = (int) (-yOff + pYPos) / World.TILE_SIZE;

		if (lGridX < 0)
			lGridX = 0;
		if (lGridX >= World.WIDTH)
			lGridX = World.WIDTH - 1;

		if (lGridY < 0)
			lGridY = 0;
		if (lGridY > World.HEIGHT)
			lGridY = World.HEIGHT - 1;

		return lGridY * World.WIDTH + lGridX;
	}

}
