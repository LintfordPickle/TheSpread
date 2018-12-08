package com.ruse.spread.data;

import com.ruse.spread.controllers.ParticleController;
import com.ruse.spread.data.world.World;
import com.ruse.spread.data.world.WorldRegion;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.graphics.particles.modifiers.IParticleModifier;

public class ParticleMortarCollisionModifier implements IParticleModifier {

	public static final boolean DEBUG_SHOT_NO_DAM = false;

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
		if (DEBUG_SHOT_NO_DAM)
			return;
		
		if(true) return;

		int lTileIndex = mGameWorld.world().getTileFromWorldPosition(pParticle.x, pParticle.y);
		int lRegionUID = mGameWorld.world().regions[lTileIndex];
		int[] lHealth = mGameWorld.world().regionHealth;
		int[] lSpreadDepth = mGameWorld.world().spreaderDepth;
		WorldRegion lRegion = mGameWorld.world().getRegionByUID(lRegionUID);

		if (lRegion == null)
			return;

		if (lRegion.type() == World.TILE_TYPE_SPAWNER) {
			if (lHealth[lTileIndex] >= 0) {
				float lSpreadDepthAct = lSpreadDepth[lTileIndex];
				if (lSpreadDepthAct <= 1)
					lSpreadDepthAct = 1f;
				lHealth[lTileIndex] -= 100f / lSpreadDepthAct;
				if (lHealth[lTileIndex] <= 0) {
					lRegion.tiles().remove(Integer.valueOf(lTileIndex));
					lHealth[lTileIndex] = 0;
					lSpreadDepth[lTileIndex] = 0;
					mGameWorld.world().regions[lTileIndex] = 0;

					if (lRegion.tiles().size() == 0) {
						mGameWorld.world().deleteRegionByUID(lRegionUID);
					}
				}

			}

			pParticle.reset();

		}

	}

}
