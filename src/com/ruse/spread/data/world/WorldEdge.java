package com.ruse.spread.data.world;

import com.ruse.spread.data.PooledInstanceData;

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

	public WorldNode getOtherNode(WorldNode pNode) {
		if (pNode.equals(node1))
			return node2;
		else
			return node1;
	}

}
