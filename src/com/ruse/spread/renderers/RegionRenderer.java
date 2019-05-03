package com.ruse.spread.renderers;

import com.ruse.spread.GameConstants;
import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldRegion;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class RegionRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "RegionRenderer";

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

	public RegionRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mGroundTexture = pResourceManager.textureManager().loadTexture("GameTexture", "res/textures/game.png", entityGroupID());

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
		int[] regionTiles = mWorldController.gameWorld().world().regionIDs;
		int[] timer = mWorldController.gameWorld().world().timer;
		int[] variations = mWorldController.gameWorld().world().variants;

		World lWorld = mWorldController.gameWorld().world();

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.gameCamera());

		WorldRegion lRegion = null;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;
				lRegion = null;
				if (regionTiles[ti] == 0x0)
					continue; // no region here

				if (lRegion == null || lRegion.uid() != regionTiles[ti]) {
					lRegion = lWorld.getRegionByUID(regionTiles[ti]);
					if (lRegion == null)
						continue;

				}

				float lR = 1f;
				float lG = 1f;
				float lB = 1f;
				float lA = 1f;

				float srcX = 0;
				float srcY = 0;

				switch (lRegion.type()) {
				case World.REGION_TYPE_CITY:
					srcY = 64;
					lR = 1f;
					lG = 1f;
					lB = 1f;
					if (timer[ti] <= 0) {
						variations[ti] = (int) (RandomNumbers.random(0, 3) * 32);
						timer[ti] = 1;
					}
					break;
				case World.REGION_TYPE_FARM:
					srcY = 96;
					lR = 1f;
					lG = 1f;
					lB = 1f;
					if (timer[ti] <= 0) {
						variations[ti] = (int) (RandomNumbers.random(0, 3) * 32);
						timer[ti] = 1;
					}
					break;
				case World.REGION_TYPE_MINE:
					srcY = 32;
					lR = 1f;
					lG = 1f;
					lB = 1f;
					if (timer[ti] <= 0) {
						variations[ti] = (int) (RandomNumbers.random(0, 3) * 32);
						timer[ti] = 1;
					}
					break;
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
