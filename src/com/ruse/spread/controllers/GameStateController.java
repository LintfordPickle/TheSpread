package com.ruse.spread.controllers;

import com.ruse.spread.data.GameState;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class GameStateController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "GameStateController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameState mGameState;
	private WorldController mWorldController;

	private boolean mIsGameEnded;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isGameEnded() {
		return mIsGameEnded;
	}

	public GameState gameState() {
		return mGameState;
	}

	@Override
	public boolean isInitialised() {
		return mGameState != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameStateController(ControllerManager pControllerManager, GameState pGameState, int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mGameState = pGameState;

	}

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, mGroupID);

	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		// Check for game won
		if (!mWorldController.gameWorld().world().checkStillSpreader()) {
			mIsGameEnded = true;
		}

		mGameState.population = mWorldController.gameWorld().HQNode().populationStore;
		mGameState.metals = mWorldController.gameWorld().HQNode().metalStore;
		mGameState.food = mWorldController.gameWorld().HQNode().foodStore;

		// Check for game ended flag from other sources (HQ destroyed , below)
		if (mIsGameEnded) {
			startNewGame();

			mWorldController.gameWorld().generateNewWorld();

		}

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame() {
		mIsGameEnded = false;
	}

	public void HQDestroyed() {
		// TODO: LOSE CONDITION MET

		mIsGameEnded = true;

	}

}
