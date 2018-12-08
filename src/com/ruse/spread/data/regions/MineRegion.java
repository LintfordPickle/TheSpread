package com.ruse.spread.data.regions;

import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldPackage;
import com.ruse.spread.data.world.WorldRegion;
import com.ruse.spread.data.world.WorldPackage.PACKAGETYPE;

public class MineRegion extends WorldRegion {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 5558627634160928108L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public int storage;
	public int storageCapacity = 10;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MineRegion(int pUID) {
		super(pUID, World.TILE_TYPE_MINE);

		name = "Mine";

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void fillPackage(WorldPackage pPackgeToFill) {
		storage--;
		if (storage < 0)
			return;
		
		pPackgeToFill.amount = 1;
		pPackgeToFill.packageType = PACKAGETYPE.metal;

	}

	@Override
	public boolean canFillPackage(PACKAGETYPE pType, int pAmt) {
		return pType == PACKAGETYPE.metal && storage >= pAmt;

	}

}
