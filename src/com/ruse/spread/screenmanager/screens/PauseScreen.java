package com.ruse.spread.screenmanager.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class PauseScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREEN_TITLE = "Paused";

	public static final int BUTTON_RESUME = 1;
	public static final int BUTTON_OPTIONS = 2;
	public static final int BUTTON_EXIT = 3;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PauseScreen(ScreenManager pScreenManager) {
		super(pScreenManager, SCREEN_TITLE);

		mPaddingTop = 70f;

		BaseLayout lNavLayout = new ListLayout(this);

		MenuEntry lStartGame = new MenuEntry(pScreenManager, lNavLayout, "Resume");
		MenuEntry lExit = new MenuEntry(pScreenManager, lNavLayout, "Exit");

		// Click handlers
		lStartGame.registerClickListener(this, BUTTON_RESUME);
		lExit.registerClickListener(this, BUTTON_EXIT);

		lNavLayout.menuEntries().add(lStartGame);
		lNavLayout.menuEntries().add(lExit);

		layouts().add(lNavLayout);

		mIsPopup = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_RESUME:
			exitScreen();
			break;

		case BUTTON_OPTIONS:
			break;

		case BUTTON_EXIT:
			LoadingScreen.load(mScreenManager, true, new MenuBackground(mScreenManager), new MainMenuScreen(mScreenManager));
			break;
		}
	}

}
