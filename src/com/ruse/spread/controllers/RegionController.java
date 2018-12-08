package com.ruse.spread.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.data.regions.SpreadRegion;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldNode;
import com.ruse.spread.data.world.WorldRegion;
import com.ruse.spread.data.world.WorldTile;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;

public class RegionController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "RegionController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private boolean mEnableSpread;

	private WorldController mWorldController;
	private NodeController mNodeController;
	private GameStateController mGameStateController;

	private int[] visited;
	List<Integer> lowestSet = new ArrayList<>();
	List<Integer> foundSet = new ArrayList<>();
	List<Integer> validSet = new ArrayList<>();

	private List<WorldRegion> mRegionUpdateList = new ArrayList<>();
	private List<Integer> mTileUpdateList = new ArrayList<>();

	private boolean step;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialised() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public RegionController(ControllerManager pControllerManager, int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mEnableSpread = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, mGroupID);
		mNodeController = (NodeController) lControllerManager.getControllerByNameRequired(NodeController.CONTROLLER_NAME, mGroupID);
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, mGroupID);

		visited = new int[mWorldController.gameWorld().world().ground.length];

	}

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F5)) {
			step = true;

		}

		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		World lWorld = mWorldController.gameWorld().world();

		int[] tileRegion = lWorld.regions;
		int[] tileHealth = lWorld.regionHealth;
		int[] tileSpread = lWorld.spreaderDepth;

		mRegionUpdateList.clear();

		// update the regions (with a view to deleting them from the list)!
		final int lNumRegions = lWorld.mRegionInstances.size();
		for (int i = 0; i < lNumRegions; i++) {
			mRegionUpdateList.add(lWorld.mRegionInstances.get(i));

		}

		// Update the regions, actual
		for (int i = 0; i < lNumRegions; i++) {
			WorldRegion lRegion = mRegionUpdateList.get(i);

			lRegion.timer += pCore.time().elapseGameTimeMilli();

			switch (lRegion.type()) {
			case World.TILE_TYPE_SPAWNER:
				updateSpreaderRegion(lWorld, lRegion);
				break;

			}

			mTileUpdateList.clear();

			// Check the state of the individual tiles, do merging, spliting etc.
			final int lRegionTileCount = lRegion.tiles().size();
			for (int j = 0; j < lRegionTileCount; j++) {
				mTileUpdateList.add(lRegion.tiles().get(j));

			}

			for (int j = 0; j < lRegionTileCount; j++) {
				int lTileIndex = mTileUpdateList.get(j);

				if (tileHealth[lTileIndex] <= 0) {
					lRegion.tiles().remove(Integer.valueOf(lTileIndex));

					tileRegion[lTileIndex] = 0x0;
					tileHealth[lTileIndex] = 0x0;
					tileSpread[lTileIndex] = 0x0;

				}

			}

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void updateSpreaderRegion(World pWorld, WorldRegion pRegion) {
		SpreadRegion lSpreadRegion = (SpreadRegion) pRegion;

		// make sure in anyone tick we don't update down the same path multiple times
		Arrays.fill(visited, 0x0);

		if(true) return;
		
		if (step || (mEnableSpread && lSpreadRegion.timer > 200)) {
			lSpreadRegion.timer = 0;

			step = false;

			final int lLevel = mGameStateController.gameState().difficultyLevel;
			for (int i = 0; i < lLevel; i++) {
				spreadNextTile(pWorld, pRegion, false);

			}

		}

	}

	/* Finds the next viable/cheapest path for a region to spread to. */
	private void spreadNextTile(World pWorld, WorldRegion pRegion, boolean revist) {
		lowestSet.clear(); // stores lowest point in the region
		foundSet.clear(); // stores the lowest points outside parameter
		validSet.clear(); // list of all valid movement places (where the spread height is higher than the ground)

		int[] lGround = pWorld.ground;
		int[] lRegions = pWorld.regions;
		int[] lHealth = pWorld.regionHealth;
		int[] lSpreadDepth = pWorld.spreaderDepth;

		int lFoundIndex = -1;
		int lFoundHeight = 99;

		int lFoundLowestIndex = -1;
		int lFoundLowestHeight = 99;

		// Update the lowest point of the region
		final int lNumRegionTiles = pRegion.tiles().size();
		for (int i = 0; i < lNumRegionTiles; i++) {
			// The first index in this list is where the region was created
			int lCurIndex = pRegion.tiles().get(i);
			int ourGroundHeight = WorldTile.getTileHeight(lGround[lCurIndex]);
			int ourSpreadHeight = lSpreadDepth[lCurIndex];

			if (ourGroundHeight + ourSpreadHeight < lFoundLowestHeight) {
				lowestSet.clear();
				lFoundLowestHeight = ourGroundHeight + ourSpreadHeight;
				lFoundLowestIndex = lCurIndex;
				lowestSet.add(lCurIndex);

			} else if (ourGroundHeight + ourSpreadHeight == lFoundLowestHeight) {
				// Keep lowest index the same and add this to the set
				lowestSet.add(lCurIndex);

			}

			// The minimum we can consider for now
			int ourTotalHeight = ourGroundHeight + ourSpreadHeight;

			// 4 directions
			{
				int leftIndex = pWorld.getLeftTileIndex(lCurIndex);
				if (leftIndex != -1 && (revist || visited[leftIndex] == 0)) {
					visited[leftIndex] = 1;

					// Check for nodes
					WorldNode lNode = mNodeController.getNodeAt(leftIndex);
					if (lNode != null) {
						// TODO: Check height different, spread cannot attack up
						nodeConflict(pWorld, lNode, lCurIndex);

					} else

					// Check for regions
					if (lRegions[leftIndex] == 0 || lRegions[leftIndex] == pRegion.uid()) { // nothing here yet
						int leftHeight = WorldTile.getTileHeight(lGround[leftIndex]);
						int leftSpreadDepth = lSpreadDepth[leftIndex];
						int leftTotalHeight = leftHeight + leftSpreadDepth;

						if (leftTotalHeight < ourTotalHeight && // lower than current tile
								leftTotalHeight < lFoundHeight) { // not yet the lowest found
							lFoundHeight = leftHeight + leftSpreadDepth;
							lFoundIndex = leftIndex;

							foundSet.clear();
							foundSet.add(leftIndex);
							validSet.add(leftIndex);

						} else if ((leftHeight + leftSpreadDepth) <= ourTotalHeight && // lower or equal to current tile
								(leftHeight + leftSpreadDepth) == lFoundHeight) { // same as current lowest found
							foundSet.add(leftIndex);
							validSet.add(leftIndex);

						}

					} else { // if the tile isn't a spreader, then destroy it
						lHealth[leftIndex] = 0;

					}

				}

				int rightIndex = pWorld.getRightTileIndex(lCurIndex);
				if (rightIndex != -1 && (revist || visited[rightIndex] == 0)) {
					visited[rightIndex] = 1;

					// Check for nodes
					WorldNode lNode = mNodeController.getNodeAt(rightIndex);
					if (lNode != null) {
						// TODO: Check height different, spread cannot attack up
						nodeConflict(pWorld, lNode, lCurIndex);

					} else

					if (lRegions[rightIndex] == 0 || lRegions[rightIndex] == pRegion.uid()) { // nothing here yet
						int rightHeight = WorldTile.getTileHeight(lGround[rightIndex]);
						int rightSpreadDepth = lSpreadDepth[rightIndex];
						int rightTotalHeight = rightHeight + rightSpreadDepth;

						if (rightTotalHeight < ourTotalHeight && // lower than current tile
								rightTotalHeight < lFoundHeight) { // not yet the lowest found
							lFoundHeight = rightHeight + rightSpreadDepth;
							lFoundIndex = rightIndex;

							foundSet.clear();
							foundSet.add(rightIndex);
							validSet.add(rightIndex);

						} else if ((rightHeight + rightSpreadDepth) <= ourTotalHeight && // lower or equal to current tile
								(rightHeight + rightSpreadDepth) == lFoundHeight) { // same as current lowest found
							foundSet.add(rightIndex);
							validSet.add(rightIndex);

						}

					} else { // if the tile isn't a spreader, then destroy it
						lHealth[rightIndex] = 0;

					}

				}

				int topIndex = pWorld.getTopTileIndex(lCurIndex);
				if (topIndex != -1 && (revist || visited[topIndex] == 0)) {
					visited[topIndex] = 1;

					// Check for nodes
					WorldNode lNode = mNodeController.getNodeAt(topIndex);
					if (lNode != null) {
						// TODO: Check height different, spread cannot attack up
						nodeConflict(pWorld, lNode, lCurIndex);

					} else

					if (lRegions[topIndex] == 0 || lRegions[topIndex] == pRegion.uid()) { // nothing here yet
						int topHeight = WorldTile.getTileHeight(lGround[topIndex]);
						int topSpreadDepth = lSpreadDepth[topIndex];
						int topTotalHeight = topHeight + topSpreadDepth;

						if (topTotalHeight < ourTotalHeight && // lower than current tile
								topTotalHeight < lFoundHeight) { // not yet the lowest found
							lFoundHeight = topHeight + topSpreadDepth;
							lFoundIndex = topIndex;

							foundSet.clear();
							foundSet.add(topIndex);
							validSet.add(topIndex);

						} else if ((topHeight + topSpreadDepth) <= ourTotalHeight && // lower or equal to current tile
								(topHeight + topSpreadDepth) == lFoundHeight) { // same as current lowest found
							foundSet.add(topIndex);
							validSet.add(topIndex);

						}

					} else { // if the tile isn't a spreader, then destroy it
						lHealth[topIndex] = 0;
					}

				}

				int bottomIndex = pWorld.getBottomTileIndex(lCurIndex);
				if (bottomIndex != -1 && (revist || visited[bottomIndex] == 0)) {
					visited[bottomIndex] = 1;

					// Check for nodes
					WorldNode lNode = mNodeController.getNodeAt(bottomIndex);
					if (lNode != null) {
						// TODO: Check height different, spread cannot attack up
						nodeConflict(pWorld, lNode, lCurIndex);

					} else

					if (lRegions[bottomIndex] == 0 || lRegions[bottomIndex] == pRegion.uid()) { // nothing here yet
						int bottomHeight = WorldTile.getTileHeight(lGround[bottomIndex]);
						int bottomSpreadDepth = lSpreadDepth[bottomIndex];
						int bottomTotalHeight = bottomHeight + bottomSpreadDepth;

						if (bottomTotalHeight < ourTotalHeight && // lower than current tile
								bottomTotalHeight < lFoundHeight) { // not yet the lowest found
							lFoundHeight = bottomHeight + bottomSpreadDepth;
							lFoundIndex = bottomIndex;

							foundSet.clear();
							foundSet.add(bottomIndex);
							validSet.add(bottomIndex);

						} else if ((bottomHeight + bottomSpreadDepth) <= ourTotalHeight && // lower or equal to current tile
								(bottomHeight + bottomSpreadDepth) == lFoundHeight) { // same as current lowest found
							foundSet.add(bottomIndex);
							validSet.add(bottomIndex);

						}

					} else { // if the tile isn't a spreader, then destroy it
						lHealth[bottomIndex] = 0;
					}

				}

			}

		}

		if (validSet.size() > 0 && RandomNumbers.getRandomChance(10)) {
			int lRandomMovementPoint = validSet.get(RandomNumbers.random(0, validSet.size()));

			visited[lRandomMovementPoint] = 1; // don't update past this tile for the remainder of this tick
			lRegions[lRandomMovementPoint] = pRegion.uid();
			pWorld.regionHealth[lRandomMovementPoint] = 100;
			lSpreadDepth[lRandomMovementPoint] += 1;

			if (!pRegion.tiles().contains(Integer.valueOf(lRandomMovementPoint)))
				pRegion.tiles().add(lRandomMovementPoint);
		}

		// TODO: use heights to determine where to flow
		if (lFoundIndex != -1 && lFoundLowestIndex != -1) {
			if (lFoundHeight < lFoundLowestHeight) {
				// spread into this location
				int lRandomLowestPoint = foundSet.get(RandomNumbers.random(0, foundSet.size()));

				visited[lRandomLowestPoint] = 1; // don't update past this tile for the remainder of this tick
				lRegions[lRandomLowestPoint] = pRegion.uid();
				pWorld.regionHealth[lRandomLowestPoint] = 100;
				lSpreadDepth[lRandomLowestPoint] += 1;

				if (!pRegion.tiles().contains(Integer.valueOf(lRandomLowestPoint)))
					pRegion.tiles().add(lRandomLowestPoint);

				return;

			} else {
				// Add to the lowest point
				int lRandomLowestPoint = lowestSet.get(RandomNumbers.random(0, lowestSet.size()));
				lSpreadDepth[lRandomLowestPoint] += 1;
				pWorld.regionHealth[lRandomLowestPoint] = 100;

				return;

			}
		}

		else if (lFoundIndex != -1) {
			// spread into this location
			int lRandomLowestPoint = lFoundIndex; // foundSet.get(RandomNumbers.random(0, foundSet.size()));

			visited[lRandomLowestPoint] = 1; // don't update past this tile for the remainder of this tick
			lRegions[lRandomLowestPoint] = pRegion.uid();
			pWorld.regionHealth[lRandomLowestPoint] = 100;

			lSpreadDepth[lRandomLowestPoint] += 1;
			if (!pRegion.tiles().contains(Integer.valueOf(lRandomLowestPoint)))
				pRegion.tiles().add(lRandomLowestPoint);

		} else if (lFoundLowestIndex != -1) {
			// Otherwise fill the lowest point in the spread
			int lRandomLowestPoint = lowestSet.get(RandomNumbers.random(0, lowestSet.size()));
			lSpreadDepth[lRandomLowestPoint] += 1;
			pWorld.regionHealth[lRandomLowestPoint] = 100;

		} else {
			// TODO: Need to add a found bumped, to check if we only failed because we tried to
			// spread too quickly

			// Otherwise, increase the depth at the starting node
			if (pRegion.tiles().size() > 0)
				lSpreadDepth[pRegion.tiles().get(0)] += 1;

		}

	}

	private void nodeConflict(World pWorld, WorldNode pNode, int pTile) {
		if (!pNode.isConstructed)
			pNode.takeDamage(200);

		pNode.takeDamage(20);

		if (pWorld.regionHealth[pTile] >= 0) {
			float lSpreadDepthAct = pWorld.spreaderDepth[pTile];
			if (lSpreadDepthAct <= 1)
				lSpreadDepthAct = 1f;

			pWorld.regionHealth[pTile] -= 50f / lSpreadDepthAct;

		}

	}

}
