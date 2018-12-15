package com.ruse.spread.data.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.ruse.spread.GameConstants;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.noise.ImprovedNoise;
import net.lintford.library.data.BaseData;

public class World extends BaseData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -4942760447159744932L;

	public static final int REGION_TYPE_CITY = 0b00000010;
	public static final int REGION_TYPE_MINE = 0b00000100;
	public static final int REGION_TYPE_FARM = 0b00001000;

	public static final int REGION_ID_NOTHING = 0b00000001;
	public static final int REGION_ID_SPREAD = 0b00000010;
	public static final int REGION_ID_NEUTRAL = 0b00000100;

	private static int region_index_counter;

	public static final int INVALID_INDEX = -1;
	public static final int INVALID_REGION = 0xffffffff;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public final int width;
	public final int height;
	public int[] groundTileTypes; // stores type and height
	public int[] groundHeight; // stores the ground height level for each tile
	public int[] regionIDs; // stores region ID
	public int[] timer; // variants/timers on tile animations
	public int[] variants; // variants/timers on tile animations
	public int[] spreadPopulation; // The number

	private long mLastSeed = -1;
	private ImprovedNoise mNoise;

	public List<WorldRegion> mRegionInstances = new ArrayList<>();
	public List<WorldContourLine> mWorldContours = new ArrayList<>();

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
		for (int i = 0; i < width * height; i++) {
			if (spreadPopulation[i] > 0)
				return true;

		}

		return false;

	}

	public int getSpreaderPopulation() {
		int lNumSpreader = 0;

		for (int i = 0; i < width * height; i++) {
			lNumSpreader += spreadPopulation[i];

		}

		return lNumSpreader;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public World() {
		width = GameConstants.WIDTH;
		height = GameConstants.HEIGHT;

		groundTileTypes = new int[width * height];
		groundHeight = new int[width * height];
		regionIDs = new int[width * height];
		spreadPopulation = new int[width * height];
		variants = new int[width * height];
		timer = new int[width * height];
		
		// Set a default seed
		mLastSeed = 253556475200846L;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void generateNewWorld(boolean pNewSeed) {
		Arrays.fill(groundTileTypes, 0x0);
		Arrays.fill(groundHeight, 0x0);
		Arrays.fill(regionIDs, 0x0);
		Arrays.fill(timer, 0x0);
		Arrays.fill(variants, 0x0);
		Arrays.fill(spreadPopulation, 0); // number of spread here

		region_index_counter = 1;

		mRegionInstances.clear();
		mWorldContours.clear();

		if (mLastSeed == -1 || pNewSeed) {
			mLastSeed = System.nanoTime();

		}

		mNoise = new ImprovedNoise(mLastSeed);
		RandomNumbers.RANDOM = new Random(mLastSeed);

		randomiseHeights();

		createMapRegions();

		generateContours();

		createStartingArea();

	}

	public int getHighestTile() {
		int lHighestPoint = 0;
		int lHighestIndex = 0;
		for (int i = 0; i < width * height; i++) {
			int lHeight = groundHeight[i];
			if (lHeight > lHighestPoint) {
				lHighestPoint = lHeight;
				lHighestIndex = i;
			}

		}

		return lHighestIndex;

	}

	private void createStartingArea() {
		int lHQStartingArea = getHighestTile();

		int lFarmTileIndex = getRandomWithinRange(lHQStartingArea, 4);
		for (int i = 0; i < 5; i++) {
			if (lFarmTileIndex != -1)
				break;
		}

		if (lFarmTileIndex != -1)
			createNewRegion(World.REGION_TYPE_FARM, lFarmTileIndex);

		int lMineTileIndex = getRandomWithinRange(lHQStartingArea, 4);
		for (int i = 0; i < 5; i++) {
			if (lMineTileIndex != -1 && lMineTileIndex != lFarmTileIndex)
				break;
		}

		if (lMineTileIndex != -1)
			createNewRegion(World.REGION_TYPE_MINE, lMineTileIndex);

	}

	private int getRandomWithinRange(int pTileCoord, int pRange) {
		int lTileX = pTileCoord % width;
		int lTileY = pTileCoord / width;

		int lHalfRange = pRange;

		final int lNumTries = 5;
		for (int i = 0; i < lNumTries; i++) {

			int lNewX = lTileX + RandomNumbers.random(0, pRange * 2) - lHalfRange;
			if (lNewX < 0)
				lNewX = 0;
			if (lNewX >= width)
				lNewX = width - 1;

			int lNewY = lTileY + RandomNumbers.random(0, pRange * 2) - lHalfRange;
			if (lNewY < 0)
				lNewY = 0;
			if (lNewY >= height)
				lNewY = height - 1;

			int lSuggestedIndex = lNewY * width + lNewX;

			if (lSuggestedIndex < 0 || lSuggestedIndex >= width * height)
				continue;

			// Check regions collisions
			if (regionIDs[lSuggestedIndex] != 0x0)
				continue;

			return lSuggestedIndex;

		}

		return -1;
	}

	public List<Integer> createSpawnerPositions(int pNumberSpawns) {
		List<Integer> lReturn = new ArrayList<>();
		List<Integer> lOptions = new ArrayList<>();

		for (int i = 0; i < pNumberSpawns; i++) {
			lOptions.clear();
			int lLowestPoint = 99;
			int lLowestIndex = -1;

			// Top row
			for (int j = 0; j < width; j++) {
				int lHeight = groundHeight[j];
				if (lHeight < lLowestPoint && !lReturn.contains(Integer.valueOf(j))) {
					lLowestPoint = lHeight;
					lOptions.add(j);
				}
			}

			// Bottom row
			for (int j = 0; j < width; j++) {
				int ui = (width * height) - width + j;
				int lHeight = groundHeight[ui];
				if (lHeight < lLowestPoint && !lReturn.contains(Integer.valueOf(ui))) {
					lLowestPoint = lHeight;
					lOptions.add(ui);
				}
			}

			if (lOptions.size() > 0)
				lLowestIndex = lOptions.get(RandomNumbers.random(0, lOptions.size()));

			if (lLowestIndex != -1)
				lReturn.add(lLowestIndex);
		}

		return lReturn;

	}

	private WorldRegion createNewRegion(int pType, int pSpawnTileID) {
		WorldRegion lNewRegion = WorldRegion.createNewRegion(region_index_counter++, pType);

		lNewRegion.tiles().add(pSpawnTileID);

		regionIDs[pSpawnTileID] = lNewRegion.uid();

		mRegionInstances.add(lNewRegion);

		return lNewRegion;

	}

	private void createMapRegions() {

		for (int i = 0; i < GameConstants.NUM_CITIES; i++) {

			int lNewCityTileIndex = -1;
			boolean lFoundLocation = false;
			for (int j = 0; j < 10; j++) {
				lNewCityTileIndex = RandomNumbers.random(0, width * height - 1);
				if (regionIDs[lNewCityTileIndex] == 0x0) {
					lFoundLocation = true;
					continue; // cannot build here, its taken

				}

			}

			if (!lFoundLocation)
				continue;

			createNewRegion(REGION_TYPE_CITY, lNewCityTileIndex);

			if (RandomNumbers.getRandomChance(50)) { // MINE
				for (int j = 0; j < 2; j++) {
					if (RandomNumbers.getRandomChance(50)) {
						continue;

					}

					final int tileIndex = getRandomWithinRange(lNewCityTileIndex, 2);
					if (tileIndex == -1)
						continue;
					if (regionIDs[tileIndex] != 0x0) {
						continue; // cannot build here, its taken

					}

					createNewRegion(REGION_TYPE_MINE, tileIndex);

				}

			} else { // FARM
				for (int j = 0; j < 3; j++) {
					if (RandomNumbers.getRandomChance(50)) {
						continue;

					}

					final int tileIndex = getRandomWithinRange(lNewCityTileIndex, 2);
					if (tileIndex == -1)
						continue;
					if (regionIDs[tileIndex] != 0x0) {
						continue;

					}

					createNewRegion(REGION_TYPE_FARM, tileIndex);

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
				groundHeight[ti] = tileHeight;

			}

		}

	}

	public WorldRegion getRegionByTileindex(int pTileIndex) {
		if (pTileIndex < 0 || pTileIndex > width * height - 1)
			return null;
		if (regionIDs[pTileIndex] == 0x0)
			return null;

		return getRegionByUID(regionIDs[pTileIndex]);

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

				regionIDs[lTileIndex] = 0x0;

			}

			lRegion.tiles.clear();
			mRegionInstances.remove(lRegion);
		}

	}

	public void generateContours() {
		boolean[] visited = new boolean[width * height];

		final float lTileSize = GameConstants.TILE_SIZE;

		float xOff = -width * lTileSize * 0.5f;
		float yOff = -height * lTileSize * 0.5f;

		// top edge contours
		for (int i = 0; i < width * height; i++) {
			if (visited[i])
				continue;

			visited[i] = true;
			int lCurrentHeight = groundHeight[i];

			int topIndex = getTopTileIndex(i);

			float lPoint1X = 0;
			float lPoint1Y = 0;
			float lPoint2X = 0;
			float lPoint2Y = 0;

			{ // top lines
				if (topIndex == -1 || lCurrentHeight != groundHeight[topIndex]) { // on top left edge
					// Start in top left
					lPoint1X = (i % width) * lTileSize;
					lPoint1Y = (i / width) * lTileSize;

					lPoint2X = (i % width) * lTileSize + lTileSize;
					lPoint2Y = (i / width) * lTileSize;

					// see how far to the left we can go
					for (int j = i + 1; j < width; j++) {
						if (visited[j])
							break;

						if (lCurrentHeight != groundHeight[j])
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
			int lCurrentHeight = groundHeight[i];

			int leftIndex = getLeftTileIndex(i);

			float lPoint1X = 0;
			float lPoint1Y = 0;
			float lPoint2X = 0;
			float lPoint2Y = 0;

			{ // left lines
				if ((leftIndex == -1 || lCurrentHeight != groundHeight[leftIndex])) {
					// Start in top left
					lPoint1X = (i % width) * lTileSize;
					lPoint1Y = (i / width) * lTileSize;

					lPoint2X = (i % width) * lTileSize;
					lPoint2Y = (i / width) * lTileSize + lTileSize;

					// see how far down we can go
					for (int j = i + width; j < height * width; j += width) {
						if (visited[j])
							break;

						int lDownHeight = groundHeight[j];

						int lDownLeftIndex = getLeftTileIndex(j);
						int lDownLeftHeight = lDownHeight;
						if (lDownLeftIndex != -1) {
							lDownLeftHeight = groundHeight[lDownLeftIndex];

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
		float xOff = -width * 0.5f * GameConstants.TILE_SIZE;
		float xPos = pTileIndex % width * GameConstants.TILE_SIZE;

		return xOff + xPos;
	}

	public float getWorldPositionY(int pTileIndex) {
		float yOff = -height * 0.5f * GameConstants.TILE_SIZE;
		float yPos = pTileIndex / width * GameConstants.TILE_SIZE;

		return yOff + yPos;
	}

	public int getTileFromWorldPosition(float pXPos, float pYPos) {
		float xOff = -width * 0.5f * GameConstants.TILE_SIZE;
		float yOff = -height * 0.5f * GameConstants.TILE_SIZE;

		int lGridX = (int) (-xOff + pXPos) / GameConstants.TILE_SIZE;
		int lGridY = (int) (-yOff + pYPos) / GameConstants.TILE_SIZE;

		if (lGridX < 0)
			lGridX = 0;
		if (lGridX >= width)
			lGridX = width - 1;

		if (lGridY < 0)
			lGridY = 0;
		if (lGridY > height)
			lGridY = height - 1;

		return lGridY * width + lGridX;
	}

}
