package com.ruse.spread.data.projectile;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.audio.AudioData;
import net.lintford.library.core.audio.AudioManager;
import net.lintford.library.core.graphics.particles.initialisers.ParticleTurnToFaceInitialiser;
import net.lintford.library.core.graphics.particles.modifiers.ParticlePhysicsModifier;
import net.lintford.library.renderers.particles.ParticleRenderer;

public class ParticleManager {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final float BULLET_SPEED = 20f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	BulletParticleSystem mBulletParticles;
	BulletParticleSystem mTurretParticles;
	BulletParticleSystem mMortarParticles;

	ParticleRenderer mBulletRenderer;
	ParticleRenderer mTurretRenderer;
	ParticleRenderer mMortarRenderer;

	private AudioData mShootSoundData;
	AudioManager mAudioManager;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public BulletParticleSystem bullets() {
		return mBulletParticles;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ParticleManager() {

		setupBulletParticles();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mBulletRenderer.loadGLContent(pResourceManager);

		// Get the sound
		mShootSoundData = pResourceManager.audioManager().loadWavSound("ShootSound", "res/sounds/shoot.wav");
		mAudioManager = pResourceManager.audioManager();

	}

	public void unloadGLContent() {
		mBulletRenderer.unassignedParticleSystem();

		mShootSoundData.unloadAudioData();
	}

	public void handleInput(LintfordCore pCore) {

	}

	public void update(LintfordCore pCore) {
		mBulletParticles.update(pCore);

	}

	public void draw(LintfordCore pCore) {
		mBulletRenderer.draw(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void setupBulletParticles() {
		mBulletParticles = new BulletParticleSystem();

		ParticlePhysicsModifier lParticlePhysics = new ParticlePhysicsModifier();
		mBulletParticles.addModifier(lParticlePhysics);
		mBulletParticles.addInitialiser(new ParticleTurnToFaceInitialiser());

		mBulletParticles.initialise("worldParticles", "res/textures/worldParticles.png");

		// FIXME: this needs a specific/dedicated entityGroupID
		mBulletRenderer = new ParticleRenderer(LintfordCore.CORE_ENTITY_GROUP_ID);
		mBulletRenderer.assignParticleSystem(mBulletParticles);

	}

	public void shootBullet(float pFromX, float pFromY, float pToX, float pToY, int pTeamID) {
		float lAngle = (float) Math.atan2(pToY - pFromY, pToX - pFromX);

		float lVelX = (float) Math.cos(lAngle) * BULLET_SPEED;
		float lVelY = (float) Math.sin(lAngle) * BULLET_SPEED;

		bullets().spawnParticle(pFromX, pFromY, lVelX, lVelY, 500f, 0, 0, 32, 32, 24);

		mAudioManager.play(mShootSoundData);

	}

}
