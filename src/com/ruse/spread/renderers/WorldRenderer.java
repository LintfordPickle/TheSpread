package com.ruse.spread.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.ruse.spread.GameConstants;
import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.world.WorldContourLine;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Vector3f;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class WorldRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "WorldRenderer";

	public static Vector3f DIRT_COLOR = new Vector3f(124f / 255f, 75f / 255f, 42f / 255f);
	public static Vector3f GRASS_COLOR = new Vector3f(54f / 255f, 104f / 255f, 59f / 255f);
	public static Vector3f SAND_COLOR = new Vector3f(165f / 255f, 146f / 255f, 71f / 255f);

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;
	private Texture mGroundTexture;
	private TextureBatch mGroundTextureBatch;
	private Texture mUITexture;

	@Override
	public int ZDepth() {
		return 2;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public WorldRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		mGroundTextureBatch = new TextureBatch();

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

		mGroundTexture = pResourceManager.textureManager().loadTexture("GameTexture", "res/textures/game.png", entityGroupID());
		mGroundTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mGroundTextureBatch.unloadGLContent();

	}

	@Override
	public void draw(LintfordCore pCore) {

		drawGround(pCore);
		drawContours(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawGround(LintfordCore pCore) {
		int width = mWorldController.gameWorld().world().width;
		int height = mWorldController.gameWorld().world().height;
		int[] lGroundHeights = mWorldController.gameWorld().world().groundHeight;

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.gameCamera());
		mGroundTextureBatch.begin(pCore.gameCamera());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;
				Vector3f tileColor = SAND_COLOR;

				final float maxDepth = 16f;
				final int tileSize = GameConstants.TILE_SIZE;
				final float dAmt = lGroundHeights[ti] / (maxDepth * 0.5f);

				final float xOff = -width * tileSize * 0.5f;
				final float yOff = -height * tileSize * 0.5f;

				lTextureBatch.draw(mUITexture, 0, 0, 32, 32, xOff + x * tileSize, yOff + y * tileSize, tileSize, tileSize, -0.3f, tileColor.x * dAmt, tileColor.y * dAmt, tileColor.z * dAmt, 1f);
				mGroundTextureBatch.draw(mGroundTexture, 0, 0, 32, 32, xOff + x * tileSize, yOff + y * tileSize, tileSize, tileSize, -0.3f, tileColor.x * dAmt, tileColor.y * dAmt, tileColor.z * dAmt, 0.8f);

			}
		}

		lTextureBatch.end();
		mGroundTextureBatch.end();
	}

	private void drawContours(LintfordCore pCore) {
		List<WorldContourLine> lLines = mWorldController.gameWorld().world().mWorldContours;

		GL11.glLineWidth(1);
		
		GLDebug.checkGLErrorsException(getClass().getSimpleName());

		LineBatch lLineBatch = mRendererManager.uiLineBatch();
		
		GLDebug.checkGLErrorsException(getClass().getSimpleName());

		final int lLineCount = lLines.size();
		lLineBatch.begin(pCore.gameCamera());
		
		GLDebug.checkGLErrorsException(getClass().getSimpleName());
		
		for (int i = 0; i < lLineCount; i++) {
			WorldContourLine lLine = lLines.get(i);
			float pAmt = 0f;
			lLineBatch.a = 1f / lLine.height * 1f;
			lLineBatch.draw(lLine.start.x, lLine.start.y, lLine.end.x, lLine.end.y, -0.3f, pAmt, pAmt, pAmt);

			GLDebug.checkGLErrorsException(getClass().getSimpleName());
			
		}
		lLineBatch.end();
		
		GLDebug.checkGLErrorsException(getClass().getSimpleName());
	}

}
