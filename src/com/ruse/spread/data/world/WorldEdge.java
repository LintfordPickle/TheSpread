package com.ruse.spread.data.world;

import com.ruse.spread.data.PooledInstanceData;
import com.ruse.spread.data.world.nodes.WorldNode;

public class WorldEdge extends PooledInstanceData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -6316584074386994224L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public WorldNode node1;
	public WorldNode node2;
	public float dist;

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public WorldNode getOtherNode(WorldNode pNode) {
		if (pNode.equals(node1))
			return node2;
		else
			return node1;
	}

	public void removeEdgeFromNodes() {
		// Clean up external references
		if (node1 != null) {
			node1.edges.remove(this);
		}

		if (node2 != null) {
			node2.edges.remove(this);
		}

		// Clean up internal references
		node1 = null;
		node2 = null;

		dist = -1;
	}

	public boolean isLinkBetween(WorldNode pNodeA, WorldNode pNodeB) {
		if (pNodeA == null || pNodeB == null)
			return false;

		if (node1.equals(pNodeA) && node2.equals(pNodeB)) {
			return true;
		} else if (node2.equals(pNodeA) && node1.equals(pNodeB)) {
			return true;
		} else
			return false;
	}

}
