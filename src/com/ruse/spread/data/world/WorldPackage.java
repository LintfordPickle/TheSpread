package com.ruse.spread.data.world;

import java.util.ArrayList;
import java.util.List;

import com.ruse.spread.data.PooledInstanceData;

import net.lintford.library.core.geometry.Rectangle;

public class WorldPackage extends PooledInstanceData {

	private static final long serialVersionUID = -9022054250659262042L;

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int WIDTH = 12;
	public static final int HEIGHT = 12;

	public enum PACKAGETYPE {
		none, population, metal, food,
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public PACKAGETYPE packageType = PACKAGETYPE.none;
	public float amount;
	public boolean isMoving;
	public Rectangle mBounds = new Rectangle();

	// TODO: Don't store the lists per package
	public List<WorldNode> moveList = new ArrayList<>();

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public WorldPackage() {
		kill();

		mBounds.setDimensions(WIDTH, HEIGHT);

	}

	public WorldPackage(WorldNode pNode, PACKAGETYPE pType, int pAmt) {
		packageType = pType;
		amount = pAmt;
		isMoving = false;
		// TODO: Move this out of the DATA

		mBounds.setDimensions(WIDTH, HEIGHT);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void kill() {
		super.kill();

		moveList.clear();
		isMoving = false;
		amount = 0;

	}

}
