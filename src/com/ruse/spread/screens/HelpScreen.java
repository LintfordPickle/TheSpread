package com.ruse.spread.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.entries.HorizontalEntryGroup;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class HelpScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_RESUME = 1;
	public static final int BUTTON_NEXTPAGE = 2;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Texture mBackgroundTexture;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public HelpScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "");

		mPaddingTop = 460f;

		BaseLayout lNavLayout = new ListLayout(this);

		MenuEntry lResumeGame = new MenuEntry(pScreenManager, lNavLayout, "Resume");
		MenuEntry lNextPage = new MenuEntry(pScreenManager, lNavLayout, "next Page");

		// Click handlers
		lResumeGame.registerClickListener(this, BUTTON_RESUME);
		lNextPage.registerClickListener(this, BUTTON_NEXTPAGE);

		HorizontalEntryGroup lHorizontalEntry = new HorizontalEntryGroup(pScreenManager, lNavLayout);
		lHorizontalEntry.addEntry(lResumeGame);
		lHorizontalEntry.addEntry(lNextPage);

		lNavLayout.menuEntries().add(lHorizontalEntry);

		layouts().add(lNavLayout);

		mIsPopup = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mBackgroundTexture = pResourceManager.textureManager().loadTexture("HelpScreen1", "res/textures/screens/help01.png", entityGroupID());
	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

	}

	@Override
	public void draw(LintfordCore pCore) {

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mBackgroundTexture, 0, 0, 640, 640, -320, -320, 640, 640, -0.1f, 1f, 1f, 1f, 1f);
		lTextureBatch.end();

		super.draw(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_RESUME:
			exitScreen();
			break;

		case BUTTON_NEXTPAGE:
			mScreenManager.addScreen(new HelpScreen02(mScreenManager));
			exitScreen();
			break;
		}
	}

}
