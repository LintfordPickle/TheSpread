package com.ruse.spread.screens;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.ruse.spread.controllers.CameraBoundController;
import com.ruse.spread.controllers.GameStateController;
import com.ruse.spread.controllers.MouseController;
import com.ruse.spread.controllers.NodeController;
import com.ruse.spread.controllers.PackageController;
import com.ruse.spread.controllers.ParticleController;
import com.ruse.spread.controllers.RegionController;
import com.ruse.spread.controllers.RoadController;
import com.ruse.spread.controllers.SpreadController;
import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.GameWorld;
import com.ruse.spread.renderers.DebugRenderer;
import com.ruse.spread.renderers.HUDRenderer;
import com.ruse.spread.renderers.MouseRenderer;
import com.ruse.spread.renderers.NodeRenderer;
import com.ruse.spread.renderers.ProjectileRenderer;
import com.ruse.spread.renderers.RegionRenderer;
import com.ruse.spread.renderers.RoadRenderer;
import com.ruse.spread.renderers.SpreadRenderer;
import com.ruse.spread.renderers.WorldRenderer;

import net.lintford.library.controllers.camera.CameraZoomController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.BaseGameScreen;

public class GameScreen extends BaseGameScreen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// Data
	private GameWorld mWorld;

	WorldController mWorldController;
	MouseController mMouseController;
	PackageController mPackageController;
	RegionController mRegionController;
	RoadController mRoadController;
	SpreadController mSpreadController;
	GameStateController mGameStateController;
	NodeController mNodeController;
	ParticleController mProjectileController;

	CameraZoomController mCameraZoomController;
	CameraBoundController mCameraBoundController;

	WorldRenderer mWorldRenderer;
	RegionRenderer mRegionRenderer;
	RoadRenderer mRoadRenderer;
	ProjectileRenderer mProjectileRenderer;
	NodeRenderer mObjectRenderer;
	HUDRenderer mHUDRenderer;
	DebugRenderer mDebugRenderer;
	MouseRenderer mMouseRenderer;
	SpreadRenderer mSpreadRenderer;

	Texture mBackgroundTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public GameWorld world() {
		return mWorld;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		// TODO: Load from file
		mWorld = new GameWorld();

		ControllerManager lControllerManager = pScreenManager.core().controllerManager();

		mWorldController = new WorldController(lControllerManager, mWorld, entityGroupID);
		mMouseController = new MouseController(lControllerManager, entityGroupID);
		mPackageController = new PackageController(lControllerManager, entityGroupID);
		mRegionController = new RegionController(lControllerManager, entityGroupID);
		mRoadController = new RoadController(lControllerManager, entityGroupID);
		mSpreadController = new SpreadController(lControllerManager, entityGroupID);
		mNodeController = new NodeController(lControllerManager, entityGroupID);
		mProjectileController = new ParticleController(lControllerManager, mWorld.projectileManager(), entityGroupID);

		mGameStateController = new GameStateController(lControllerManager, mWorld.gameState(), entityGroupID);

		mWorldRenderer = new WorldRenderer(mRendererManager, entityGroupID);
		mObjectRenderer = new NodeRenderer(mRendererManager, entityGroupID);
		mRoadRenderer = new RoadRenderer(mRendererManager, entityGroupID);
		mRegionRenderer = new RegionRenderer(mRendererManager, entityGroupID);
		mProjectileRenderer = new ProjectileRenderer(mRendererManager, mWorld, entityGroupID);
		mHUDRenderer = new HUDRenderer(mRendererManager, entityGroupID);
		mMouseRenderer = new MouseRenderer(mRendererManager, entityGroupID);
		mDebugRenderer = new DebugRenderer(mRendererManager, entityGroupID);
		mSpreadRenderer = new SpreadRenderer(mRendererManager, entityGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		ControllerManager lControllerManager = mScreenManager.core().controllerManager();

		mCameraZoomController = new CameraZoomController(lControllerManager, (Camera) mScreenManager.core().gameCamera(), entityGroupID);
		mCameraZoomController.setZoomConstraints(0.7f, 1.7f);
		mCameraZoomController.initialise(mScreenManager.core());

		mCameraBoundController = new CameraBoundController(lControllerManager, (Camera) mScreenManager.core().gameCamera(), null, entityGroupID);
		mCameraBoundController.initialise(mScreenManager.core());

		mWorldController.initialise(mScreenManager.core());
		mMouseController.initialise(mScreenManager.core());
		mPackageController.initialise(mScreenManager.core());
		mRegionController.initialise(mScreenManager.core());
		mRoadController.initialise(mScreenManager.core());
		mSpreadController.initialise(mScreenManager.core());
		mNodeController.initialise(mScreenManager.core());
		mProjectileController.initialise(mScreenManager.core());

		mGameStateController.initialise(mScreenManager.core());

		mWorldRenderer.initialise(mScreenManager.core());
		mObjectRenderer.initialise(mScreenManager.core());
		mRoadRenderer.initialise(mScreenManager.core());
		mRegionRenderer.initialise(mScreenManager.core());
		mProjectileRenderer.initialise(mScreenManager.core());
		mHUDRenderer.initialise(mScreenManager.core());
		mMouseRenderer.initialise(mScreenManager.core());
		mDebugRenderer.initialise(mScreenManager.core());
		mSpreadRenderer.initialise(mScreenManager.core());

		// fasle to use the default seed
		mGameStateController.startNewGame(false);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mBackgroundTexture = TextureManager.textureManager().loadTexture("GameBackground", "res/textures/screens/background.png", GL11.GL_LINEAR);
	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F2)) {
			mScreenManager.addScreen(new HelpScreen(mScreenManager));

			return;

		}

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			mScreenManager.addScreen(new PauseScreen(mScreenManager));

			return;

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		if (pCoveredByOtherScreen)
			return;

		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (mGameStateController.isGameEnded()) {
			if (mGameStateController.isGameWon()) {
				// Game won
				mScreenManager.addScreen(new GameWonScreen(mScreenManager, entityGroupID));

			} else {
				// Game lost
				mScreenManager.addScreen(new GameLostScreen(mScreenManager, entityGroupID));

			}

		}

	}

	@Override
	public void draw(LintfordCore pCore) {
		GL11.glClearColor(82f / 255f, 104f / 255f, 150f / 255f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		float lParallaxX = -pCore.gameCamera().getPosition().x * 0.01f;
		float lParallaxY = -pCore.gameCamera().getPosition().y * 0.01f;

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mBackgroundTexture, lParallaxX, lParallaxY, 640, 640, -320, -320, 640, 640, -0.8f, 1f, 1f, 1f, 1f);
		lTextureBatch.end();

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		super.draw(pCore);

		GL11.glDisable(GL11.GL_DEPTH_TEST);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void updateStructureDimensions(LintfordCore pCore) {

	}

	@Override
	public void updateStructurePositions(LintfordCore pCore) {

	}

}
