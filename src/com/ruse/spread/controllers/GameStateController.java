package com.ruse.spread.controllers;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.data.GameState;
import com.ruse.spread.data.world.World;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.camera.CameraController;
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
	private CameraController mCameraController;
	private CameraBoundController mCameraBoundController;

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
		mCameraController = (CameraController) lControllerManager.getControllerByNameRequired(CameraController.CONTROLLER_NAME, LintfordCore.CORE_ID);
		mCameraBoundController = (CameraBoundController) lControllerManager.getControllerByNameRequired(CameraBoundController.CONTROLLER_NAME, mGroupID);

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_R)) {
			mIsGameEnded = true;

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
		if (!mWorldController.gameWorld().world().checkStillSpreader()) {
			mIsGameEnded = true;
		}

		mGameState.population = mWorldController.gameWorld().HQNode().populationStore;
		mGameState.metals = mWorldController.gameWorld().HQNode().metalStore;
		mGameState.food = mWorldController.gameWorld().HQNode().foodStore;

		// Check for game ended flag from other sources (HQ destroyed , below)
		if (mIsGameEnded) {
			startNewGame();

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame() {
		mWorldController.gameWorld().generateNewWorld();
		mGameState.startNewGame();

		final World lWorld = mWorldController.gameWorld().world();
		final int lHQNodeTileIndex = mWorldController.gameWorld().HQNode().tileIndex;

		float StartX = lWorld.getWorldPositionX(lHQNodeTileIndex);
		float StartY = lWorld.getWorldPositionY(lHQNodeTileIndex);

		mCameraBoundController.setup(StartX, StartY, -lWorld.width / 2f * World.TILE_SIZE, -lWorld.height / 2f * World.TILE_SIZE, lWorld.width * World.TILE_SIZE, lWorld.height * World.TILE_SIZE);

		mIsGameEnded = false;
	}

	public void HQDestroyed() {
		// TODO: LOSE CONDITION MET

		mIsGameEnded = true;

	}

}
