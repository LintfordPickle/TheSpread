package com.ruse.spread.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldContourLine;
import com.ruse.spread.data.world.WorldTile;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Vector3f;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class WorldRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "WorldRenderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;
	private Texture mGroundTexture;

	@Override
	public int ZDepth() {
		return 2;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public WorldRenderer(RendererManager pRendererManager, int pGroupID) {
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

		drawGround(pCore);
		drawContours(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawGround(LintfordCore pCore) {
		int width = mWorldController.gameWorld().world().width;
		int height = mWorldController.gameWorld().world().height;
		int[] levelTiles = mWorldController.gameWorld().world().ground;

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.gameCamera());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int ti = y * width + x;
				int tile = levelTiles[ti];

				Vector3f tileColor = WorldTile.getTileColor(tile);

				final float maxDepth = 16f;
				final int tileSize = World.TILE_SIZE;
				final float dAmt = WorldTile.getTileHeight(tile) / (maxDepth * 0.5f);

				final float xOff = -width * tileSize * 0.5f;
				final float yOff = -height * tileSize * 0.5f;

				lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, xOff + x * tileSize, yOff + y * tileSize, tileSize, tileSize, -0.3f, tileColor.x * dAmt, tileColor.y * dAmt, tileColor.z * dAmt, 1f);
				lTextureBatch.draw(mGroundTexture, 0, 0, 32, 32, xOff + x * tileSize, yOff + y * tileSize, tileSize, tileSize, -0.3f, tileColor.x * dAmt, tileColor.y * dAmt, tileColor.z * dAmt, 0.8f);
				
			}
		}

		lTextureBatch.end();
	}

	private void drawContours(LintfordCore pCore) {
		List<WorldContourLine> lLines = mWorldController.gameWorld().world().mWorldContours;

		GL11.glLineWidth(1);
		
		LineBatch lLineBatch = mRendererManager.uiLineBatch();

		final int lLineCount = lLines.size();
		lLineBatch.begin(pCore.gameCamera());
		for (int i = 0; i < lLineCount; i++) {
			WorldContourLine lLine = lLines.get(i);
			float pAmt = 0f;
			lLineBatch.a = 1f / lLine.height * 1f;
			lLineBatch.draw(lLine.start.x, lLine.start.y, lLine.end.x, lLine.end.y, -0.3f, pAmt, pAmt, pAmt);

		}
		lLineBatch.end();
	}

}
