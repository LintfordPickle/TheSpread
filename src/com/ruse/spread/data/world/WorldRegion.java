package com.ruse.spread.data.world;

import java.util.ArrayList;
import java.util.List;

import com.ruse.spread.data.regions.CityRegion;
import com.ruse.spread.data.regions.FarmRegion;
import com.ruse.spread.data.regions.MineRegion;

import net.lintford.library.data.BaseData;

public abstract class WorldRegion extends BaseData {

	public static WorldRegion createNewRegion(int pUID, int pType) {
		switch (pType) {
		case World.REGION_TYPE_MINE:
			return new MineRegion(pUID);
		case World.REGION_TYPE_FARM:
			return new FarmRegion(pUID);
		case World.REGION_TYPE_CITY:
			return new CityRegion(pUID);
		}

		return null;
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -4942760447159744932L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected int mUID;
	protected int mType;
	public String name = "empty";

	protected List<Integer> tiles = new ArrayList<>();

	public int health;
	public float timer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int uid() {
		return mUID;
	}

	public int type() {
		return mType;
	}

	public List<Integer> tiles() {
		return tiles;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public WorldRegion(int pUID, int pType) {
		mUID = pUID;
		mType = pType;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public abstract void fillPackage(WorldPackage pPackgeToFill);

	public abstract boolean canFillPackage(WorldPackage.PACKAGETYPE pType, int pAmt);

}
