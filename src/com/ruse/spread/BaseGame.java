package com.ruse.spread;

import org.lwjgl.opengl.GL11;

import com.ruse.spread.screenmanager.screens.MainMenuScreen;
import com.ruse.spread.screenmanager.screens.MenuBackground;

import net.lintford.library.ConstantsTable;
import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.IMenuAction;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.LoadingScreen;
import net.lintford.library.screenmanager.screens.TimedIntroScreen;

public class BaseGame extends LintfordCore {

	public static final String GAME_NAME = "The Spread";

	public static final String GAME_FONT_NAME = "GameFont";
	public static final String GAME_FONT_LOCATION = "res/fonts/OxygenMono-Regular.ttf";

	// final build flags
	public static final boolean SHOW_INTRO = true;
	public static final boolean SHOW_FPS = false;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ScreenManager mScreenManager;
	private TextureBatch mTextureBatch;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BaseGame(GameInfo pGameInfo, String[] pArgs) {
		super(pGameInfo, pArgs);

		ConstantsTable.registerValue("APPLICATION_NAME", GAME_NAME);

		mIsFixedTimeStep = true;

		mScreenManager = new ScreenManager(this);
		mTextureBatch = new TextureBatch();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void onInitialiseGL() {
		super.onInitialiseGL();

		mMasterConfig.display().setDisplayMouse(false);

	}

	@Override
	protected void onInitialiseApp() {
		super.onInitialiseApp();

		// TODO: preload textures
		TextureManager.TEXTURE_CORE_UI.reloadTexture("res/textures/core/system.png");
		TextureManager.textureManager().loadTexture(LoadingScreen.LOADING_BACKGROUND_TEXTURE_NAME, "res/textures/screens/loading.png", GL11.GL_NEAREST, true);

		// Load fonts
		mResourceManager.fontManager().loadNewFont(GAME_FONT_NAME, GAME_FONT_LOCATION, 16);

		if (SHOW_INTRO) {
			TimedIntroScreen lSplashScreen = new TimedIntroScreen(mScreenManager, "res/textures/screens/splash.png", 4f);
			lSplashScreen.stretchBackgroundToFit(false);
			lSplashScreen.setTextureSrcRectangle(0, 0, 640, 640);
			lSplashScreen.setTimerFinishedCallback(new IMenuAction() {

				@Override
				public void TimerFinished(Screen pScreen) {

					// Add screens to screenmanager
					mScreenManager.addScreen(new MenuBackground(mScreenManager));
					mScreenManager.addScreen(new MainMenuScreen(mScreenManager));

				}

			});

			mScreenManager.addScreen(lSplashScreen);

		} else {
			// Add screens to screenmanager
			mScreenManager.addScreen(new MenuBackground(mScreenManager));
			mScreenManager.addScreen(new MainMenuScreen(mScreenManager));

		}

		Debug.debugManager().profiler().isOpen(SHOW_FPS);

		mScreenManager.initialise(GAME_FONT_LOCATION);

	}

	@Override
	protected void onLoadGLContent() {
		super.onLoadGLContent();

		mScreenManager.loadGLContent(mResourceManager);
		mTextureBatch.loadGLContent(mResourceManager);

	}

	@Override
	protected void onUnloadGLContent() {
		super.onUnloadGLContent();

		mScreenManager.unloadGLContent();
		mTextureBatch.unloadGLContent();
	}

	@Override
	protected void onHandleInput() {
		super.onHandleInput();

		mScreenManager.handleInput(this);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		mScreenManager.update(this);

	}

	@Override
	protected void onDraw() {
		super.onDraw();

		mScreenManager.draw(this);

		float lCursorX = HUD().getMouseWorldSpaceX();
		float lCursorY = HUD().getMouseWorldSpaceY();

		mTextureBatch.begin(HUD());
		mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 256, 0, 32, 32, lCursorX, lCursorY, 32, 32, -0.01f, 1f, 1f, 1f, 1f);
		mTextureBatch.end();

	}

	// ---------------------------------------------
	// Entry Point
	// ---------------------------------------------

	public static void main(String[] args) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String applicationName() {
				return GAME_NAME;
			}

			@Override
			public String windowTitle() {
				return GAME_NAME;
			}

			@Override
			public int baseGameResolutionWidth() {
				return defaultWindowWidth();
			}

			@Override
			public int baseGameResolutionHeight() {
				return defaultWindowHeight();
			}

			@Override
			public int defaultWindowWidth() {
				return 640;
			}

			@Override
			public int defaultWindowHeight() {
				return 640;
			}

			@Override
			public int minimumWindowWidth() {
				return 640;
			}

			@Override
			public int minimumWindowHeight() {
				return 640;
			}

			@Override
			public boolean windowResizeable() {
				return false;
			}

		};

		BaseGame lClient = new BaseGame(lGameInfo, args);
		lClient.createWindow();
	}

}
