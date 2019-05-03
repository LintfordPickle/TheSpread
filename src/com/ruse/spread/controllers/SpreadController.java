package com.ruse.spread.controllers;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.GameConstants;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldRegion;
import com.ruse.spread.data.world.nodes.WorldNode;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class SpreadController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "SpreadController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private boolean mEnableSpread;

	private WorldController mWorldController;
	private NodeController mNodeController;

	private float stepTimer;
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

	public SpreadController(ControllerManager pControllerManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mEnableSpread = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, entityGroupID());
		mNodeController = (NodeController) lControllerManager.getControllerByNameRequired(NodeController.CONTROLLER_NAME, entityGroupID());

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

		stepTimer += pCore.time().elapseGameTimeMilli();

		if (mEnableSpread && (step || stepTimer > GameConstants.TICK_TIMER_MS)) {
			updateSpreadTile();

			stepTimer = 0;

		}

		step = false;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/* Finds the next viable/cheapest path for a region to spread to. */
	private void updateSpreadTile() {
		World lWorld = mWorldController.gameWorld().world();

		int[] lRegions = lWorld.regionIDs;
		int[] groundHeights = lWorld.groundHeight;
		int[] spreadPopulation = lWorld.spreadPopulation;

		final int lTileCount = lWorld.width * lWorld.height;
		for (int i = 0; i < lTileCount; i++) {

			int lCurIndex = i;

			int lCurrentHeight = groundHeights[i];
			int lLocalPopulation = spreadPopulation[i];

			// Maintain at minimum this amount of pop per tile
			final int lMinimumPopulationMaintain = 25;
			if (lLocalPopulation <= lMinimumPopulationMaintain)
				continue;

			{
				int leftIndex = lWorld.getLeftTileIndex(lCurIndex);
				if (leftIndex != -1) {

					// Check for nodes to attack
					WorldNode lNode = mNodeController.getNodeAt(leftIndex);
					if (lNode != null && lNode.mTeamID == WorldNode.TEAM_ID_PLAYER) {
						nodeConflict(lWorld, lNode, leftIndex);

					}

					// Check for regions
					else if (lRegions[leftIndex] != 0) {
						WorldRegion lLeftRegion = lWorld.getRegionByTileindex(leftIndex);

						if (lLeftRegion != null) {
							lLeftRegion.health = 0;
							lRegions[leftIndex] = 0;

						}

					}

					// Spread
					else {
						if (spreadPopulation[leftIndex] + GameConstants.POP_TO_MOVE <= lLocalPopulation) {

							int lPopToMove = 0;
							int lLeftHeight = groundHeights[leftIndex];
							if (lLeftHeight == lCurrentHeight) { // same height
								if (lLocalPopulation >= spreadPopulation[leftIndex] + GameConstants.SATURATION_LEVEL_GROUND)
									lPopToMove = GameConstants.MOVE_SAME_LEVEL;

							} else if (lLeftHeight < lCurrentHeight) { // downhill

								if (lLocalPopulation >= spreadPopulation[leftIndex] + GameConstants.SATURATION_DOWNHILL)
									lPopToMove = GameConstants.MOVE_DOWNHILL;

							} else if (lLeftHeight > lCurrentHeight) { // Uphill
								if (lLocalPopulation >= spreadPopulation[leftIndex] + GameConstants.SATURATION_UPHILL)
									lPopToMove = GameConstants.MOVE_UPHILL;

							}

							setPopulation(leftIndex, lPopToMove);
							spreadPopulation[lCurIndex] -= lPopToMove;

						}

					}

				}

				int rightIndex = lWorld.getRightTileIndex(lCurIndex);
				if (rightIndex != -1) {

					// Check for nodes to attack
					WorldNode lNode = mNodeController.getNodeAt(rightIndex);
					if (lNode != null && lNode.mTeamID == WorldNode.TEAM_ID_PLAYER) {
						nodeConflict(lWorld, lNode, rightIndex);

					}

					// Check for regions
					else if (lRegions[rightIndex] != 0) {
						WorldRegion lRightRegion = lWorld.getRegionByTileindex(rightIndex);

						if (lRightRegion != null) {
							lRightRegion.health = 0;
							lRegions[rightIndex] = 0;

						}

					}

					// Spread
					else {
						if (spreadPopulation[rightIndex] + GameConstants.POP_TO_MOVE <= lLocalPopulation) {
							int lPopToMove = 0;
							int lRightHeight = groundHeights[rightIndex];
							if (lRightHeight == lCurrentHeight) { // same height
								if (lLocalPopulation >= spreadPopulation[rightIndex] + GameConstants.SATURATION_LEVEL_GROUND)
									lPopToMove = GameConstants.MOVE_SAME_LEVEL;

							} else if (lRightHeight < lCurrentHeight) { // downhill

								if (lLocalPopulation >= spreadPopulation[rightIndex] + GameConstants.SATURATION_DOWNHILL)
									lPopToMove = GameConstants.MOVE_DOWNHILL;

							} else if (lRightHeight > lCurrentHeight) { // Uphill
								if (lLocalPopulation >= spreadPopulation[rightIndex] + GameConstants.SATURATION_UPHILL)
									lPopToMove = GameConstants.MOVE_UPHILL;

							}

							setPopulation(rightIndex, lPopToMove);
							spreadPopulation[lCurIndex] -= lPopToMove;

						}

					}

				}

				int topIndex = lWorld.getTopTileIndex(lCurIndex);
				if (topIndex != -1) {

					// Check for nodes to attack
					WorldNode lNode = mNodeController.getNodeAt(topIndex);
					if (lNode != null && lNode.mTeamID == WorldNode.TEAM_ID_PLAYER) {
						nodeConflict(lWorld, lNode, topIndex);

					}

					// Check for regions
					else if (lRegions[topIndex] != 0) {
						WorldRegion lTopRegion = lWorld.getRegionByTileindex(topIndex);

						if (lTopRegion != null) {
							lTopRegion.health = 0;
							lRegions[topIndex] = 0;

						}

					}

					// Spread
					else {
						if (spreadPopulation[topIndex] + GameConstants.POP_TO_MOVE <= lLocalPopulation) {
							int lPopToMove = 0;
							int lTopHeight = groundHeights[topIndex];
							if (lTopHeight == lCurrentHeight) { // same height
								if (lLocalPopulation >= spreadPopulation[topIndex] + GameConstants.SATURATION_LEVEL_GROUND)
									lPopToMove = GameConstants.MOVE_SAME_LEVEL;

							} else if (lTopHeight < lCurrentHeight) { // downhill

								if (lLocalPopulation >= spreadPopulation[topIndex] + GameConstants.SATURATION_DOWNHILL)
									lPopToMove = GameConstants.MOVE_DOWNHILL;

							} else if (lTopHeight > lCurrentHeight) { // Uphill
								if (lLocalPopulation >= spreadPopulation[topIndex] + GameConstants.SATURATION_UPHILL)
									lPopToMove = GameConstants.MOVE_UPHILL;

							}

							setPopulation(topIndex, lPopToMove);
							spreadPopulation[lCurIndex] -= lPopToMove;

						}

					}

				}

				int bottomIndex = lWorld.getBottomTileIndex(lCurIndex);
				if (bottomIndex != -1) {

					// Check for nodes to attack
					WorldNode lNode = mNodeController.getNodeAt(bottomIndex);
					if (lNode != null && lNode.mTeamID == WorldNode.TEAM_ID_PLAYER) {
						nodeConflict(lWorld, lNode, bottomIndex);

					}

					// Check for regions
					else if (lRegions[bottomIndex] != 0) {
						WorldRegion lBottomRegion = lWorld.getRegionByTileindex(bottomIndex);

						if (lBottomRegion != null) {
							lBottomRegion.health = 0;
							lRegions[bottomIndex] = 0;

						}

					}

					// Spread
					else {
						if (spreadPopulation[bottomIndex] + GameConstants.POP_TO_MOVE <= lLocalPopulation) {
							int lPopToMove = 0;
							int lBottomHeight = groundHeights[bottomIndex];
							if (lBottomHeight == lCurrentHeight) { // same height
								if (lLocalPopulation >= spreadPopulation[bottomIndex] + GameConstants.SATURATION_LEVEL_GROUND)
									lPopToMove = GameConstants.MOVE_SAME_LEVEL;

							} else if (lBottomHeight < lCurrentHeight) { // downhill

								if (lLocalPopulation >= spreadPopulation[bottomIndex] + GameConstants.SATURATION_DOWNHILL)
									lPopToMove = GameConstants.MOVE_DOWNHILL;

							} else if (lBottomHeight > lCurrentHeight) { // Uphill
								if (lLocalPopulation >= spreadPopulation[bottomIndex] + GameConstants.SATURATION_UPHILL)
									lPopToMove = GameConstants.MOVE_UPHILL;

							}

							setPopulation(bottomIndex, lPopToMove);
							spreadPopulation[lCurIndex] -= lPopToMove;

						}

					}

				}

			}

		}

	}

	private void setPopulation(int pTileIndex, int pNewPop) {
		if (pTileIndex < 0 || pTileIndex >= GameConstants.WIDTH * GameConstants.HEIGHT)
			return;

		// TODO: This function should exist within the World class
		mWorldController.gameWorld().world().spreadPopulation[pTileIndex] += pNewPop;

	}

	private void nodeConflict(World pWorld, WorldNode pNode, int pTile) {
		if (!pNode.isConstructed)
			pNode.takeDamage(200);

		pNode.takeDamage(40);

		if (pWorld.spreadPopulation[pTile] >= 0) {
			pWorld.spreadPopulation[pTile] -= 50f;

		}

	}

}
