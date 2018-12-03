package com.ruse.spread.renderers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class RoadRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "RoadRenderer";
	
	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public RoadRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);
		
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void draw(LintfordCore pCore) {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
