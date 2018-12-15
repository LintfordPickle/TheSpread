package com.ruse.spread.data;

import com.ruse.spread.controllers.ParticleController;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.graphics.particles.modifiers.IParticleModifier;

public class ParticleBulletCollisionModifier implements IParticleModifier {

	public static final boolean DEBUG_SHOT_NO_DAM = false;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameWorld mGameWorld;
	private ParticleController mGameParticleController;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleBulletCollisionModifier(ParticleController pParticleController, GameWorld pGameWorld) {
		mGameWorld = pGameWorld;
		mGameParticleController = pParticleController;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		int lTileIndex = mGameWorld.world().getTileFromWorldPosition(pParticle.x, pParticle.y);
		int[] spreaderPopulation = mGameWorld.world().spreadPopulation;

		if (spreaderPopulation[lTileIndex] > 0) {

			spreaderPopulation[lTileIndex] -= 10;
			if (spreaderPopulation[lTileIndex] <= 0) {
				spreaderPopulation[lTileIndex] = 0;

			}

		}

	}

}
