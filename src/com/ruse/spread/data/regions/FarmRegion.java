package com.ruse.spread.data.regions;

import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldPackage;
import com.ruse.spread.data.world.WorldRegion;
import com.ruse.spread.data.world.WorldPackage.PACKAGETYPE;

public class FarmRegion extends WorldRegion {

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

	public FarmRegion(int pUID) {
		super(pUID, World.TILE_TYPE_FARM);

		name = "Farm";
		
		storage = storageCapacity;
		
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void fillPackage(WorldPackage pPackgeToFill) {
		pPackgeToFill.amount = 1;
		pPackgeToFill.packageType = PACKAGETYPE.food;
		
		storage = 0;

	}
	
	@Override
	public boolean canFillPackage(PACKAGETYPE pType, int pAmt) {
		return pType == PACKAGETYPE.food && storage >= pAmt;
		
	}

}
