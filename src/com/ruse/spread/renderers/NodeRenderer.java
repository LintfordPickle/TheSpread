package com.ruse.spread.renderers;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.controllers.MouseController;
import com.ruse.spread.controllers.NodeController;
import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.world.WorldPackage;
import com.ruse.spread.data.world.nodes.WorldNode;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
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

	private Texture mUITexture;
	private Texture mGameTexture;
	private boolean mDrawPop;
	private boolean mDrawHash;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int ZDepth() {
		return 5;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public NodeRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		mDrawPop = true;
		mDrawHash = false;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, entityGroupID());
		mMouseController = (MouseController) lControllerManager.getControllerByNameRequired(MouseController.CONTROLLER_NAME, entityGroupID());
		mNodeController = (NodeController) lControllerManager.getControllerByNameRequired(NodeController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUITexture = pResourceManager.textureManager().textureCore();
		mGameTexture = pResourceManager.textureManager().loadTexture("GameTexture", "res/textures/game.png", entityGroupID());

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F8)) {
			mDrawHash = !mDrawHash;
		}

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
		drawPackages(pCore);

		drawNodes(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

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
			lTextureBatch.draw(mUITexture, xSrc, ySrc, 16, 16, lRect, -0.25f, 1f, 1f, 1f, 1f);

		}

		lTextureBatch.end();
		lFont.end();
	}

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
			case WorldNode.NODE_TYPE_SPREADER:
				xSrc = 96;
				ySrc = 32;
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
			if (mDrawPop && lNode.nodeType() != WorldNode.NODE_TYPE_SPREADER)
				lTextureBatch.draw(mGameTexture, 64, 160, 16, 16, lNode.mBounds.x + 16, lNode.mBounds.y - 16, 16, 16, -0.2f, 1f * lNotConstructedModifier, 1f * lNotConstructedModifier, 1f * lNotConstructedModifier,
						lNotConstructedModifier);

			float lPopR = 1f;
			float lPopG = (lNode.populationStore > lNode.maintainMinimumPop) ? 1f : 0f;
			float lPopB = (lNode.populationStore > lNode.maintainMinimumPop) ? 1f : 0f;

			if (lNode.nodeType() == WorldNode.NODE_TYPE_TURRET) {
				lTextureBatch.draw(mGameTexture, 160, 160, 32, 16, lNode.mBounds.centerX() - 6, lNode.mBounds.centerY() - 7, 32, 16, -0.2f, lNode.angle, 4, 7, 0.5f, 1f * lNotConstructedModifier, 1f * lNotConstructedModifier,
						1f * lNotConstructedModifier, lNotConstructedModifier);
			}

			if (mDrawPop && lNode.nodeType() != WorldNode.NODE_TYPE_SPREADER) // pop count
				lFont.draw("" + lNode.populationStore, lNode.mBounds.x, lNode.mBounds.y - 18, -0.2f, lPopR, lPopG, lPopB, 1f, 1f);

			if (mDrawHash) // hash
				lFont.draw("" + lNode.hashCode(), lNode.mBounds.x, lNode.mBounds.y + lNode.mBounds.h, -0.2f, 1f, 0f, 0f, 1f, 1f);

		}

		lTextureBatch.end();
		lFont.end();
	}

}
