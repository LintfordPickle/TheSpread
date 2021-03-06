package com.ruse.spread.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class HelpScreen02 extends MenuScreen {

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

	public HelpScreen02(ScreenManager pScreenManager) {
		super(pScreenManager, "");

		mPaddingTop = 460f;

		BaseLayout lNavLayout = new ListLayout(this);

		MenuEntry lResumeGame = new MenuEntry(pScreenManager, lNavLayout, "Resume");

		// Click handlers
		lResumeGame.registerClickListener(this, BUTTON_RESUME);

		lNavLayout.menuEntries().add(lResumeGame);

		layouts().add(lNavLayout);

		mIsPopup = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mBackgroundTexture = pResourceManager.textureManager().loadTexture("HelpScreen2", "res/textures/screens/help02.png", entityGroupID());
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
		}
	}

}
