package com.ruse.spread.renderers;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldTile;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "DebugRenderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;

	// Add to options screen
	private boolean mDrawGrid = false;
	private boolean mDrawCenter = false;
	private boolean mDrawHeights = false;

	@Override
	public int ZDepth() {
		return 4;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public DebugRenderer(RendererManager pRendererManager, int pGroupID) {
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
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F4)) {
			mDrawHeights = !mDrawHeights;

		}

	}

	@Override
	public void draw(LintfordCore pCore) {
		mDrawGrid = false;
		if (mDrawGrid)
			renderGrid(pCore);

		if (mDrawCenter)
			renderCenterPoint(pCore);

		if (mDrawHeights)
			renderHeights(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void renderGrid(LintfordCore pCore) {
		int width = mWorldController.gameWorld().world().width;
		int height = mWorldController.gameWorld().world().height;
		int[] levelTiles = mWorldController.gameWorld().world().ground;

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.gameCamera());

		if (mDrawGrid) {

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					final int ti = y * width + x;
					int tile = levelTiles[ti];

					final float maxDepth = 16f;
					final int tileSize = World.TILE_SIZE;
					final float dAmt = 1f - WorldTile.getTileHeight(tile) / (maxDepth * 1f);

					final float xOff = -width * tileSize * 0.5f;
					final float yOff = -height * tileSize * 0.5f;

					lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 127, 0, 32, 32, xOff + x * tileSize, yOff + y * tileSize, tileSize, tileSize, -0.1f, 1f, 1f, 1f, dAmt);

				}

			}

		}

		lTextureBatch.end();
	}

	private void renderHeights(LintfordCore pCore) {
		int width = mWorldController.gameWorld().world().width;
		int height = mWorldController.gameWorld().world().height;

		int[] levelTiles = mWorldController.gameWorld().world().ground;
		int[] spreadDepth = mWorldController.gameWorld().world().spreaderDepth;
		int[] regionUID = mWorldController.gameWorld().world().regions;
		int[] tileHealth = mWorldController.gameWorld().world().regionHealth;

		FontUnit lFont = mRendererManager.textFont();

		final boolean lDrawGroundHeight = true;
		final boolean lDrawSpreadHeight = true;
		final boolean lDrawHealth = false;
		final boolean lDrawRegionUID = false;
		final boolean lDrawTileIndex = false;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;
				int tile = levelTiles[ti];

				final int tileSize = World.TILE_SIZE;
				final int tileDepth = WorldTile.getTileHeight(tile);
				final int sDepth = tileDepth + spreadDepth[ti];
				final int sRegionUID = regionUID[ti];
				final int stileHealth = tileHealth[ti];

				final float xOff = -width * tileSize * 0.5f + tileSize * 0.25f;
				final float yOff = -height * tileSize * 0.5f + tileSize * 0.25f;

				lFont.begin(pCore.gameCamera());

				if (lDrawGroundHeight)
					lFont.draw("" + tileDepth, xOff + x * tileSize, yOff + y * tileSize, -0.1f, 0.75f, 0.8f, 0.3f, 1f, 0.8f, -1);

				if (lDrawSpreadHeight)
					lFont.draw("" + sDepth, xOff + x * tileSize + tileSize * 0.5f, yOff + y * tileSize, -0.1f, 0.7f, 0.7f, 1f, 1f, 0.8f, -1);

				// Bottom left (Region UID)
				if (lDrawRegionUID)
					lFont.draw("" + sRegionUID, xOff + x * tileSize, yOff + y * tileSize + tileSize * 0.5f, -0.1f, 0.15f, 0.8f, 0.15f, 1f, 0.8f, -1);

				if (lDrawTileIndex)
					lFont.draw("" + ti, xOff + x * tileSize, yOff + y * tileSize + tileSize * 0.5f, -0.1f, 0.15f, 0.8f, 0.15f, 1f, 0.8f, -1);

				// Bottom right (health)
				if (lDrawHealth)
					lFont.draw("" + stileHealth, xOff + x * tileSize, yOff + y * tileSize, -0.1f, 0.7f, 0.2f, 0.2f, 1f, 0.8f, -1);

				lFont.end();
			}

		}

	}

	private void renderCenterPoint(LintfordCore pCore) {
		Debug.debugManager().drawers().drawRect(pCore.gameCamera(), -2, -2, 4, 4, 1f, 0, 0);
	}

}
