package com.ruse.spread.data.world;

import java.util.ArrayList;
import java.util.List;

import com.ruse.spread.data.PooledInstanceData;
import com.ruse.spread.data.world.WorldPackage.PACKAGETYPE;

import net.lintford.library.core.geometry.Rectangle;

public class WorldNode extends PooledInstanceData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 5501121323802766922L;

	public static final int NODE_TYPE_NONE = 0b00000000;
	public static final int NODE_TYPE_HQ = 0b00000001;

	// Nodes
	public static final int NODE_TYPE_NORMAL = 0b00000010;
	public static final int NODE_TYPE_LONG = 0b00001000;
	public static final int NODE_TYPE_STORAGE = 0b00010000;

	// Military Nodes
	public static final int NODE_TYPE_PILLBOX = 0b00100000;
	public static final int NODE_TYPE_TURRET = 0b01000000;
	public static final int NODE_TYPE_MORTAR = 0b10000000;

	public static final int MAX_HEALTH = 100;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public float maxDistanceBetweenNodes;
	public int tileIndex;
	private int nodeType;
	public boolean isConstructed;

	public String name;

	public int neededPop;
	public int neededFood;
	public int neededMetals;
	public Rectangle mBounds = new Rectangle();

	public int storageCapacityFood = 0;
	public int storageCapacityMetal = 0;
	public int storageCapacityPopulation = 0;
	public int storageCapacityAmmo = 0;

	public int foodStore;
	public int populationStore;
	public int metalStore;
	public int ammoStore;

	// Military
	public int ammoPerMetal; // number of shots gained per metal resource
	public int shotDamage; // damage per shot
	public int rangeInTiles; // The distance the military units can shoot
	public int shootCooldownTime; // Time between shots
	public int reloadCooldownTime; // Time between converting metals to bullets

	public int maintainMinimumPop;

	public int health;

	public boolean provider;
	public boolean storage;
	public boolean isUnderCaution;

	public List<WorldEdge> edges = new ArrayList<>();
	public List<WorldPackage> packageRequested = new ArrayList<>(10);

	public float sendPackageTimer;
	public float requestPackageTimer;
	public float ammoConvertTimer;
	public float shootTimer;
	public float nodeWorkerTimer;
	public float hqGenerateTimer;

	public boolean nodeEnabled;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int nodeType() {
		return nodeType;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public WorldNode() {
		mBounds.set(0, 0, World.TILE_SIZE, World.TILE_SIZE);
		name = "empty";
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void resetNode() {
		maxDistanceBetweenNodes = 0;
		storageCapacityFood = 0;
		storageCapacityMetal = 0;
		storageCapacityPopulation = 0;
		storageCapacityAmmo = 0;
		provider = true;
		health = 0;
		isConstructed = false;

		neededPop = 0;
		neededFood = 0;
		neededMetals = 0;

		nodeEnabled = false;
	}

	public void setupNewNode(int pMetalNeeded, int pFoodNeeded, int pPopNeeded) {
		isConstructed = false;
		nodeEnabled = true;
		neededMetals = pMetalNeeded;
		neededFood = pFoodNeeded;
		neededPop = pPopNeeded;

	}

	public boolean hasStoresEnough(PACKAGETYPE pType, int pAmt, boolean pReduceAmt) {
		switch (pType) {
		case food:
			if (foodStore >= pAmt) {
				if (pReduceAmt)
					foodStore -= pAmt;
				if (foodStore < 0)
					foodStore = 0;
				return true;
			}

			break;

		case metal:
			if (metalStore >= pAmt) {
				if (pReduceAmt)
					metalStore -= pAmt;
				if (metalStore < 0)
					metalStore = 0;
				return true;
			}

			break;

		case population:
			if (populationStore >= pAmt && populationStore > maintainMinimumPop) {
				if (pReduceAmt)
					populationStore -= pAmt;

				if (populationStore < 0)
					populationStore = 0;
				return true;
			}

			break;

		default:
		}

		return false;
	}

	public void deliverPackage(WorldPackage pPackage) {
		if (pPackage == null)
			return;

		if (pPackage.packageType == PACKAGETYPE.metal) {
			if (neededMetals > 0) {
				neededMetals -= pPackage.amount;
				return;
			}

			if (isConstructed && metalStore < storageCapacityMetal) {
				metalStore += pPackage.amount;
				return;
			}

		}

		if (pPackage.packageType == PACKAGETYPE.food) {
			if (neededFood > 0) {
				neededFood -= pPackage.amount;
				return;
			}
			if (isConstructed && foodStore < storageCapacityFood) {
				foodStore += pPackage.amount;
				return;
			}

		}

		if (pPackage.packageType == PACKAGETYPE.population) {
			if (neededPop > 0) {
				neededPop -= pPackage.amount;
				return;
			}

			if (isConstructed && populationStore < storageCapacityPopulation) {
				populationStore += pPackage.amount;
				return;
			}
		}

	}

	public void setNodeType(int pNodeType) {
		nodeType = pNodeType;
		switch (nodeType) {
		case NODE_TYPE_HQ:
			name = "HQ";
			maxDistanceBetweenNodes = World.TILE_SIZE * 3;
			storageCapacityFood = 30;
			storageCapacityMetal = 30;
			storageCapacityPopulation = 30;
			maintainMinimumPop = 2;
			storageCapacityAmmo = 0;
			ammoPerMetal = 0;
			provider = true;
			storage = true;
			health = MAX_HEALTH;

			neededPop = 0;
			neededFood = 0;
			neededMetals = 0;
			break;

		case NODE_TYPE_NORMAL:
			name = "Node";
			maxDistanceBetweenNodes = World.TILE_SIZE * 3;
			storageCapacityFood = 2;
			storageCapacityMetal = 2;
			storageCapacityPopulation = 2;
			maintainMinimumPop = 1;
			storageCapacityAmmo = 0;
			ammoPerMetal = 0;
			provider = false;
			storage = false;
			health = MAX_HEALTH;

			neededPop = 0;
			neededFood = 0;
			neededMetals = 10;
			break;

		case NODE_TYPE_LONG:
			name = "Long Node";
			maxDistanceBetweenNodes = World.TILE_SIZE * 5;
			storageCapacityFood = 2;
			storageCapacityMetal = 2;
			storageCapacityPopulation = 2;
			maintainMinimumPop = 1;
			storageCapacityAmmo = 0;
			storageCapacityAmmo = 0;
			ammoPerMetal = 0;
			provider = false;
			storage = false;
			health = MAX_HEALTH;

			neededPop = 0;
			neededFood = 0;
			neededMetals = 25;
			break;

		case NODE_TYPE_STORAGE:
			name = "Storage Node";
			maxDistanceBetweenNodes = World.TILE_SIZE * 3;
			storageCapacityFood = 20;
			storageCapacityMetal = 20;
			storageCapacityPopulation = 7;
			maintainMinimumPop = 2;
			storageCapacityAmmo = 0;
			ammoPerMetal = 0;
			provider = true;
			storage = true;
			health = MAX_HEALTH;

			neededPop = 0;
			neededFood = 0;
			neededMetals = 25;
			break;

		case NODE_TYPE_PILLBOX:
			name = "Pillbox";
			maxDistanceBetweenNodes = World.TILE_SIZE * 3;
			storageCapacityFood = 5;
			storageCapacityMetal = 5;
			storageCapacityPopulation = 5;
			maintainMinimumPop = 5;
			storageCapacityAmmo = 5;
			ammoPerMetal = 15;
			rangeInTiles = 5;
			shootCooldownTime = 65;
			reloadCooldownTime = 400;
			shotDamage = 50;
			provider = false;
			storage = false;
			health = MAX_HEALTH;

			neededPop = 0;
			neededFood = 0;
			neededMetals = 15;
			break;

		case NODE_TYPE_TURRET:
			name = "Turret";
			maxDistanceBetweenNodes = World.TILE_SIZE * 3;
			storageCapacityFood = 0;
			storageCapacityMetal = 10;
			storageCapacityPopulation = 4;
			maintainMinimumPop = 4;
			storageCapacityAmmo = 5;
			ammoPerMetal = 7;
			rangeInTiles = 7;
			shootCooldownTime = 105;
			reloadCooldownTime = 400;
			shotDamage = 90;
			provider = false;
			storage = false;
			health = MAX_HEALTH;

			neededPop = 0;
			neededFood = 0;
			neededMetals = 15;
			break;

		case NODE_TYPE_MORTAR:
			name = "Mortar";
			maxDistanceBetweenNodes = World.TILE_SIZE * 3;
			storageCapacityFood = 0;
			storageCapacityMetal = 10;
			storageCapacityPopulation = 6;
			maintainMinimumPop = 6;
			storageCapacityAmmo = 5;
			ammoPerMetal = 4;
			rangeInTiles = 11;
			shootCooldownTime = 1500;
			reloadCooldownTime = 800;
			shotDamage = 250;
			provider = false;
			storage = false;
			health = MAX_HEALTH;

			neededPop = 0;
			neededFood = 0;
			neededMetals = 15;
			break;

		}
	}

	public void takeDamage(int pAmt) {
		health -= pAmt;
		if (health <= 0) {
			if (populationStore > 0) {
				populationStore--;
				health = MAX_HEALTH;

			}
		}

	}

}
