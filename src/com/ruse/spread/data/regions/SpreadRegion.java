package com.ruse.spread.data.regions;

import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldPackage;
import com.ruse.spread.data.world.WorldRegion;
import com.ruse.spread.data.world.WorldPackage.PACKAGETYPE;

public class SpreadRegion extends WorldRegion {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 5558627634160928108L;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SpreadRegion(int pUID) {
		super(pUID, World.TILE_TYPE_SPAWNER);

		name = "Spread";

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void fillPackage(WorldPackage pPackgeToFill) {

	}

	@Override
	public boolean canFillPackage(PACKAGETYPE pType, int pAmt) {
		return false;
	}

}
