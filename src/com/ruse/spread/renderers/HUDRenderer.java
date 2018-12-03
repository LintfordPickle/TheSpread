package com.ruse.spread.renderers;

import com.ruse.spread.controllers.GameStateController;
import com.ruse.spread.controllers.MouseController;
import com.ruse.spread.controllers.NodeController;
import com.ruse.spread.controllers.WorldController;
import com.ruse.spread.data.GameState;
import com.ruse.spread.data.regions.CityRegion;
import com.ruse.spread.data.regions.FarmRegion;
import com.ruse.spread.data.regions.MineRegion;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldNode;
import com.ruse.spread.data.world.WorldRegion;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class HUDRenderer extends BaseRenderer {

	public interface buyableItem {
		public abstract String getDisplayName();

		public abstract String getCostPop();

		public abstract String getCostFood();

		public abstract String getCostMetal();
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "HUDRenderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private NodeController mNodeController;
	private WorldController mWorldController;

	private Rectangle mNodeNormalButtonArea = new Rectangle();
	private Rectangle mNodeLongButtonArea = new Rectangle();
	private Rectangle mNodeStorageButtonArea = new Rectangle();

	private Rectangle mPillBoxButtonArea = new Rectangle();
	private Rectangle mTurretButtonArea = new Rectangle();
	private Rectangle mMortarButtonArea = new Rectangle();

	private Rectangle mDeleteNodeButtonArea = new Rectangle();

	private MouseController mMouseController;

	private String mMouseOverText;

	private WorldNode mSelectedNode;
	private WorldRegion mSelectedRegion;

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

	public HUDRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		final int PANEL_HEIGHT = 128;
		final float lPanelPositionY = 320 - PANEL_HEIGHT;

		mNodeNormalButtonArea.set(-106, lPanelPositionY + 48, 32, 32);
		mNodeLongButtonArea.set(-64, lPanelPositionY + 48, 32, 32);
		mNodeStorageButtonArea.set(-22, lPanelPositionY + 48, 32, 32);

		mPillBoxButtonArea.set(48, lPanelPositionY + 48, 32, 32);
		mTurretButtonArea.set(90, lPanelPositionY + 48, 32, 32);
		mMortarButtonArea.set(132, lPanelPositionY + 48, 32, 32);

		mDeleteNodeButtonArea.set(0, 0, 32, 32);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, mEntityID);
		mMouseController = (MouseController) lControllerManager.getControllerByNameRequired(MouseController.CONTROLLER_NAME, mEntityID);
		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, mEntityID);
		mNodeController = (NodeController) lControllerManager.getControllerByNameRequired(NodeController.CONTROLLER_NAME, mEntityID);

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {

		mMouseOverText = "";

		// Cancel Build
		if (pCore.input().isMouseTimedRightClickAvailable()) {

			clearSelected();

			if (mMouseController.isBuilding && mMouseController.tempWorldNode != null) {
				pCore.input().setRightMouseClickHandled();

				mMouseController.isBuilding = false;
				mMouseController.tempWorldNode = null;

				return true;

			}

		}

		if (handleHUDInput(pCore)) {
			// Something was selected on the HUD
			mSelectedRegion = null;
			mSelectedNode = null;

			return true;

		}

		if (handleWorldInput(pCore)) {
			return true;

		}

		return super.handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

	}

	@Override
	public void draw(LintfordCore pCore) {

		final int PANEL_HEIGHT = 128;
		final float lPanelPositionX = -320;
		final float lPanelPositionY = 320 - PANEL_HEIGHT;

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 128, 640, 128, lPanelPositionX, lPanelPositionY, 640, PANEL_HEIGHT, -0.02f, 1f, 1f, 1f, 1f);

		// Nodes
		lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 288, 0, 32, 32, mNodeNormalButtonArea, -0.02f, 1f, 1f, 1f, 1f);
		lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 288, 32, 32, 32, mNodeLongButtonArea, -0.02f, 1f, 1f, 1f, 1f);
		lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 288, 64, 32, 32, mNodeStorageButtonArea, -0.02f, 1f, 1f, 1f, 1f);

		// Military
		lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 320, 0, 32, 32, mPillBoxButtonArea, -0.02f, 1f, 1f, 1f, 1f);
		lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 320, 32, 32, 32, mTurretButtonArea, -0.02f, 1f, 1f, 1f, 1f);
		lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 320, 64, 32, 32, mMortarButtonArea, -0.02f, 1f, 1f, 1f, 1f);

		lTextureBatch.end();

		final GameState lGameState = mGameStateController.gameState();

		FontUnit lFont = mRendererManager.textFont();
		lFont.begin(pCore.HUD());

		lFont.draw(mMouseOverText, lPanelPositionX + 5f, lPanelPositionY - 25, -0.02f, 0.85f, 0.91f, 1f, 1f, 1f, -1);

		final float lTextScale = 1f;
		final float lTextPaddingX = 25f;
		final float lTextPaddingY = 20f;
		lFont.draw("HQ Status: ", lPanelPositionX + lTextPaddingX, lPanelPositionY + lTextPaddingY - 2, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
		lFont.draw("Population: " + lGameState.population + "/" + lGameState.populationWorld, lPanelPositionX + lTextPaddingX + 5f, lPanelPositionY + lTextPaddingY * 2, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
		lFont.draw("Food: " + lGameState.food, lPanelPositionX + lTextPaddingX + 5f, lPanelPositionY + lTextPaddingY * 3, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
		lFont.draw("Metals: " + lGameState.metals, lPanelPositionX + lTextPaddingX + 5f, lPanelPositionY + lTextPaddingY * 4, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);

		lFont.draw("Nodes", lPanelPositionX + 230 + lTextPaddingX, lPanelPositionY + lTextPaddingY - 2, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
		lFont.draw("Military Nodes", lPanelPositionX + 345 + lTextPaddingX, lPanelPositionY + lTextPaddingY - 2, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);

		lFont.draw("Press F2 for help", lPanelPositionX + 440 + lTextPaddingX, lPanelPositionY + lTextPaddingY + 75, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);

		lFont.end();

		float SUB_PANEL_HEIGHT = 100;
		float lMinusY = 325 - PANEL_HEIGHT - SUB_PANEL_HEIGHT;

		if (mSelectedNode != null) {
			// Render node information

			mDeleteNodeButtonArea.set(-320 + 270 - 32, lMinusY + 5, 32, 32);

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, -315, lMinusY, 270, SUB_PANEL_HEIGHT, -0.1f, 0.1f, 0.12f, 0f, 0.3f);
			lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 288, 0, 32, 32, mDeleteNodeButtonArea, -0.02f, 1f, 1f, 1f, 1f);
			lTextureBatch.end();

			lFont.begin(pCore.HUD());
			lFont.draw("Select Node: " + mSelectedNode.name, -310, lMinusY, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
			lFont.draw("People: " + mSelectedNode.populationStore + "/" + mSelectedNode.storageCapacityPopulation, -300, lMinusY + 25, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
			lFont.draw("Food: " + mSelectedNode.foodStore + "/" + mSelectedNode.storageCapacityFood, -300, lMinusY + 45, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
			lFont.draw("Metal: " + mSelectedNode.metalStore + "/" + mSelectedNode.storageCapacityMetal, -300, lMinusY + 65, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);

			lFont.end();

			lMinusY -= SUB_PANEL_HEIGHT + 5f;

		}

		if (mSelectedRegion != null) {
			// Render region information

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, -315, lMinusY, 270, SUB_PANEL_HEIGHT, -0.2f, 0.1f, 0.12f, 0f, 0.3f);
			lTextureBatch.end();

			lFont.begin(pCore.HUD());
			lFont.draw("Select Region: " + mSelectedRegion.name, -310, lMinusY, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);

			switch (mSelectedRegion.type()) {
			case World.TILE_TYPE_CITY:
				CityRegion lCity = (CityRegion) mSelectedRegion;
				lFont.draw("People: " + lCity.popStorage + "/" + lCity.popStorageCapacity, -300, lMinusY + 25, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
				lFont.draw("Food: " + lCity.foodStorage + "/" + lCity.foodStorageCapacity, -300, lMinusY + 45, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
				break;

			case World.TILE_TYPE_FARM:
				FarmRegion lFarm = (FarmRegion) mSelectedRegion;
				lFont.draw("Food: " + lFarm.storage + "/" + lFarm.storageCapacity, -300, lMinusY + 25, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
				break;

			case World.TILE_TYPE_MINE:
				MineRegion lMine = (MineRegion) mSelectedRegion;
				lFont.draw("Metal: " + lMine.storage + "/" + lMine.storageCapacity, -300, lMinusY + 25, -0.02f, 0.85f, 0.91f, 0.88f, 1f, lTextScale, -1);
				break;

			}

			lFont.end();

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void clearSelected() {

	}

	private boolean handleHUDInput(LintfordCore pCore) {
		if (mDeleteNodeButtonArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			if (mSelectedNode != null && mSelectedNode.nodeType() != WorldNode.NODE_TYPE_HQ) {
				if (pCore.input().isMouseTimedLeftClickAvailable()) {
					// TODO: Tell Node to send all resources back to HQ

					mNodeController.removeNode(mSelectedNode, true);

					pCore.input().setLeftMouseClickHandled();
					mSelectedNode = null;

				}

			}

		}

		if (mNodeNormalButtonArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mMouseOverText = "Road junction";
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (!mMouseController.isBuilding && mMouseController.tempWorldNode == null) {
					WorldNode lNewNode = new WorldNode();

					lNewNode.setNodeType(WorldNode.NODE_TYPE_NORMAL);

					pCore.input().setLeftMouseClickHandled();

					mMouseController.isBuilding = true;
					mMouseController.tempWorldNode = lNewNode;

					return true;
				}
			}
		}

		if (mNodeLongButtonArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mMouseOverText = "Long Node";
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (!mMouseController.isBuilding && mMouseController.tempWorldNode == null) {
					WorldNode lNewNode = new WorldNode();

					lNewNode.setNodeType(WorldNode.NODE_TYPE_LONG);

					pCore.input().setLeftMouseClickHandled();

					mMouseController.isBuilding = true;
					mMouseController.tempWorldNode = lNewNode;

					return true;

				}

			}
		}

		if (mNodeStorageButtonArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mMouseOverText = "Storage Node";
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (!mMouseController.isBuilding && mMouseController.tempWorldNode == null) {
					WorldNode lNewNode = new WorldNode();

					lNewNode.setNodeType(WorldNode.NODE_TYPE_STORAGE);

					pCore.input().setLeftMouseClickHandled();

					mMouseController.isBuilding = true;
					mMouseController.tempWorldNode = lNewNode;

					return true;

				}
			}

		}

		// Build pillbox
		if (mPillBoxButtonArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mMouseOverText = "Pillbox Node";
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (!mMouseController.isBuilding && mMouseController.tempWorldNode == null) {
					WorldNode lNewNode = new WorldNode();

					lNewNode.setNodeType(WorldNode.NODE_TYPE_PILLBOX);

					pCore.input().setLeftMouseClickHandled();

					mMouseController.isBuilding = true;
					mMouseController.tempWorldNode = lNewNode;

					return true;

				}
			}

		}

		// Build turret
		if (mTurretButtonArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mMouseOverText = "Turret Node";
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (!mMouseController.isBuilding && mMouseController.tempWorldNode == null) {
					WorldNode lNewNode = new WorldNode();

					lNewNode.setNodeType(WorldNode.NODE_TYPE_TURRET);

					pCore.input().setLeftMouseClickHandled();

					mMouseController.isBuilding = true;
					mMouseController.tempWorldNode = lNewNode;

					return true;

				}
			}

		}

		if (mMortarButtonArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mMouseOverText = "Mortar Node";
			if (pCore.input().isMouseTimedLeftClickAvailable()) {

				if (!mMouseController.isBuilding && mMouseController.tempWorldNode == null) {
					WorldNode lNewNode = new WorldNode();

					lNewNode.setNodeType(WorldNode.NODE_TYPE_MORTAR);

					pCore.input().setLeftMouseClickHandled();

					mMouseController.isBuilding = true;
					mMouseController.tempWorldNode = lNewNode;

					return true;

				}

			}

		}

		return false;

	}

	private boolean handleWorldInput(LintfordCore pCore) {

		if (pCore.input().isMouseTimedLeftClickAvailable()) {
			float lMouseX = pCore.gameCamera().getMouseWorldSpaceX();
			float lMouseY = pCore.gameCamera().getMouseWorldSpaceY();

			int lSelectedWorldTile = mWorldController.gameWorld().world().getTileFromWorldPosition(lMouseX, lMouseY);

			// First check to see if there is a node here:
			mSelectedNode = mWorldController.gameWorld().getNodeByTileIndex(lSelectedWorldTile);

			// Check to see if there is a region below the node
			mSelectedRegion = mWorldController.gameWorld().world().getRegionByTileindex(lSelectedWorldTile);

			// pCore.input().setLeftMouseClickHandled();

			return true;

		}

		return false;

	}

}
