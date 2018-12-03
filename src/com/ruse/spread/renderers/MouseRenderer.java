package com.ruse.spread.renderers;

import com.ruse.spread.controllers.MouseController;
import com.ruse.spread.data.world.WorldNode;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class MouseRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "MouseRenderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private MouseController mMouseController;
	
	private Texture mGameTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 6;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MouseRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mMouseController = (MouseController) lControllerManager.getControllerByNameRequired(MouseController.CONTROLLER_NAME, mEntityID);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);
		
		mGameTexture = TextureManager.textureManager().loadTexture("GameTexture", "res/textures/game.png");
		
	}
	
	@Override
	public void draw(LintfordCore pCore) {
		float lCursorX = pCore.HUD().getMouseWorldSpaceX();
		float lCursorY = pCore.HUD().getMouseWorldSpaceY();

		if (mMouseController.isBuilding && mMouseController.tempWorldNode != null) {
			WorldNode lTempNode = mMouseController.tempWorldNode;

			int xSrc = 0;
			int ySrc = 128;
			switch (lTempNode.nodeType()) {
			case WorldNode.NODE_TYPE_HQ:
				break;
			case WorldNode.NODE_TYPE_NORMAL:
				xSrc = 32;
				ySrc = 128;
				break;
			case WorldNode.NODE_TYPE_LONG:
				xSrc = 64;
				ySrc = 128;
				break;
			case WorldNode.NODE_TYPE_STORAGE:
				xSrc = 96;
				ySrc = 128;
				break;
			case WorldNode.NODE_TYPE_TURRET:
				xSrc = 128;
				ySrc = 128;
				break;
			case WorldNode.NODE_TYPE_PILLBOX:
				xSrc = 160;
				ySrc = 128;
				break;
			case WorldNode.NODE_TYPE_MORTAR:
				xSrc = 192;
				ySrc = 128;
				break;
			}

			TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mGameTexture, xSrc, ySrc, 32, 32, lCursorX - 16, lCursorY - 16, 32, 32, -0.01f, 1f, 1f, 1f, 1f);
			lTextureBatch.end();

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
