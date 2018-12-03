package com.ruse.spread.data;

import net.lintford.library.data.BaseData;

public class GameState extends BaseData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 8675321017137876822L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public int population; // population in HQ
	public int populationPlayer; // player owned people
	public int populationWorld; // total people in world
	public int food; // food in HQ
	public int metals; // metals in HQ

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameState() {
		startNewGame();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame() {
		population = 0;
		populationPlayer = 0;
		populationWorld = 0;
		metals = 0;
		food = 0;

	}

}
