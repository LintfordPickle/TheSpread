package com.ruse.spread.controllers;

import com.ruse.spread.data.world.nodes.WorldNode;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class MouseController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "MouseController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public WorldNode tempWorldNode;
	public boolean isBuilding;
	public boolean viable;
	public float x, y;

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

	public MouseController(ControllerManager pControllerManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

	}

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void unload() {

	}

}
