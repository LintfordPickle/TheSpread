package com.ruse.spread;

public class GameConstants {

	// World
	public static final int WIDTH = 64;
	public static final int HEIGHT = 64;

	public static final int NUM_CITIES =12;

	public static final int TILE_SIZE = 32;
	
	// PACKAGES
	public static final float PACKAGE_MOVEMENT_SPEED = 1.5f;

	// SPREAD
	public static final int BASE_SPREAD_PER_TICK = 1;
	public static final int MAX_SPREAD_PER_TILE = 2000;
	public static final float SPREAD_PER_DIFF_MOD = 2;

	// Set the minimum amount of population in a tile before the spread will move to adjacent tiles.
	public static final int SATURATION_DOWNHILL = +10;
	public static final int SATURATION_LEVEL_GROUND = +7;
	public static final int SATURATION_UPHILL = +10;

	// Number of spread to move in any one direction (this controls the speed).
	public static final int DIFFICULTY_MOD = 1;
	public static final int MOVE_DOWNHILL = 10 + DIFFICULTY_MOD;
	public static final int MOVE_SAME_LEVEL = 5 + DIFFICULTY_MOD;
	public static final int MOVE_UPHILL = 3 + DIFFICULTY_MOD;

	public static final int POP_TO_MOVE = 1;

	public static final int TICK_TIMER_MS = 200; // update spread per x ms
	
}
