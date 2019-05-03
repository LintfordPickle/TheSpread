package com.ruse.spread.controllers;

import java.util.List;

import com.ruse.spread.data.ParticleBulletCollisionModifier;
import com.ruse.spread.data.ParticleMortarCollisionModifier;
import com.ruse.spread.data.projectile.ProjectileManager;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.graphics.particles.ParticleSystem;
import net.lintford.library.core.graphics.particles.initialisers.ParticleRandomRotationInitialiser;
import net.lintford.library.core.graphics.particles.initialisers.ParticleRandomSizeInitialiser;
import net.lintford.library.core.graphics.particles.initialisers.ParticleSourceRegionInitialiser;
import net.lintford.library.core.graphics.particles.initialisers.ParticleTurnToFaceInitialiser;
import net.lintford.library.core.graphics.particles.modifiers.ParticleLifetimeAlphaFadeInOutModifier;
import net.lintford.library.core.graphics.particles.modifiers.ParticleLifetimeModifier;
import net.lintford.library.core.graphics.particles.modifiers.ParticleLifetimeScaleFadeInOutModifier;
import net.lintford.library.core.graphics.particles.modifiers.ParticlePhysicsModifier;

public class ParticleController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "ProjectileController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ProjectileManager mProjectileManager;
	private WorldController mWorldController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ProjectileManager projectileManager() {
		return mProjectileManager;
	}

	@Override
	public boolean isInitialised() {
		return mProjectileManager != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ParticleController(ControllerManager pControllerManager, ProjectileManager pProjectileManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mProjectileManager = pProjectileManager;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		ControllerManager lControllerManager = pCore.controllerManager();

		mWorldController = (WorldController) lControllerManager.getControllerByNameRequired(WorldController.CONTROLLER_NAME, entityGroupID());

		createParticleSystems();

	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		List<ParticleSystem> lSystems = mProjectileManager.particleSystems();
		final int lNumSystems = lSystems.size();
		for (int i = 0; i < lNumSystems; i++) {
			lSystems.get(i).update(pCore);

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public Particle shootProjectile(String pPSystem, float pX, float pY, float pVelX, float pVelY, float pLife) {
		ParticleSystem lParticleSystem = mProjectileManager.getParticleSystem(pPSystem);
		if (lParticleSystem == null)
			return null;

		return lParticleSystem.spawnParticle(pX, pY, pVelX, pVelY, pLife);

	}

	private void createParticleSystems() {
		ParticleSystem lBulletsParticleSystem = new ParticleSystem("Pillbox", 128);

		lBulletsParticleSystem.addInitialiser(new ParticleTurnToFaceInitialiser());
		lBulletsParticleSystem.addInitialiser(new ParticleSourceRegionInitialiser(0, 0, 32, 32));

		lBulletsParticleSystem.addModifier(new ParticleLifetimeModifier());
		lBulletsParticleSystem.addModifier(new ParticlePhysicsModifier());
		lBulletsParticleSystem.addModifier(new ParticleBulletCollisionModifier(this, mWorldController.gameWorld()));

		ParticleSystem lTurretParticleSystem = new ParticleSystem("Turret", 128);

		lTurretParticleSystem.addInitialiser(new ParticleTurnToFaceInitialiser());
		lTurretParticleSystem.addInitialiser(new ParticleSourceRegionInitialiser(64, 0, 32, 32));

		lTurretParticleSystem.addModifier(new ParticleLifetimeModifier());
		lTurretParticleSystem.addModifier(new ParticlePhysicsModifier());
		lTurretParticleSystem.addModifier(new ParticleBulletCollisionModifier(this, mWorldController.gameWorld()));

		ParticleSystem lMortarParticleSystem = new ParticleSystem("Mortar", 128);
		lMortarParticleSystem.addInitialiser(new ParticleTurnToFaceInitialiser());
		lMortarParticleSystem.addInitialiser(new ParticleSourceRegionInitialiser(64, 0, 32, 32));

		lMortarParticleSystem.addModifier(new ParticleLifetimeModifier());
		lMortarParticleSystem.addModifier(new ParticlePhysicsModifier());
		lMortarParticleSystem.addModifier(new ParticleLifetimeScaleFadeInOutModifier(1.0f, 5.0f));
		lMortarParticleSystem.addModifier(new ParticleMortarCollisionModifier(this, mWorldController.gameWorld()));

		ParticleSystem lSmokeParticleSystem = new ParticleSystem("Smoke", 128);
		lSmokeParticleSystem.addInitialiser(new ParticleSourceRegionInitialiser(0, 32, 32, 32));
		lSmokeParticleSystem.addInitialiser(new ParticleRandomSizeInitialiser(1f, 2.0f));
		lSmokeParticleSystem.addInitialiser(new ParticleRandomRotationInitialiser(0, 360));

		lSmokeParticleSystem.addModifier(new ParticleLifetimeModifier());
		lSmokeParticleSystem.addModifier(new ParticlePhysicsModifier());
		lSmokeParticleSystem.addModifier(new ParticleLifetimeAlphaFadeInOutModifier());

		ParticleSystem lDebrisParticleSystem = new ParticleSystem("Debris", 128);
		lDebrisParticleSystem.addInitialiser(new ParticleTurnToFaceInitialiser());
		lDebrisParticleSystem.addInitialiser(new ParticleSourceRegionInitialiser(32, 0, 32, 32));
		lDebrisParticleSystem.addInitialiser(new ParticleRandomSizeInitialiser(1.5f, 2.0f));
		lDebrisParticleSystem.addInitialiser(new ParticleRandomRotationInitialiser(0, 360));

		lDebrisParticleSystem.addModifier(new ParticleLifetimeModifier());
		lDebrisParticleSystem.addModifier(new ParticlePhysicsModifier());
		lDebrisParticleSystem.addModifier(new ParticleLifetimeAlphaFadeInOutModifier());

		ParticleSystem lMuzzleParticleSystem = new ParticleSystem("Muzzle", 64);
		lMuzzleParticleSystem.addInitialiser(new ParticleTurnToFaceInitialiser());
		lMuzzleParticleSystem.addInitialiser(new ParticleSourceRegionInitialiser(64, 32, 32, 32));
		lMuzzleParticleSystem.addInitialiser(new ParticleRandomSizeInitialiser(0.4f, 1.0f));
		lMuzzleParticleSystem.addInitialiser(new ParticleRandomRotationInitialiser(0, 360));
		lMuzzleParticleSystem.addInitialiser(new ParticleRandomSizeInitialiser(2, 4));

		lMuzzleParticleSystem.addModifier(new ParticleLifetimeModifier());
		lMuzzleParticleSystem.addModifier(new ParticlePhysicsModifier());
		lMuzzleParticleSystem.addModifier(new ParticleLifetimeAlphaFadeInOutModifier());

		mProjectileManager.addParticleSystem(lBulletsParticleSystem);
		mProjectileManager.addParticleSystem(lTurretParticleSystem);
		mProjectileManager.addParticleSystem(lMortarParticleSystem);
		mProjectileManager.addParticleSystem(lSmokeParticleSystem);
		mProjectileManager.addParticleSystem(lDebrisParticleSystem);
		mProjectileManager.addParticleSystem(lMuzzleParticleSystem);

	}

}
