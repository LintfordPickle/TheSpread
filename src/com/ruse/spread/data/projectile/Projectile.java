package com.ruse.spread.data.projectile;

import com.ruse.spread.data.PooledInstanceData;

import net.lintford.library.core.geometry.Rectangle;

public class Projectile extends PooledInstanceData {

	private static final long serialVersionUID = 3208403078484067531L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public Rectangle bounds = new Rectangle();

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public Projectile() {
		kill();

	}

}
