package com.ruse.spread.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class SpreadController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "SpreadController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialised() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SpreadController(ControllerManager pControllerManager, int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

	}

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void unload() {

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
