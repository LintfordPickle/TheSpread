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

	public int difficultyLevelMax = 5;
	public int difficultyLevel = 1;
	public final float difficultyTime = 40000; // ms
	public float difficultyTimer;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameState() {
		startNewGame();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame() {
		population = 0;
		populationPlayer = 0;
		populationWorld = 0;
		metals = 0;
		food = 0;

		difficultyLevel = 1;
		difficultyTimer = 0;

	}

}
