package com.ruse.spread.data.projectile;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.graphics.particles.ParticleSystem;
import net.lintford.library.data.BaseData;

public class ProjectileManager extends BaseData {

	private static final long serialVersionUID = 3051290044961187148L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private List<ParticleSystem> mParticleSystems = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<ParticleSystem> particleSystems() {
		return mParticleSystems;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ProjectileManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public ParticleSystem getParticleSystem(String pName) {
		final int lNumParticleSystems = mParticleSystems.size();
		for (int i = 0; i < lNumParticleSystems; i++) {
			if (mParticleSystems.get(i).name().equals(pName)) {
				return mParticleSystems.get(i);

			}

		}

		return null;
	}

	public void addParticleSystem(ParticleSystem pNewParticleSystem) {
		if (pNewParticleSystem == null)
			return;
		if (pNewParticleSystem.name() == null || pNewParticleSystem.name().isEmpty())
			return;

		if (!mParticleSystems.contains(pNewParticleSystem)) {
			mParticleSystems.add(pNewParticleSystem);

		}

	}

	public void startNewGame() {
		final int lParticleSystemCount = mParticleSystems.size();
		for (int i = 0; i < lParticleSystemCount; i++) {
			ParticleSystem lSystem = mParticleSystems.get(i);

			lSystem.reset();

		}

	}

}
