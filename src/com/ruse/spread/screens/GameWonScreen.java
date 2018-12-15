package com.ruse.spread.screens;

import com.ruse.spread.controllers.GameStateController;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class GameWonScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREEN_TITLE = "You've Won!";

	public static final int BUTTON_RESTART_NEW = 1;
	public static final int BUTTON_RESTART_SAME = 2;
	public static final int BUTTON_EXIT = 3;

	// --------------------------------------
	// Variables
	// -------------------------------------

	private GameStateController mGameStateController;
	private int mGameEntityID;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameWonScreen(ScreenManager pScreenManager, int pGameEntityID) {
		super(pScreenManager, SCREEN_TITLE);

		mPaddingTop = 70f;
		mGameEntityID = pGameEntityID;

		BaseLayout lNavLayout = new ListLayout(this);

		MenuEntry lNextGame = new MenuEntry(pScreenManager, lNavLayout, "Next Map");
		MenuEntry lReplayGame = new MenuEntry(pScreenManager, lNavLayout, "Replay Map");
		MenuEntry lExit = new MenuEntry(pScreenManager, lNavLayout, "Exit");

		// Click handlers
		lNextGame.registerClickListener(this, BUTTON_RESTART_NEW);
		lReplayGame.registerClickListener(this, BUTTON_RESTART_SAME);
		lExit.registerClickListener(this, BUTTON_EXIT);

		lNavLayout.menuEntries().add(lNextGame);
		lNavLayout.menuEntries().add(lReplayGame);
		lNavLayout.menuEntries().add(lExit);

		layouts().add(lNavLayout);

		mIsPopup = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		final ControllerManager lControllerManager = mScreenManager.core().controllerManager();

		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, mGameEntityID);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_RESTART_NEW:
			mGameStateController.startNewGame(true);
			exitScreen();
			break;

		case BUTTON_RESTART_SAME:
			mGameStateController.startNewGame(false);
			exitScreen();
			break;

		// Go to main menu
		case BUTTON_EXIT:
			LoadingScreen.load(mScreenManager, false, new MenuBackground(mScreenManager), new MainMenuScreen(mScreenManager));
			break;
		}
	}

}
