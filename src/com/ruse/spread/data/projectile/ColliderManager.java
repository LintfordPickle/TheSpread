package com.ruse.spread.data.projectile;

import com.ruse.spread.data.GameWorld;

import net.lintford.library.core.LintfordCore.GameTime;
import net.lintford.library.core.graphics.particles.ParticleSystem;

public class ColliderManager {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameWorld mGameWorld;
	private ParticleSystem mParticleSystem;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ColliderManager(GameWorld pGameWorld, ParticleSystem pParticleSystem) {
		mGameWorld = pGameWorld;
		mParticleSystem = pParticleSystem;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void update(GameTime pGameTime) {

	}

}
