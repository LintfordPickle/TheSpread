package com.ruse.spread.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuBackground extends Screen {

	private Texture mBackgroundTexture;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuBackground(ScreenManager pScreenManager) {
		super(pScreenManager);

		mShowInBackground = true;
		
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mBackgroundTexture = TextureManager.textureManager().loadTexture("BackgroundTexture", "res/textures/screens/menu.png");

	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

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
	public void updateStructurePositions(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStructureDimensions(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitScreen() {
		super.exitScreen();

	}

}
