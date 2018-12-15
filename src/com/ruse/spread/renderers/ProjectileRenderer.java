package com.ruse.spread.renderers;

import com.ruse.spread.data.GameWorld;
import com.ruse.spread.data.projectile.ProjectileManager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.graphics.particles.ParticleSystem;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class ProjectileRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "GameParticleRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ProjectileManager mGameParticles;

	private Texture mParticlesTexture;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int ZDepth() {
		return 5;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ProjectileRenderer(RendererManager pRendererManager, GameWorld pWorld, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		mGameParticles = pWorld.projectileManager();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mParticlesTexture = TextureManager.textureManager().loadTexture("GameParticlesTexture", "res/textures/particles.png");

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

	}

	@Override
	public void draw(LintfordCore pCore) {

		TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.gameCamera());

		final int lParticleSystemCount = mGameParticles.particleSystems().size();
		for (int i = 0; i < lParticleSystemCount; i++) {
			ParticleSystem lSystem = mGameParticles.particleSystems().get(i);
			renderParticleSystem(pCore, lTextureBatch, lSystem);

		}

		lTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void renderParticleSystem(LintfordCore pCore, TextureBatch pTextureBatch, ParticleSystem pParticleSystem) {

		final int lNumParticles = pParticleSystem.particles().size();
		for (int i = 0; i < lNumParticles; i++) {
			Particle lP = pParticleSystem.particles().get(i);
			if (lP.isFree())
				continue;

			final float lScale = lP.scale;
			final float lParticleHalfSize = 2 * lScale;

			// lP.sx, lP.sy, lP.sw, lP.sh
			pTextureBatch.draw(mParticlesTexture, lP.sx, lP.sy, lP.sw, lP.sh, lP.x - lParticleHalfSize, lP.y - lParticleHalfSize, lParticleHalfSize * 2, lParticleHalfSize * 2, -0.2f, lP.rot, lParticleHalfSize, lParticleHalfSize,
					5f + lScale, lP.r, lP.g, lP.b, lP.a);

		}
	}

}
