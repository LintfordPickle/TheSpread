package com.ruse.spread.data;

import com.ruse.spread.controllers.ParticleController;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.graphics.particles.modifiers.IParticleModifier;

public class ParticleMortarCollisionModifier implements IParticleModifier {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameWorld mGameWorld;
	private ParticleController mGameParticleController;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleMortarCollisionModifier(ParticleController pParticleController, GameWorld pGameWorld) {
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
		// check if bullets have collided with a region (spread)
		
		if(true) return;

		int lTileIndex = mGameWorld.world().getTileFromWorldPosition(pParticle.x, pParticle.y);
		int[] spreaderPopulation = mGameWorld.world().spreadPopulation;

		// TODO(JoH): Mortars clear areas

		if (spreaderPopulation[lTileIndex] >= 0) {
			spreaderPopulation[lTileIndex] -= 15f;
			if (spreaderPopulation[lTileIndex] <= 0) {
				spreaderPopulation[lTileIndex] = 0;

			}

		}

		pParticle.reset();

	}

}
