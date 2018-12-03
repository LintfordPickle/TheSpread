package com.ruse.spread.data.regions;

import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldPackage;
import com.ruse.spread.data.world.WorldRegion;
import com.ruse.spread.data.world.WorldPackage.PACKAGETYPE;

public class CityRegion extends WorldRegion {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 5558627634160928108L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public int foodStorage;
	public int foodStorageCapacity = 5;

	public int popStorage;
	public int popStorageCapacity = 15;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CityRegion(int pUID) {
		super(pUID, World.TILE_TYPE_CITY);

		name = "City";

		popStorage = popStorageCapacity;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void fillPackage(WorldPackage pPackgeToFill) {
		pPackgeToFill.amount = 1;
		pPackgeToFill.packageType = PACKAGETYPE.population;

	}

	@Override
	public boolean canFillPackage(PACKAGETYPE pType, int pAmt) {
		return pType == PACKAGETYPE.population && popStorage >= pAmt;

	}

}
