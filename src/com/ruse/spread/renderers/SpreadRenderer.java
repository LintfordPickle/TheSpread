package com.ruse.spread.renderers;

import com.ruse.spread.GameConstants;
import com.ruse.spread.controllers.WorldController;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class SpreadRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "SpreadRenderer";

	@Override
	public int ZDepth() {
		return 4;
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;
	private Texture mGroundTexture;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SpreadRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, mEntityID);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mGroundTexture = TextureManager.textureManager().loadTexture("GameTexture", "res/textures/game.png");

	}

	@Override
	public void draw(LintfordCore pCore) {
		drawRegions(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawRegions(LintfordCore pCore) {
		int width = mWorldController.gameWorld().world().width;
		int height = mWorldController.gameWorld().world().height;
		int[] spreadPopulation = mWorldController.gameWorld().world().spreadPopulation;
		int[] timer = mWorldController.gameWorld().world().timer;
		int[] variations = mWorldController.gameWorld().world().variants;

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.gameCamera());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;
				if (spreadPopulation[ti] == 0x0)
					continue; // no spread here

				float lR = 1f;
				float lG = 0f;
				float lB = 0f;
				float lA = 1f;

				float srcX = 0;
				float srcY = 0;

				timer[ti]--;
				if (timer[ti] <= 0) {
					variations[ti] = (int) (RandomNumbers.getRandomChance(2) ? RandomNumbers.random(0, 6) * 32 : 0f);
					timer[ti] = 128; // frames
				}

				final int tileSize = GameConstants.TILE_SIZE;

				final float xOff = -width * tileSize * 0.5f;
				final float yOff = -height * tileSize * 0.5f;

				lTextureBatch.draw(mGroundTexture, srcX + variations[ti], srcY, 32, 32, xOff + x * tileSize, yOff + y * tileSize, tileSize, tileSize, -0.2f, lR, lG, lB, lA);

			}

		}

		lTextureBatch.end();

	}

}
