package com.ruse.spread.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.ruse.spread.GameConstants;
import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.world.WorldEdge;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class RoadRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "RoadRenderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 3;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public RoadRenderer(RendererManager pRendererManager, int pGroupID) {
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
	public void draw(LintfordCore pCore) {
		drawRoads(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void drawRoads(LintfordCore pCore) {
		LineBatch lLineBatch = mRendererManager.uiLineBatch();

		final float xOff = -GameConstants.WIDTH * 0.5f * GameConstants.TILE_SIZE;
		final float yOff = -GameConstants.HEIGHT * 0.5f * GameConstants.TILE_SIZE;

		lLineBatch.begin(pCore.gameCamera());

		List<WorldEdge> lEdges = mWorldController.gameWorld().edges();
		final int lNumEdges = lEdges.size();
		for (int i = 0; i < lNumEdges; i++) {
			WorldEdge lEdge = lEdges.get(i);

			float x1 = lEdge.node1.tileIndex % GameConstants.WIDTH * GameConstants.TILE_SIZE;
			float y1 = lEdge.node1.tileIndex / GameConstants.WIDTH * GameConstants.TILE_SIZE;

			float x2 = lEdge.node2.tileIndex % GameConstants.WIDTH * GameConstants.TILE_SIZE;
			float y2 = lEdge.node2.tileIndex / GameConstants.WIDTH * GameConstants.TILE_SIZE;

			boolean lIsActive = lEdge.node1.isConstructed && lEdge.node2.isConstructed;

			float lR = lIsActive ? (80f / 255f) : 0.2f;
			float lG = lIsActive ? (43f / 255f) : 0.2f;
			float lB = lIsActive ? (26f / 255f) : 0.2f;

			int lLineWidth = (int) (3f * pCore.gameCamera().getZoomFactor());
			GL11.glLineWidth(lLineWidth);
			final float lHalfTile = GameConstants.TILE_SIZE / 2f;
			lLineBatch.a = 0.7f;
			lLineBatch.draw(xOff + x1 + lHalfTile, yOff + y1 + lHalfTile, xOff + x2 + lHalfTile, yOff + y2 + lHalfTile, -0.3f, lR, lG, lB);

		}

		lLineBatch.end();
	}

}
