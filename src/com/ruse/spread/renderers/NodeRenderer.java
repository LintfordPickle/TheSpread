package com.ruse.spread.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.ruse.spread.controllers.MouseController;
import com.ruse.spread.controllers.NodeController;
import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldEdge;
import com.ruse.spread.data.world.WorldNode;
import com.ruse.spread.data.world.WorldPackage;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class NodeRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "ObjectRenderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;
	private MouseController mMouseController;
	private NodeController mNodeController;

	private Texture mGameTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 4;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public NodeRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, mEntityID);
		mMouseController = (MouseController) lControllerManager.getControllerByNameRequired(MouseController.CONTROLLER_NAME, mEntityID);
		mNodeController = (NodeController) lControllerManager.getControllerByNameRequired(NodeController.CONTROLLER_NAME, mEntityID);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mGameTexture = TextureManager.textureManager().loadTexture("GameTexture", "res/textures/game.png");

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {

		if (pCore.input().isMouseTimedLeftClickAvailable()) {
			if (mMouseController.isBuilding && mMouseController.tempWorldNode != null) {
				if (!mNodeController.addNode(mMouseController.tempWorldNode)) {
					//

				}

				mMouseController.isBuilding = false;
				mMouseController.tempWorldNode = null;

				pCore.input().setLeftMouseClickHandled();
				return true;

			}

		}

		return super.handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mMouseController.isBuilding && mMouseController.tempWorldNode != null) {
			// Update the node on the mouse
			WorldNode lUpdateNode = mMouseController.tempWorldNode;

			int ti = mWorldController.gameWorld().world().getTileFromWorldPosition(pCore.gameCamera().getMouseWorldSpaceX(), pCore.gameCamera().getMouseWorldSpaceY());

			// place a node, if viable
			lUpdateNode.tileIndex = ti;

			mNodeController.checkViability(lUpdateNode);

		}

	}

	@Override
	public void draw(LintfordCore pCore) {

		drawEdges(pCore);

		drawPackages(pCore);

		drawNodes(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void drawNodes(LintfordCore pCore) {
		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();
		FontUnit lFont = mRendererManager.textFont();

		lTextureBatch.begin(pCore.gameCamera());
		lFont.begin(pCore.gameCamera());

		List<WorldNode> lNodes = mWorldController.gameWorld().nodes();
		final int lNumNodes = lNodes.size();
		for (int i = 0; i < lNumNodes; i++) {
			WorldNode lNode = lNodes.get(i);

			int xSrc = 0;
			int ySrc = 128;
			switch (lNode.nodeType()) {
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

			float lNotConstructedModifier = lNode.isConstructed ? 1f : 0.5f;

			lTextureBatch.draw(mGameTexture, xSrc, ySrc, 32, 32, lNode.mBounds, -0.2f, 1f * lNotConstructedModifier, 1f * lNotConstructedModifier, 1f * lNotConstructedModifier, lNotConstructedModifier);
			lTextureBatch.draw(mGameTexture, 64, 160, 16, 16, lNode.mBounds.x+16, lNode.mBounds.y - 16, 16, 16, -0.2f, 1f * lNotConstructedModifier, 1f * lNotConstructedModifier, 1f * lNotConstructedModifier, lNotConstructedModifier);

			if (true) // pop
				lFont.draw("" + lNode.populationStore, lNode.mBounds.x, lNode.mBounds.y - 18, -0.2f, 1f, 0f, 0f, 1f, 1f);

			if (false) // hash
				lFont.draw("" + lNode.hashCode(), lNode.mBounds.x, lNode.mBounds.y - 16, -0.2f, 1f, 0f, 0f, 1f, 1f);

//			if (true)
//				lFont.draw("" + lNode.id, lNode.mBounds.x, lNode.mBounds.y - 16, -0.2f, 1f, 0f, 0f, 1f, 1f);

		}

		lTextureBatch.end();
		lFont.end();
	}

	public void drawEdges(LintfordCore pCore) {
		LineBatch lLineBatch = mRendererManager.uiLineBatch();

		final float xOff = -World.WIDTH * 0.5f * World.TILE_SIZE;
		final float yOff = -World.HEIGHT * 0.5f * World.TILE_SIZE;

		lLineBatch.begin(pCore.gameCamera());

		List<WorldEdge> lEdges = mWorldController.gameWorld().edges();
		final int lNumEdges = lEdges.size();
		for (int i = 0; i < lNumEdges; i++) {
			WorldEdge lEdge = lEdges.get(i);

			float x1 = lEdge.node1.tileIndex % World.WIDTH * World.TILE_SIZE;
			float y1 = lEdge.node1.tileIndex / World.WIDTH * World.TILE_SIZE;

			float x2 = lEdge.node2.tileIndex % World.WIDTH * World.TILE_SIZE;
			float y2 = lEdge.node2.tileIndex / World.WIDTH * World.TILE_SIZE;

			boolean lIsActive = lEdge.node1.isConstructed && lEdge.node2.isConstructed;

			float lR = lIsActive ? (80f / 255f) : 0.2f;
			float lG = lIsActive ? (72f / 255f) : 0.2f;
			float lB = lIsActive ? (36f / 255f) : 0.2f;

			GL11.glLineWidth(3);
			final float lHalfTile = World.TILE_SIZE / 2f;
			lLineBatch.a = 0.7f;
			lLineBatch.draw(xOff + x1 + lHalfTile, yOff + y1 + lHalfTile, xOff + x2 + lHalfTile, yOff + y2 + lHalfTile, -0.3f, lR, lG, lB);

		}

		lLineBatch.end();
	}

	public void drawPackages(LintfordCore pCore) {
		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();
		FontUnit lFont = mRendererManager.textFont();

		lTextureBatch.begin(pCore.gameCamera());
		lFont.begin(pCore.gameCamera());

		List<WorldPackage> lPackage = mWorldController.gameWorld().packages();
		final int lNumNodes = lPackage.size();
		for (int i = 0; i < lNumNodes; i++) {
			WorldPackage lWorldPackage = lPackage.get(i);
			if (!lWorldPackage.isMoving)
				continue;

			int xSrc = 160;
			int ySrc = 0;
			switch (lWorldPackage.packageType) {
			case food:
				xSrc = 192;
				ySrc = 0;
				break;
			case metal:
				xSrc = 224;
				ySrc = 0;
				break;
			case population:
				xSrc = 160;
				ySrc = 0;
				break;
			default:
				break;

			}

			Rectangle lRect = lWorldPackage.mBounds;
			lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, xSrc, ySrc, 16, 16, lRect, -0.25f, 1f, 1f, 1f, 1f);

		}

		lTextureBatch.end();
		lFont.end();
	}

}
