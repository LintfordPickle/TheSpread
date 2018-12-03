package com.ruse.spread.screenmanager.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class MainMenuScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREEN_TITLE = "";

	public static final int BUTTON_START = 1;
	public static final int BUTTON_OPTIONS = 2;
	public static final int BUTTON_EXIT = 3;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MainMenuScreen(ScreenManager pScreenManager) {
		super(pScreenManager, SCREEN_TITLE);

		mPaddingTop = 150f;

		BaseLayout lNavLayout = new ListLayout(this);

		MenuEntry lStartGame = new MenuEntry(pScreenManager, lNavLayout, "Start Game");
		MenuEntry lExit = new MenuEntry(pScreenManager, lNavLayout, "Exit");

		// Click handlers
		lStartGame.registerClickListener(this, BUTTON_START);
		lExit.registerClickListener(this, BUTTON_EXIT);

		lNavLayout.menuEntries().add(lStartGame);
		lNavLayout.menuEntries().add(lExit);

		layouts().add(lNavLayout);

		mESCBackEnabled = false;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_START:
			LoadingScreen.load(mScreenManager, true, new GameScreen(mScreenManager));
			break;

		case BUTTON_OPTIONS:
			break;

		case BUTTON_EXIT:
			mScreenManager.exitGame();
			break;
		}
	}

}
