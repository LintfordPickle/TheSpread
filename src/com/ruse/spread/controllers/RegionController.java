package com.ruse.spread.controllers;

import java.util.ArrayList;
import java.util.List;

import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldRegion;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class RegionController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "RegionController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;

	private List<WorldRegion> mRegionUpdateList = new ArrayList<>();
	private List<Integer> mTileUpdateList = new ArrayList<>();

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

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, mGroupID);

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		World lWorld = mWorldController.gameWorld().world();

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

			mTileUpdateList.clear();

			// Check the state of the individual tiles, do merging, spliting etc.
			final int lRegionTileCount = lRegion.tiles().size();
			for (int j = 0; j < lRegionTileCount; j++) {
				mTileUpdateList.add(lRegion.tiles().get(j));

			}

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
