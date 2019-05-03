package com.ruse.spread.renderers;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.GameConstants;
import com.ruse.spread.controllers.WorldController;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
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
	private boolean mDrawRegionUIDs = false;
	private boolean mDrawTileCoords = false;
	private boolean mDrawSpreadPopulation = false;

	private Texture mUITexture;

	@Override
	public int ZDepth() {
		return 20;
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

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUITexture = pResourceManager.textureManager().textureCore();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F5)) {
			mDrawHeights = !mDrawHeights;

		}

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F6)) {
			mDrawTileCoords = !mDrawTileCoords;

		}

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F7)) {
			mDrawSpreadPopulation = !mDrawSpreadPopulation;

		}

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F9)) {
			mDrawRegionUIDs = !mDrawRegionUIDs;

		}

		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mDrawGrid)
			renderGrid(pCore);

		if (mDrawCenter)
			renderCenterPoint(pCore);

		renderValues(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void renderGrid(LintfordCore pCore) {
		int width = mWorldController.gameWorld().world().width;
		int height = mWorldController.gameWorld().world().height;
		int[] lGroundHeights = mWorldController.gameWorld().world().groundHeight;

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.gameCamera());

		if (mDrawGrid) {

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					final int ti = y * width + x;
					final float maxDepth = 16f;
					final int tileSize = GameConstants.TILE_SIZE;
					final float dAmt = 1f - lGroundHeights[ti] / (maxDepth * 1f);

					final float xOff = -width * tileSize * 0.5f;
					final float yOff = -height * tileSize * 0.5f;

					lTextureBatch.draw(mUITexture, 127, 0, 32, 32, xOff + x * tileSize, yOff + y * tileSize, tileSize, tileSize, -0.1f, 1f, 1f, 1f, dAmt);

				}

			}

		}

		lTextureBatch.end();
	}

	private void renderValues(LintfordCore pCore) {
		int width = mWorldController.gameWorld().world().width;
		int height = mWorldController.gameWorld().world().height;

		int[] lGroundHeights = mWorldController.gameWorld().world().groundHeight;
		int[] lSpreadPopulation = mWorldController.gameWorld().world().spreadPopulation;
		int[] regionUID = mWorldController.gameWorld().world().regionIDs;

		FontUnit lFont = mRendererManager.textFont();

		final boolean lDrawGroundHeight = mDrawHeights;
		final boolean lDrawRegionUID = mDrawRegionUIDs;
		final boolean lDrawTileIndex = mDrawTileCoords;

		lFont.begin(pCore.gameCamera());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;
				final int tileSize = GameConstants.TILE_SIZE;
				final int tileDepth = lGroundHeights[ti];
				final int spreadPop = lSpreadPopulation[ti];
				final int sRegionUID = regionUID[ti];

				final float xOff = -width * tileSize * 0.5f + tileSize * 0.05f;
				final float yOff = -height * tileSize * 0.5f + tileSize * 0.05f;

				if (lDrawGroundHeight)
					lFont.draw("" + tileDepth, xOff + x * tileSize, yOff + y * tileSize, -0.1f, 0.75f, 0.8f, 0.3f, 1f, 0.8f, -1);

				// Bottom left (Region UID)
				if (lDrawRegionUID)
					lFont.draw("" + sRegionUID, xOff + x * tileSize, yOff + y * tileSize + tileSize * 0.5f, -0.1f, 0.15f, 0.8f, 0.15f, 1f, 0.8f, -1);

				if (lDrawTileIndex)
					lFont.draw("" + ti, xOff + x * tileSize, yOff + y * tileSize + tileSize * 0.5f, -0.1f, 0.15f, 0.8f, 0.15f, 1f, 0.8f, -1);

				if (mDrawSpreadPopulation)
					lFont.draw("" + spreadPop, xOff + x * tileSize, yOff + y * tileSize, -0.1f, 0.75f, 0.8f, 0.3f, 1f, 0.8f, -1);

			}

		}

		lFont.end();

	}

	private void renderCenterPoint(LintfordCore pCore) {
		Debug.debugManager().drawers().drawRectImmediate(pCore.gameCamera(), -2, -2, 4, 4, 1f, 0, 0);

	}

}
