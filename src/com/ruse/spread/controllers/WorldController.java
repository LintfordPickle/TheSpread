package com.ruse.spread.controllers;

import com.ruse.spread.data.GameWorld;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class WorldController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "WorldController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameWorld mGameWorld;

	private float randomTimer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialised() {
		return mGameWorld != null;
	}

	public GameWorld gameWorld() {
		return mGameWorld;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public WorldController(ControllerManager pControllerManager, GameWorld pGameWorld, int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mGameWorld = pGameWorld;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		randomTimer += pCore.time().elapseGameTimeMilli();
		if (randomTimer > 10000) {
			// mGameWorld.world().generateNewWorld();
			randomTimer = 0;
		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
