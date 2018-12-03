package com.ruse.spread.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class HelpScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_RESUME = 1;

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

		mBackgroundTexture = TextureManager.textureManager().loadTexture("HelpScreen", "res/textures/screens/help.png");
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
		lTextureBatch.draw(mBackgroundTexture, 0, 0, 800, 600, -400, -300, 800, 600, -0.1f, 1f, 1f, 1f, 1f);
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
