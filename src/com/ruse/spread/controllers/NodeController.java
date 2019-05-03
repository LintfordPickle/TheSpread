package com.ruse.spread.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.ruse.spread.GameConstants;
import com.ruse.spread.data.GameWorld;
import com.ruse.spread.data.GameWorld.PathingNode;
import com.ruse.spread.data.regions.CityRegion;
import com.ruse.spread.data.regions.FarmRegion;
import com.ruse.spread.data.regions.MineRegion;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldEdge;
import com.ruse.spread.data.world.WorldPackage;
import com.ruse.spread.data.world.WorldPackage.PACKAGETYPE;
import com.ruse.spread.data.world.WorldRegion;
import com.ruse.spread.data.world.nodes.WorldNode;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;

public class NodeController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "NodeController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldController mWorldController;
	private GameStateController mGameStateController;
	private ParticleController mProjectileController;

	private List<WorldPackage> mPackageUpdateList = new ArrayList<>();
	private List<WorldNode> mNodeUpdateList = new ArrayList<>();
	private List<WorldEdge> mTempEdgeList = new ArrayList<>();

	private Vector2f mTempVector = new Vector2f();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialised() {
		return mWorldController != null;
	}

	public WorldNode getNodeAt(int pTileIndex) {
		List<WorldNode> lNodes = mWorldController.gameWorld().nodes();
		final int lNodeCount = lNodes.size();
		for (int i = 0; i < lNodeCount; i++) {
			if (lNodes.get(i).tileIndex == pTileIndex) {
				return lNodes.get(i);

			}

		}

		return null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public NodeController(ControllerManager pControllerManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, entityGroupID());
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());
		mProjectileController = (ParticleController) lControllerManager.getControllerByNameRequired(ParticleController.CONTROLLER_NAME, entityGroupID());

	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		GameWorld lGameWorld = mWorldController.gameWorld();
		List<WorldNode> lNodes = lGameWorld.nodes();

		// PACKAGES
		mPackageUpdateList.clear();
		final int lNumPackages = lGameWorld.packages().size();
		for (int i = 0; i < lNumPackages; i++) {
			mPackageUpdateList.add(lGameWorld.packages().get(i));

		}

		for (int i = 0; i < lNumPackages; i++) {
			WorldPackage lPackage = mPackageUpdateList.get(i);

			updatePackage(pCore, lPackage);

			if (lPackage.isFree()) {
				lGameWorld.returnPackage(lPackage);

			}

		}

		// NODES
		mNodeUpdateList.clear();
		final int lNumNodes = lNodes.size();
		for (int i = 0; i < lNumNodes; i++) {
			mNodeUpdateList.add(lNodes.get(i));

		}

		for (int i = 0; i < lNumNodes; i++) {
			WorldNode lNode = mNodeUpdateList.get(i);

			if (lNode.health <= 0) {
				removeNode(lNode, false);

			} else
				updateNode(pCore, lGameWorld, lNode);

		}

	}

	public void updateNode(LintfordCore pCore, GameWorld pGameWorld, WorldNode pNode) {
		if (!pNode.nodeEnabled)
			return;

		pNode.requestPackageTimer += pCore.time().elapseGameTimeMilli();
		pNode.sendPackageTimer += pCore.time().elapseGameTimeMilli();
		pNode.shootTimer += pCore.time().elapseGameTimeMilli();
		pNode.ammoConvertTimer += pCore.time().elapseGameTimeMilli();
		pNode.hqGenerateTimer += pCore.time().elapseGameTimeMilli();

		if (pNode.isConstructed) {
			// Worker timer increases based on number of workers
			float lEffectiveWork = 1.0f;
			if (pNode.storageCapacityPopulation > 0f) {
				lEffectiveWork = (pNode.populationStore / pNode.storageCapacityPopulation);

			}

			pNode.nodeWorkerTimer += pCore.time().elapseGameTimeMilli() * lEffectiveWork;

		}

		// REQUESTED PACKAGES
		mPackageUpdateList.clear();
		final int lNumReqPackages = pNode.packageRequested.size();
		for (int j = 0; j < lNumReqPackages; j++) {
			mPackageUpdateList.add(pNode.packageRequested.get(j));

		}

		for (int j = 0; j < lNumReqPackages; j++) {
			WorldPackage lPackage = mPackageUpdateList.get(j);

			if (lPackage.isFree()) {
				pNode.packageRequested.remove(lPackage);

			}

		}

		// Update Spread Spawn regions
		if (pNode.nodeType() == WorldNode.NODE_TYPE_SPREADER) {
			updateNodeSpreadSpawner(pCore, mWorldController.gameWorld(), pNode);
			return;
		}

		// NODE
		if (pNode.isConstructed) {
			updateNodeInternal(pCore, mWorldController.gameWorld(), pNode);

		} else {
			updateNodeConstruction(pCore, mWorldController.gameWorld(), pNode);

		}

		if (pNode.nodeType() == WorldNode.NODE_TYPE_PILLBOX || pNode.nodeType() == WorldNode.NODE_TYPE_TURRET || pNode.nodeType() == WorldNode.NODE_TYPE_MORTAR) {
			if (pNode.isConstructed && pNode.populationStore > 0) {

				if (pNode.ammoStore < pNode.storageCapacityAmmo && pNode.metalStore > 0) {
					if (pNode.ammoConvertTimer > pNode.reloadCooldownTime) {
						pNode.metalStore--;

						pNode.ammoStore += pNode.ammoPerMetal;

						pNode.ammoConvertTimer = 0;

					}

				}

				if (pNode.nodeType() == WorldNode.NODE_TYPE_PILLBOX) {
					shootLOS(pNode, pGameWorld);

				}

				if (pNode.nodeType() == WorldNode.NODE_TYPE_TURRET) {
					shootTurret(pNode, pGameWorld);

				}

				if (pNode.nodeType() == WorldNode.NODE_TYPE_MORTAR) {
					shootMortar(pNode, pGameWorld);

				}

			}

		}

	}

	private int getSpreadWithinRange(int pTileCoord, int pRange) {
		int lTileX = pTileCoord % GameConstants.WIDTH;
		int lTileY = pTileCoord / GameConstants.WIDTH;

		int lHalfRange = pRange;

		int[] lSpreadPopulation = mWorldController.gameWorld().world().spreadPopulation;

		float lShortestDist = Float.MAX_VALUE;
		int lFoundIndex = -1;

		float lOriginWorldX = mWorldController.gameWorld().world().getWorldPositionX(pTileCoord);
		float lOriginWorldY = mWorldController.gameWorld().world().getWorldPositionY(pTileCoord);

		for (int y = lTileY - lHalfRange; y < lTileY + lHalfRange; y++) {
			for (int x = lTileX - lHalfRange; x < lTileX + lHalfRange; x++) {
				if (x < 0 || x >= GameConstants.WIDTH)
					continue;
				if (y < 0 || y >= GameConstants.HEIGHT)
					continue;
				final int lTileIndex = y * GameConstants.WIDTH + x;

				// bounds check
				if (lTileIndex < 0 || lTileIndex > GameConstants.WIDTH * GameConstants.HEIGHT - 1)
					continue;

				float lTargetWorldX = mWorldController.gameWorld().world().getWorldPositionX(lTileIndex);
				float lTargetWorldY = mWorldController.gameWorld().world().getWorldPositionY(lTileIndex);

				if (lSpreadPopulation[lTileIndex] > 0) {

					float dist2 = Vector2f.distance2(lOriginWorldX, lOriginWorldY, lTargetWorldX, lTargetWorldY);

					if (dist2 < lShortestDist) {
						lShortestDist = dist2;
						lFoundIndex = lTileIndex;

					}

				}

			}
		}

		return lFoundIndex; // nowt
	}

	private void updateNodeConstruction(LintfordCore pCore, GameWorld pGameWorld, WorldNode pNode) {
		final float REQUEST_PACKAGE_COOLDOWN = 750;
		if (!pNode.isConstructed && pNode.requestPackageTimer > REQUEST_PACKAGE_COOLDOWN) {
			boolean lCanConstruct = true;
			pNode.requestPackageTimer = 0;
			int lFulfillerHash = -1;

			if (pNode.neededFood > 0) {
				int lRequestFood = howManyRequests(pNode, PACKAGETYPE.food);

				if (lRequestFood < pNode.neededFood) {
					lFulfillerHash = requestPackage(pGameWorld, pNode, PACKAGETYPE.food, false, lFulfillerHash);
				}

				lCanConstruct = false;
			}

			if (pNode.neededMetals > 0) {
				int lRequestMeetals = howManyRequests(pNode, PACKAGETYPE.metal);

				if (lRequestMeetals < pNode.neededMetals) {
					lFulfillerHash = requestPackage(pGameWorld, pNode, PACKAGETYPE.metal, false, lFulfillerHash);
				}

				lCanConstruct = false;
			}

			if (pNode.neededPop > 0) {
				int lRequestPop = howManyRequests(pNode, PACKAGETYPE.population);

				if (lRequestPop < pNode.neededPop) {
					requestPackage(pGameWorld, pNode, PACKAGETYPE.population, false, lFulfillerHash);
				}

				lCanConstruct = false;
			}

			if (lCanConstruct) {
				pNode.isConstructed = true;

			}

		}

	}

	private void updateNodeSpreadSpawner(LintfordCore pCore, GameWorld pGameWorld, WorldNode pNode) {
		final int lNodeTileIndex = pNode.tileIndex;

		// Bounds checks
		if (lNodeTileIndex < 0 || lNodeTileIndex >= GameConstants.WIDTH * GameConstants.HEIGHT)
			return;

		int[] spreadPop = pGameWorld.world().spreadPopulation;

		int lAmt = GameConstants.BASE_SPREAD_PER_TICK + (int) (mGameStateController.gameState().difficultyLevel * GameConstants.SPREAD_PER_DIFF_MOD);

		// TODO: This should depend on the current difficulty level
		spreadPop[lNodeTileIndex] += lAmt;

		if (spreadPop[lNodeTileIndex] > GameConstants.MAX_SPREAD_PER_TILE)
			spreadPop[lNodeTileIndex] = GameConstants.MAX_SPREAD_PER_TILE;

		return;

	}

	private void updateNodeInternal(LintfordCore pCore, GameWorld pGameWorld, WorldNode pNode) {

		// 1. Check to see if this node can work a region below it
		// Can only receive if placed on a region, or is a HQ
		WorldRegion lRegion = mWorldController.gameWorld().world().getRegionByTileindex(pNode.tileIndex);
		if (lRegion != null) {
			final float GENERATE_RESOURCE_TIME = 2500;
			if (lRegion.type() == World.REGION_TYPE_FARM) {
				// Check if work cycle is complete
				if (pNode.nodeWorkerTimer > GENERATE_RESOURCE_TIME) {

					// First we fil up the node
					if (pNode.foodStore < pNode.storageCapacityFood) {

						pNode.foodStore++;
						pNode.nodeWorkerTimer = 0;

					}

					// Then we fill up the region
					FarmRegion lFarmRegion = (FarmRegion) lRegion;
					if (lFarmRegion.storage < lFarmRegion.storageCapacity) {
						// Work the farm with the people at this node
						lFarmRegion.storage++;
						pNode.nodeWorkerTimer = 0;

					}

				}

			}

			if (lRegion.type() == World.REGION_TYPE_CITY) {
				CityRegion lCityRegion = (CityRegion) lRegion;
				// Check if work cycle is complete
				if (pNode.nodeWorkerTimer > GENERATE_RESOURCE_TIME) {

					// move food from node to region
					if (pNode.foodStore > 0 && lCityRegion.foodStorage < lCityRegion.foodStorageCapacity) {
						pNode.foodStore--;
						lCityRegion.foodStorage++;

						pNode.nodeWorkerTimer = 0;

					}

					// Spawn new people
					if (lCityRegion.foodStorage > 0 && lCityRegion.popStorage < lCityRegion.popStorageCapacity) {
						lCityRegion.popStorage++;
						lCityRegion.foodStorage--;

						pNode.nodeWorkerTimer = 0;

					}

				}

				// Move pop to node if needed
				if (pNode.populationStore < pNode.storageCapacityPopulation) {
					pNode.populationStore++;
					lCityRegion.popStorage--;

					pNode.nodeWorkerTimer = 0;
				}

			}

			if (lRegion.type() == World.REGION_TYPE_MINE) {
				MineRegion lMineRegion = (MineRegion) lRegion;

				// Check if work cycle is complete
				if (pNode.nodeWorkerTimer > GENERATE_RESOURCE_TIME) {
					if (lMineRegion.storage < lMineRegion.storageCapacity) {
						lMineRegion.storage++;

						pNode.nodeWorkerTimer = 0;

					}

					if (lMineRegion.storage > 0 && pNode.metalStore < pNode.storageCapacityMetal) {
						pNode.metalStore++;
						lMineRegion.storage--;

						pNode.nodeWorkerTimer = 0;

					}

				}

			}
		}

		// HQs autoamatically spawn all resource types, at a much slower pace
		if (pNode.nodeType() == WorldNode.NODE_TYPE_HQ) {
			final float GENERATE_RESOURCE_TIME = 3000;
			if (pNode.hqGenerateTimer > GENERATE_RESOURCE_TIME) {
				if (pNode.metalStore < pNode.storageCapacityMetal) {
					pNode.metalStore++;
				}

				pNode.hqGenerateTimer = 0;

			}

		}

		final float REQUEST_PACKAGE_TIME = 1000;
		if (pNode.requestPackageTimer > REQUEST_PACKAGE_TIME) {
			boolean isOnCityRegion = lRegion != null && lRegion.type() == World.REGION_TYPE_CITY;
			boolean isOnFarmRegion = lRegion != null && lRegion.type() == World.REGION_TYPE_FARM;
			boolean isOnMineRegion = lRegion != null && lRegion.type() == World.REGION_TYPE_MINE;

			int lRequestedFood = howManyRequests(pNode, PACKAGETYPE.food);
			if (!isOnFarmRegion && lRequestedFood + pNode.foodStore < pNode.storageCapacityFood) {
				requestPackage(pGameWorld, pNode, PACKAGETYPE.food, pNode.storage, 0x0);
				pNode.requestPackageTimer = 0;

			}

			int lRequestedMetal = howManyRequests(pNode, PACKAGETYPE.metal);
			if (!isOnMineRegion && lRequestedMetal + pNode.metalStore < pNode.storageCapacityMetal) {
				requestPackage(pGameWorld, pNode, PACKAGETYPE.metal, pNode.storage, 0x0);
				pNode.requestPackageTimer = 0;

			}

			int lRequestedPopulation = howManyRequests(pNode, PACKAGETYPE.population);
			if (!isOnCityRegion && lRequestedPopulation + pNode.populationStore < pNode.storageCapacityPopulation) {
				requestPackage(pGameWorld, pNode, PACKAGETYPE.population, pNode.storage && pNode.populationStore > 0, 0x0);
				pNode.requestPackageTimer = 0;

			}

		}

	}

	public void updatePackage(LintfordCore pCore, WorldPackage pPackage) {

		if (pPackage.isMoving) {
			if (pPackage.moveList.size() == 0) {
				// already at destination ??

			}

			// Check if the package is at the destination
			WorldNode lNextNode = pPackage.moveList.get(0);

			if (lNextNode == null) { // node destroyed ?
				pPackage.kill();
				return;
			}

			if (pPackage.mBounds.intersectsAA(lNextNode.mBounds.centerX(), lNextNode.mBounds.centerY())) {
				// We are at the destination
				pPackage.moveList.remove(lNextNode);

				if (pPackage.moveList.size() == 0) {
					lNextNode.deliverPackage(pPackage);

					// Recycle the package
					pPackage.kill();
					return;

				}

				return;
			}

			// Move the package towards the destination

			mTempVector.x = lNextNode.mBounds.centerX() - pPackage.mBounds.centerX();
			mTempVector.y = lNextNode.mBounds.centerY() - pPackage.mBounds.centerY();

			mTempVector.nor();

			pPackage.mBounds.setCenterPosition(pPackage.mBounds.centerX() + mTempVector.x * GameConstants.PACKAGE_MOVEMENT_SPEED, pPackage.mBounds.centerY() + mTempVector.y * GameConstants.PACKAGE_MOVEMENT_SPEED);

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void shootLOS(WorldNode pNode, GameWorld pGameWorld) {
		if (pNode.ammoStore > 0 && pNode.shootTimer > 500) {
			// 1. Check for spread within range
			int pTargetTile = getSpreadWithinRange(pNode.tileIndex, pNode.rangeInTiles);

			if (pTargetTile != -1) {
				float xTar = pGameWorld.world().getWorldPositionX(pTargetTile) + GameConstants.TILE_SIZE * 0.5f;
				float yTar = pGameWorld.world().getWorldPositionY(pTargetTile) + GameConstants.TILE_SIZE * 0.5f;

				// 2. Shoot projectiles towards the threat
				float xPos = pGameWorld.world().getWorldPositionX(pNode.tileIndex) + GameConstants.TILE_SIZE * 0.5f;
				float yPos = pGameWorld.world().getWorldPositionY(pNode.tileIndex) + GameConstants.TILE_SIZE * 0.5f;

				mTempVector.x = xTar - xPos;
				mTempVector.y = yTar - yPos;
				mTempVector.nor();
				final float PROJ_SPEED = 350;

				float lAngle = (float) (Math.atan2(mTempVector.y, mTempVector.x) + Math.toRadians(RandomNumbers.random(-5, 5)));

				float lVelX = (float) Math.cos(lAngle) * PROJ_SPEED;
				float lVelY = (float) Math.sin(lAngle) * PROJ_SPEED;

				pNode.angle = lAngle;

				mProjectileController.shootProjectile("Pillbox", xPos, yPos, lVelX, lVelY, 1000);
				mProjectileController.shootProjectile("Muzzle", xPos, yPos, 0, 0, 100);

				pNode.ammoStore--;
				pNode.shootTimer = 0;

			}

		}
	}

	public void shootTurret(WorldNode pNode, GameWorld pGameWorld) {
		if (pNode.ammoStore > 0 && pNode.shootTimer > 500) {
			// 1. Check for spread within range
			int pTargetTile = getSpreadWithinRange(pNode.tileIndex, pNode.rangeInTiles);

			if (pTargetTile != -1) {
				float xTar = pGameWorld.world().getWorldPositionX(pTargetTile) + GameConstants.TILE_SIZE * 0.5f;
				float yTar = pGameWorld.world().getWorldPositionY(pTargetTile) + GameConstants.TILE_SIZE * 0.5f;

				// 2. Shoot projectiles towards the threat
				float xPos = pGameWorld.world().getWorldPositionX(pNode.tileIndex) + GameConstants.TILE_SIZE * 0.5f;
				float yPos = pGameWorld.world().getWorldPositionY(pNode.tileIndex) + GameConstants.TILE_SIZE * 0.5f;

				mTempVector.x = xTar - xPos;
				mTempVector.y = yTar - yPos;
				mTempVector.nor();
				final float PROJ_SPEED = 350;

				float lAngle = (float) (Math.atan2(mTempVector.y, mTempVector.x) + Math.toRadians(RandomNumbers.random(-5, 5)));

				float lVelX = (float) Math.cos(lAngle) * PROJ_SPEED;
				float lVelY = (float) Math.sin(lAngle) * PROJ_SPEED;

				pNode.angle = lAngle;

				mProjectileController.shootProjectile("Turret", xPos, yPos, lVelX, lVelY, 1000);
				mProjectileController.shootProjectile("Muzzle", xPos, yPos, 0, 0, 100);

				pNode.ammoStore--;
				pNode.shootTimer = 0;

			}

		}
	}

	public void shootMortar(WorldNode pNode, GameWorld pGameWorld) {
		if (pNode.ammoStore > 0 && pNode.shootTimer > pNode.shootCooldownTime) {
			// 1. Check for spread within range
			int pTargetTile = getSpreadWithinRange(pNode.tileIndex, pNode.rangeInTiles);

			if (pTargetTile != -1) {
				float xTar = pGameWorld.world().getWorldPositionX(pTargetTile) + GameConstants.TILE_SIZE * 0.5f;
				float yTar = pGameWorld.world().getWorldPositionY(pTargetTile) + GameConstants.TILE_SIZE * 0.5f;

				// 2. Shoot projectiles towards the threat
				float xPos = pGameWorld.world().getWorldPositionX(pNode.tileIndex) + GameConstants.TILE_SIZE * 0.5f;
				float yPos = pGameWorld.world().getWorldPositionY(pNode.tileIndex) + GameConstants.TILE_SIZE * 0.5f;

				mTempVector.x = xTar - xPos;
				mTempVector.y = yTar - yPos;
				mTempVector.nor();
				final float PROJ_SPEED = 200;

				float lAngle = (float) (Math.atan2(mTempVector.y, mTempVector.x) + Math.toRadians(RandomNumbers.random(-5, 5)));

				float lVelX = (float) Math.cos(lAngle) * PROJ_SPEED;
				float lVelY = (float) Math.sin(lAngle) * PROJ_SPEED;

				float lDist = Vector2f.distance(xPos, yPos, xTar, yTar);

				Particle p = mProjectileController.shootProjectile("Mortar", xPos, yPos, lVelX, lVelY, lDist * 10);
				mProjectileController.shootProjectile("Muzzle", xPos, yPos, 0, 0, 100);

				if (p != null) {
					pNode.ammoStore--;
					pNode.shootTimer = 0;

				}

			}

		}
	}

	public int howManyRequests(WorldNode pNode, PACKAGETYPE pPackageType) {
		int lReturnAmount = 0;
		final int lNumRequests = pNode.packageRequested.size();
		for (int i = 0; i < lNumRequests; i++) {
			if (pNode.packageRequested.get(i).packageType == pPackageType) {
				lReturnAmount += pNode.packageRequested.get(i).amount;

			}

		}

		return lReturnAmount;

	}

	private int requestPackage(GameWorld pGameWorld, WorldNode pNodeTo, PACKAGETYPE pPackageType, boolean pStorageRequest, int pExcludeHash) {
		WorldPackage lPackage = pGameWorld.getFreePackage();

		pGameWorld.resetPathNodes();
		Queue<PathingNode> pathPriorityQueue = pGameWorld.pathPriorityQueue;
		pathPriorityQueue.clear();

		boolean lFoundEnd = false;

		PathingNode lOurPathingNode = pGameWorld.getPathNode(pNodeTo);
		if (lOurPathingNode == null) {
			pGameWorld.returnPackage(lPackage);

			return -1;

		}

		lOurPathingNode.visited = true;
		lOurPathingNode.aggCost = 0; // start node

		// 1. Add all neightbouring nodes to the priorityQueue
		int lNeighbourSize = pNodeTo.edges.size();
		for (int i = 0; i < lNeighbourSize; i++) {
			WorldEdge lEdge = pNodeTo.edges.get(i);
			WorldNode lNextNode = lEdge.getOtherNode(pNodeTo);

			PathingNode lPathNode = pGameWorld.getPathNode(lNextNode);

			if (lPathNode == lOurPathingNode)
				continue;

			if (lPathNode == null)
				return -1;

			lPathNode.aggCost = lEdge.dist;
			lPathNode.prevNode = lOurPathingNode;
			lPathNode.visited = true;

			if (lPathNode.node.isConstructed)
				pathPriorityQueue.add(lPathNode);

		}

		PathingNode lPathNode = pathPriorityQueue.poll();
		while (!lFoundEnd && lPathNode != null) {

			// If it is a storage request and we are a storage unit, then don't move the resources around
			boolean fulfilStorageRequest = !(pStorageRequest && lPathNode.node.storage);

			// Check the node for stored packages
			int lFulfillerHash = lPathNode.node.hashCode();
			if (fulfilStorageRequest && lFulfillerHash != pExcludeHash) {
				boolean lFound = false;

				if (lPathNode.node.nodeEnabled && lPathNode.node.provider && lPathNode.node.hasStoresEnough(pPackageType, 1, true)) {
					lPackage.amount = 1;
					lPackage.packageType = pPackageType;

					if (pPackageType == PACKAGETYPE.population) {
						System.out.printf("Sending food (Node) from %d (%d) to %d (%d)\n", lPathNode.node.hashCode(), lPathNode.node.nodeType(), pNodeTo.hashCode(), pNodeTo.nodeType());

					}

					lFound = true;

				} else {
					// else check the underlying region for package (not all nodes have storage
					WorldRegion lRegion = mWorldController.gameWorld().world().getRegionByTileindex(lPathNode.node.tileIndex);
					if (lRegion != null && lRegion.canFillPackage(pPackageType, 1)) {
						lRegion.fillPackage(lPackage);

						if (pPackageType == PACKAGETYPE.population) {
							System.out.printf("Sending food (Region) from %d (%d) to %d (%d)\n", lPathNode.node.hashCode(), lPathNode.node.nodeType(), pNodeTo.hashCode(), pNodeTo.nodeType());

						}

						lFound = true;

					}

				}

				if (lFound) {
					// Return path to this node
					lFoundEnd = true;

					pNodeTo.packageRequested.add(lPackage);

					// Unpack the path and add it to the WorldPackage
					PathingNode lN = lPathNode;

					// Set the initial position
					lPackage.isMoving = true;

					float xPos = pGameWorld.world().getWorldPositionX(lN.node.tileIndex);
					float yPos = pGameWorld.world().getWorldPositionY(lN.node.tileIndex);
					lPackage.mBounds.setCenterPosition(xPos + GameConstants.TILE_SIZE * 0.5f, yPos + GameConstants.TILE_SIZE * 0.5f);

					while (lN != null) {
						lPackage.moveList.add(lN.node);

						lN = lN.prevNode;
					}

					return lFulfillerHash;
				}

			}

			// Add the next nodes neighbours to the priorityQueue
			lNeighbourSize = lPathNode.node.edges.size();
			for (int i = 0; i < lNeighbourSize; i++) {
				WorldEdge lEdge = lPathNode.node.edges.get(i);
				WorldNode lNextNode = lEdge.getOtherNode(lPathNode.node);

				PathingNode lNextPathNode = pGameWorld.getPathNode(lNextNode);

				float lPathAggCost = lPathNode.aggCost + lEdge.dist;

				if (lPathAggCost < lNextPathNode.aggCost) {
					lNextPathNode.aggCost = lPathAggCost;
					lNextPathNode.prevNode = lPathNode;
				}

				int lType = lPathNode.node.nodeType();
				boolean isLogisticalNode = (lType != WorldNode.NODE_TYPE_PILLBOX && lType != WorldNode.NODE_TYPE_TURRET && lType != WorldNode.NODE_TYPE_MORTAR);

				// Only visit each node once
				if (isLogisticalNode && !lNextPathNode.visited && lNextPathNode.node.isConstructed) {
					pathPriorityQueue.add(lNextPathNode);
					lNextPathNode.visited = true;

				}

			}

			lPathNode = pathPriorityQueue.poll();
		}

		pGameWorld.returnPackage(lPackage);
		return -1;

	}

	// TODO: Incomplete sendPackage function. used when selling buildings to redistribute the items
	private int sendPackage(GameWorld pGameWorld, WorldNode pNodeFrom, WorldNode pNodeTo, PACKAGETYPE pPackageType, int pExcludeHash) {
		WorldPackage lPackage = pGameWorld.getFreePackage();

		pGameWorld.resetPathNodes();
		Queue<PathingNode> pathPriorityQueue = pGameWorld.pathPriorityQueue;
		pathPriorityQueue.clear();

		boolean lFoundEnd = false;

		PathingNode lOurPathingNode = pGameWorld.getPathNode(pNodeTo);
		if (lOurPathingNode == null) {
			pGameWorld.returnPackage(lPackage);

			return -1;

		}

		lOurPathingNode.visited = true;
		lOurPathingNode.aggCost = 0; // start node

		// 1. Add all neightbouring nodes to the priorityQueue
		int lNeighbourSize = pNodeTo.edges.size();
		for (int i = 0; i < lNeighbourSize; i++) {
			WorldEdge lEdge = pNodeTo.edges.get(i);
			WorldNode lNextNode = lEdge.getOtherNode(pNodeTo);

			PathingNode lPathNode = pGameWorld.getPathNode(lNextNode);

			if (lPathNode == null)
				return -1;

			lPathNode.aggCost = lEdge.dist;
			lPathNode.prevNode = lOurPathingNode;
			lPathNode.visited = true;

			if (lPathNode.node.isConstructed)
				pathPriorityQueue.add(lPathNode);

		}

		PathingNode lPathNode = pathPriorityQueue.poll();
		while (!lFoundEnd && lPathNode != null) {

			// Check the node for stored packages
			int lFulfillerHash = lPathNode.node.hashCode();
			if (lFulfillerHash != pExcludeHash) {
				boolean lFound = false;

				if (lPathNode.node.provider && lPathNode.node.hasStoresEnough(pPackageType, 1, true)) {
					lPackage.amount = 1;
					lPackage.packageType = pPackageType;

					lFound = true;

				} else {
					// else check the underlying region for package (not all nodes have storage
					WorldRegion lRegion = mWorldController.gameWorld().world().getRegionByTileindex(lPathNode.node.tileIndex);
					if (lRegion != null && lRegion.canFillPackage(pPackageType, 1)) {
						lRegion.fillPackage(lPackage);

						lFound = true;

					}

				}

				if (lFound) {
					// Return path to this node
					lFoundEnd = true;

					pNodeTo.packageRequested.add(lPackage);

					// Unpack the path and add it to the WorldPackage
					PathingNode lN = lPathNode;

					// Set the initial position
					lPackage.isMoving = true;

					float xPos = pGameWorld.world().getWorldPositionX(lN.node.tileIndex);
					float yPos = pGameWorld.world().getWorldPositionY(lN.node.tileIndex);
					lPackage.mBounds.setCenterPosition(xPos + GameConstants.TILE_SIZE * 0.5f, yPos + GameConstants.TILE_SIZE * 0.5f);

					// System.out.println("Route: (" + pPackageType.toString() + ")");
					while (lN != null) {
						// System.out.println(" " + lN.node.hashCode());
						lPackage.moveList.add(lN.node);

						lN = lN.prevNode;
					}

					return lFulfillerHash;
				}

			}

			// Add the next nodes neighbours to the priorityQueue
			lNeighbourSize = lPathNode.node.edges.size();
			for (int i = 0; i < lNeighbourSize; i++) {
				WorldEdge lEdge = lPathNode.node.edges.get(i);
				WorldNode lNextNode = lEdge.getOtherNode(lPathNode.node);

				PathingNode lNextPathNode = pGameWorld.getPathNode(lNextNode);

				float lPathAggCost = lPathNode.aggCost + lEdge.dist;

				if (lPathAggCost < lNextPathNode.aggCost) {
					lNextPathNode.aggCost = lPathAggCost;
					lNextPathNode.prevNode = lPathNode;
				}

				int lType = lPathNode.node.nodeType();
				boolean isLogisticalNode = (lType != WorldNode.NODE_TYPE_PILLBOX && lType != WorldNode.NODE_TYPE_TURRET && lType != WorldNode.NODE_TYPE_MORTAR);

				// Only visit each node once
				if (isLogisticalNode && !lNextPathNode.visited && lNextPathNode.node.isConstructed) {
					pathPriorityQueue.add(lNextPathNode);
					lNextPathNode.visited = true;

				}

			}

			lPathNode = pathPriorityQueue.poll();
		}

		pGameWorld.returnPackage(lPackage);
		return -1;

	}

	public boolean addNode(WorldNode pNode) {
		final GameWorld lWorld = mWorldController.gameWorld();

		pNode.nodeEnabled = true;

		// TODO: Check if the node can really be placed here
		final int lNodeCount = mWorldController.gameWorld().nodes().size();
		for (int i = 0; i < lNodeCount; i++) {
			if (pNode.tileIndex == mWorldController.gameWorld().nodes().get(i).tileIndex) {
				System.out.printf("Cannot place another node on tile %d, a node already exists here!\n", pNode.tileIndex);
				return false;

			}

		}

		lWorld.addNewWorldNode(pNode);

		connectNodes(pNode);
		return true;

	}

	public void removeNode(WorldNode pNode, boolean pReturnResources) {
		GameWorld lWorld = mWorldController.gameWorld();

		if (lWorld.nodes().contains(pNode)) {

			if (pReturnResources) {
				WorldNode pHQNode = mWorldController.gameWorld().HQNode();

				for (int i = 0; i < pNode.foodStore; i++) {
					sendPackage(lWorld, pNode, pHQNode, PACKAGETYPE.food, -1);
				}
				for (int i = 0; i < pNode.metalStore; i++) {
					sendPackage(lWorld, pNode, pHQNode, PACKAGETYPE.metal, -1);
				}
				for (int i = 0; i < pNode.populationStore; i++) {
					sendPackage(lWorld, pNode, pHQNode, PACKAGETYPE.population, -1);
				}

			}

			// Remove edges
			mTempEdgeList.clear();
			final int lNumEdges = pNode.edges.size();
			for (int i = 0; i < lNumEdges; i++) {
				mTempEdgeList.add(pNode.edges.get(i));

			}

			for (int i = 0; i < lNumEdges; i++) {
				WorldEdge lEdge = mTempEdgeList.get(i);

				lEdge.node1.edges.remove(lEdge);
				lEdge.node2.edges.remove(lEdge);

				lWorld.edges().remove(lEdge);
			}

			if (pNode.nodeType() == WorldNode.NODE_TYPE_HQ) {
				// TODO: Check if the player has any other HQ nodes ..
				mGameStateController.HQDestroyed();

			}

			pNode.resetNode();

			lWorld.removeWorldNode(pNode);

		}

	}

	public void reconnectNodes(WorldNode pNode) {
		final int lNumEdges = pNode.edges.size();
		for (int i = 0; i < lNumEdges; i++) {
			WorldEdge lEdge = pNode.edges.get(i);
			lEdge.node1 = null;
			lEdge.node2 = null;
			lEdge.kill();

		}

		pNode.edges.clear();

		// Rebuild the graph around this node
		connectNodes(pNode);

	}

	public void connectNodes(WorldNode pNode) {
		final GameWorld lWorld = mWorldController.gameWorld();

		float lNodePosX = (pNode.tileIndex % GameConstants.WIDTH) * GameConstants.TILE_SIZE;
		float lNodePosY = (pNode.tileIndex / GameConstants.WIDTH) * GameConstants.TILE_SIZE;

		List<WorldNode> lNeighbouringNodes = new ArrayList<>();

		// Check the proximity to other nodes and add edges
		final int lNodeCount = lWorld.nodes().size();
		for (int i = 0; i < lNodeCount; i++) {
			WorldNode lOtherNode = lWorld.nodes().get(i);

			if (pNode == lOtherNode)
				continue;

			float lONodeX = (lOtherNode.tileIndex % GameConstants.WIDTH) * GameConstants.TILE_SIZE;
			float lONodeY = (lOtherNode.tileIndex / GameConstants.WIDTH) * GameConstants.TILE_SIZE;

			float lMaxDist = Math.max(pNode.maxDistanceBetweenNodes, lOtherNode.maxDistanceBetweenNodes);

			// Check the direct distance to the nodes
			float dist = Vector2f.distance(lNodePosX, lNodePosY, lONodeX, lONodeY);
			if (dist < lMaxDist) {
				// Add an edge
				WorldEdge lNewEdge = new WorldEdge();

				lNewEdge.node1 = pNode;
				lNewEdge.node2 = lOtherNode;
				lNewEdge.dist = Vector2f.distance(lNodePosX, lNodePosY, lONodeX, lONodeY);

				lWorld.edges().add(lNewEdge);

				lOtherNode.edges.add(lNewEdge);
				pNode.edges.add(lNewEdge);

				lNeighbouringNodes.add(lOtherNode);

				System.out.printf("Link created (dist: %f) between %d (%d) and %d (%d)\n", lNewEdge.dist, pNode.hashCode(), pNode.nodeType(), lOtherNode.hashCode(), lOtherNode.nodeType());

			}

		}

		// This doesn't work - but the idea was that sometimes there are connections placed between nodes which are
		// just redundent due to the direct distance.
		boolean cleanNodes = false;
		if (cleanNodes) {
			// After we have created all the nodes, loop through all the neighbouring nodes and measure the distance.
			// Then try to get the distance via the graph edges - for any where the graph is faster, then

			final int lTotNodeCount = lWorld.nodes().size();
			for (int i = 0; i < lTotNodeCount; i++) {
				WorldNode lOtherNode = lWorld.nodes().get(i);

				if (lOtherNode == pNode)
					continue;

				float lONodeX = (lOtherNode.tileIndex % GameConstants.WIDTH) * GameConstants.TILE_SIZE;
				float lONodeY = (lOtherNode.tileIndex / GameConstants.WIDTH) * GameConstants.TILE_SIZE;
				float lDirectDistance = Vector2f.distance(lNodePosX, lNodePosY, lONodeX, lONodeY);

				PathingNode lGraphPath = distAlongGraph(lWorld, pNode, lOtherNode);
				if (lGraphPath != null) {
					float lGraphDistance = lGraphPath.aggCost;
					int lGraphLinkCount = lGraphPath.aggLinkCount;

					final float lTolerance = 95f;
					if (lGraphDistance != -1 && lGraphLinkCount > 0 && lGraphDistance < lDirectDistance + lTolerance) {
						System.out.println("remove this link - not effective enough");
						int lFoundIndex = -1;
						final int lLinkCount = lOtherNode.edges.size();
						for (int j = 0; j < lLinkCount; j++) {
							WorldEdge lLink = lOtherNode.edges.get(j);
							if (lLink.isLinkBetween(lOtherNode, pNode)) {
								lFoundIndex = j;
								break;

							}

						}

						if (lFoundIndex != -1) {
							WorldEdge lRemoveEdge = lOtherNode.edges.get(lFoundIndex);
							lRemoveEdge.removeEdgeFromNodes();

							lWorld.edges().remove(lRemoveEdge);

						}

					}

				}

			}

		}

	}

	public boolean checkViability(WorldNode pNode) {
		return true;

	}

	private PathingNode distAlongGraph(GameWorld pGameWorld, WorldNode pNodeFrom, WorldNode pNodeTo) {
		pGameWorld.resetPathNodes();

		Queue<PathingNode> pathPriorityQueue = pGameWorld.pathPriorityQueue;
		pathPriorityQueue.clear();

		boolean lFoundEnd = false;

		PathingNode lOurPathingNode = pGameWorld.getPathNode(pNodeTo);
		if (lOurPathingNode == null) {
			return null;

		}

		lOurPathingNode.visited = true;
		lOurPathingNode.aggCost = 0; // start node
		lOurPathingNode.aggLinkCount = 0;

		// 1. Add all neighbouring nodes to the priorityQueue
		int lNeighbourSize = pNodeTo.edges.size();
		for (int i = 0; i < lNeighbourSize; i++) {
			WorldEdge lEdge = pNodeTo.edges.get(i);
			WorldNode lNextNode = lEdge.getOtherNode(pNodeTo);

			PathingNode lPathNode = pGameWorld.getPathNode(lNextNode);

			if (lPathNode == null)
				return null;

			lPathNode.aggCost = lEdge.dist;
			lOurPathingNode.aggLinkCount++;
			lPathNode.prevNode = lOurPathingNode;
			lPathNode.visited = true;

			pathPriorityQueue.add(lPathNode);

		}

		PathingNode lPathNode = pathPriorityQueue.poll();
		while (!lFoundEnd && lPathNode != null) {
			if (lPathNode.node == pNodeFrom) { // Back at teh start ?
				return lPathNode;
			}

			// Add the next nodes neighbours to the priorityQueue
			lNeighbourSize = lPathNode.node.edges.size();
			for (int i = 0; i < lNeighbourSize; i++) {
				WorldEdge lEdge = lPathNode.node.edges.get(i);
				WorldNode lNextNode = lEdge.getOtherNode(lPathNode.node);

				PathingNode lNextPathNode = pGameWorld.getPathNode(lNextNode);

				float lPathAggCost = lPathNode.aggCost + lEdge.dist;

				if (lPathAggCost < lNextPathNode.aggCost) {
					lNextPathNode.aggCost = lPathAggCost;
					lOurPathingNode.aggLinkCount++;
					lNextPathNode.prevNode = lPathNode;
				}

				int lType = lPathNode.node.nodeType();
				boolean isLogisticalNode = (lType != WorldNode.NODE_TYPE_PILLBOX && lType != WorldNode.NODE_TYPE_TURRET && lType != WorldNode.NODE_TYPE_MORTAR);

				// Only visit each node once
				if (isLogisticalNode && !lNextPathNode.visited && lNextPathNode.node.isConstructed) {
					pathPriorityQueue.add(lNextPathNode);
					lNextPathNode.visited = true;

				}

			}

			lPathNode = pathPriorityQueue.poll();
		}

		return null;

	}

}
