package com.ruse.spread.controllers;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.GameConstants;
import com.ruse.spread.data.GameState;
import com.ruse.spread.data.world.World;

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
	private CameraBoundController mCameraBoundController;

	private boolean mGameWon;
	private boolean mIsGameEnded;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isGameEnded() {
		return mIsGameEnded;
	}

	public boolean isGameWon() {
		return mGameWon;
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
		mCameraBoundController = (CameraBoundController) lControllerManager.getControllerByNameRequired(CameraBoundController.CONTROLLER_NAME, mGroupID);

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_R)) {
//			mNewSeed = false;
//			mIsGameEnded = true;
			startNewGame(false);

		}
		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_N)) {
//			mNewSeed = true;
//			mIsGameEnded = true;
			startNewGame(true);

		}

		return super.handleInput(pCore);
	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mGameState.difficultyLevel < mGameState.difficultyLevelMax) {
			mGameState.difficultyTimer += pCore.time().elapseGameTimeMilli();

			if (mGameState.difficultyTimer > mGameState.difficultyTime) {
				mGameState.difficultyLevel++;
				System.out.println("difficulty increased to " + mGameState.difficultyLevel);

				mGameState.difficultyTimer = 0;

			}

		}

		// Check for game won
		if (mWorldController.gameWorld().numSpreadSpawns() <= 0) {
			mGameWon = true;
			mIsGameEnded = true;
		}

		mGameState.population = mWorldController.gameWorld().HQNode().populationStore;
		mGameState.metals = mWorldController.gameWorld().HQNode().metalStore;
		mGameState.food = mWorldController.gameWorld().HQNode().foodStore;

		// Check for game ended flag from other sources (HQ destroyed , below)
		if (mIsGameEnded) {
			if (mGameWon) {
				// Game won

			} else {
				// Game lost

			}

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame(boolean pNewSeed) {
		mWorldController.gameWorld().generateNewWorld(pNewSeed);
		mGameState.startNewGame();

		final World lWorld = mWorldController.gameWorld().world();
		final int lHQNodeTileIndex = mWorldController.gameWorld().HQNode().tileIndex;

		float StartX = lWorld.getWorldPositionX(lHQNodeTileIndex);
		float StartY = lWorld.getWorldPositionY(lHQNodeTileIndex);

		mCameraBoundController.setup(StartX, StartY, -lWorld.width / 2f * GameConstants.TILE_SIZE, -lWorld.height / 2f * GameConstants.TILE_SIZE, lWorld.width * GameConstants.TILE_SIZE, lWorld.height * GameConstants.TILE_SIZE);

		mIsGameEnded = false;
	}

	public void HQDestroyed() {
		mGameWon = false;
		mIsGameEnded = true;

	}

}
