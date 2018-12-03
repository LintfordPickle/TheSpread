package com.ruse.spread.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.ruse.spread.data.projectile.ProjectileManager;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldEdge;
import com.ruse.spread.data.world.WorldNode;
import com.ruse.spread.data.world.WorldPackage;

import net.lintford.library.data.BaseData;

public class GameWorld extends BaseData {

	public class PathCostComparator implements Comparator<PathingNode> {
		@Override
		public int compare(PathingNode x, PathingNode y) {
			if (x.aggCost < y.aggCost) {
				return -1;
			}
			if (x.aggCost > y.aggCost) {
				return 1;
			}
			return 0;
		}
	}

	public class PathingNode {
		public final WorldNode node;
		public boolean visited;
		public float currentCost;
		public float aggCost;
		public PathingNode prevNode;

		public PathingNode(WorldNode pNode) {
			node = pNode;
		}

		public void reset() {
			currentCost = Integer.MAX_VALUE;
			aggCost = Integer.MAX_VALUE;
			prevNode = null;
			visited = false;
		}

	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -4942760447159744932L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private World mWorld;
	private GameState mGameState;
	private ProjectileManager mProjectileManager;

	public Queue<PathingNode> pathPriorityQueue = new PriorityQueue<>(64, new PathCostComparator());
	public List<PathingNode> pathNodes = new ArrayList<>();

	// Instances
	private List<WorldNode> mNodeInstances = new ArrayList<>();
	private List<WorldEdge> mEdgeInstances = new ArrayList<>();

	private List<WorldPackage> mPackagePool = new ArrayList<>();
	private List<WorldPackage> mPackageInstances = new ArrayList<>();

	private WorldNode mHQNode;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public WorldNode HQNode() {
		return mHQNode;
	}

	public World world() {
		return mWorld;
	}

	public GameState gameState() {
		return mGameState;
	}

	public ProjectileManager projectileManager() {
		return mProjectileManager;
	}

	public List<WorldEdge> edges() {
		return mEdgeInstances;
	}

	public List<WorldNode> nodes() {
		return mNodeInstances;
	}

	public List<WorldPackage> packages() {
		return mPackageInstances;

	}

	public WorldNode getNodeByTileIndex(int pTileIndex) {
		final int lNumTotalNodes = mNodeInstances.size();
		for (int i = 0; i < lNumTotalNodes; i++) {
			if (mNodeInstances.get(i).tileIndex == pTileIndex)
				return mNodeInstances.get(i);

		}

		return null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameWorld() {
		mWorld = new World();
		mGameState = new GameState();
		mProjectileManager = new ProjectileManager();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void generateNewWorld() {
		mNodeInstances.clear();
		mEdgeInstances.clear();
		mPackageInstances.clear();
		mPackagePool.clear();
		mProjectileManager.startNewGame();

		assignPackagesToPool(200);

		mWorld.generateNewWorld();

		createHQ();

		mGameState.startNewGame();

	}

	private void createHQ() {
		mHQNode = new WorldNode();

		mHQNode.setNodeType(WorldNode.NODE_TYPE_HQ);
		mHQNode.tileIndex = mWorld.mHQTileIndex;

		mHQNode.foodStore = mHQNode.storageCapacityFood;
		mHQNode.populationStore = mHQNode.storageCapacityPopulation;
		mHQNode.metalStore = mHQNode.storageCapacityMetal;

		addNewWorldNode(mHQNode);

	}

	public void loadFromFile(String pFilename) {

	}

	public void saveToFile(String pFilename) {

	}

	public WorldPackage getFreePackage() {
		final int POOL_SIZE = mPackagePool.size();
		for (int i = 0; i < POOL_SIZE; i++) {
			if (mPackagePool.get(i).isFree()) {
				WorldPackage lPackage = mPackagePool.get(i);

				lPackage.init();

				mPackagePool.remove(lPackage);
				mPackageInstances.add(lPackage);

				return lPackage;

			}

		}

		return assignPackagesToPool(64);
	}

	public void returnPackage(WorldPackage pPackage) {
		if (pPackage == null)
			return;

		pPackage.kill();

		mPackageInstances.remove(pPackage);
		mPackagePool.add(pPackage);

	}

	public PathingNode getPathNode(WorldNode pNode) {
		PathingNode lFoundNode = null;
		final int lNumTotalNodes = pathNodes.size();
		for (int i = 0; i < lNumTotalNodes; i++) {
			if (pathNodes.get(i).node.equals(pNode)) {
				lFoundNode = pathNodes.get(i);

			}

		}

		return lFoundNode;
	}

	public void addNewWorldNode(WorldNode pNewNode) {
		if (mNodeInstances.add(pNewNode)) {
			addNewPathNode(pNewNode);

		}

		float xPos = mWorld.getWorldPositionX(pNewNode.tileIndex);
		float yPos = mWorld.getWorldPositionY(pNewNode.tileIndex);

		pNewNode.mBounds.set(xPos, yPos, World.TILE_SIZE, World.TILE_SIZE);

	}

	private void addNewPathNode(WorldNode pNewNode) {
		PathingNode lFoundNode = null;
		final int lNumTotalNodes = pathNodes.size();
		for (int i = 0; i < lNumTotalNodes; i++) {
			if (pathNodes.get(i).node.equals(pNewNode)) {
				lFoundNode = pathNodes.get(i);
				return; // no need to re-add

			}

		}

		if (lFoundNode == null) {
			pathNodes.add(new PathingNode(pNewNode));

		}

	}

	public void resetPathNodes() {
		final int lNumTotalNodes = pathNodes.size();
		for (int i = 0; i < lNumTotalNodes; i++) {
			pathNodes.get(i).reset();

		}

	}

	public void removeWorldNode(WorldNode pNewNode) {
		if (pNewNode == null)
			return;

		if (mNodeInstances.remove(pNewNode)) {
			removeNewPathNode(pNewNode);

		}

	}

	private void removeNewPathNode(WorldNode pNewNode) {
		PathingNode lFoundNode = null;
		final int lNumTotalNodes = pathNodes.size();
		for (int i = 0; i < lNumTotalNodes; i++) {
			if (pathNodes.get(i).node.equals(pNewNode)) {
				lFoundNode = pathNodes.get(i);
				break;

			}

		}

		if (lFoundNode != null) {
			pathNodes.remove(lFoundNode);

		}

	}

	private WorldPackage assignPackagesToPool(int pAmt) {
		WorldPackage lNewPackage = new WorldPackage();
		mPackagePool.add(lNewPackage);
		for (int i = 0; i < pAmt; i++) {
			mPackagePool.add(new WorldPackage());
		}

		return lNewPackage;

	}

}
