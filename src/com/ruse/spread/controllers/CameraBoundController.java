package com.ruse.spread.controllers;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.data.entities.WorldEntity;

public class CameraBoundController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraBoundController";

	private static final float CAMERA_MAN_MOVE_SPEED = 2.2f;
	private static final float CAMERA_MAN_MOVE_SPEED_MAX = 1f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;
	private Rectangle mBounds;

	private float mPositionOffsetX;
	private float mPositionOffsetY;

	private float mPositionAccX;
	private float mPositionAccY;

	private float mPositionVelocityX;
	private float mPositionVelocityY;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public Rectangle bounds() {
		return mBounds;
	}

	public void setBounds(float pLeft, float pTop, float pRight, float pBottom) {
		mBounds.set(pLeft, pTop, pRight, pBottom);

	}

	@Override
	public boolean isInitialised() {
		return mGameCamera != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraBoundController(ControllerManager pControllerManager, ICamera pCamera, WorldEntity pTrackEntity, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		mGameCamera = pCamera;
		mBounds = new Rectangle();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mGameCamera == null)
			return false;

		final float speed = CAMERA_MAN_MOVE_SPEED;

		// Just listener for clicks - couldn't be easier !!?!
		if (pCore.input().keyDown(GLFW.GLFW_KEY_A)) {
			mPositionAccX += speed;

		}

		if (pCore.input().keyDown(GLFW.GLFW_KEY_D)) {
			mPositionAccX -= speed;

		}

		if (pCore.input().keyDown(GLFW.GLFW_KEY_W)) {
			mPositionAccY += speed;

		}

		if (pCore.input().keyDown(GLFW.GLFW_KEY_S)) {
			mPositionAccY -= speed;

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		mPositionVelocityX += mPositionAccX;
		mPositionVelocityY += mPositionAccY;
		mPositionAccX = 0;
		mPositionAccY = 0;

		mPositionOffsetX += mPositionVelocityX;
		mPositionOffsetY += mPositionVelocityY;

		// TODO: This is a hack, fix it
		final float lBorder = 512;
		if (mPositionOffsetX + lBorder < (mBounds.left()) / 2) {
			mPositionOffsetX = (mBounds.left()) / 2 - lBorder;
			mPositionVelocityX = 0;
		}

		if (mPositionOffsetX - lBorder > (mBounds.right()) / 2) {
			mPositionOffsetX = (mBounds.right()) / 2 + lBorder;
			mPositionVelocityX = 0;
		}

		if (mPositionOffsetY + lBorder < (mBounds.top()) / 2) {
			mPositionOffsetY = (mBounds.top()) / 2 - lBorder;
			mPositionVelocityY = 0;
		}

		if (mPositionOffsetY - lBorder > (mBounds.bottom()) / 2) {
			mPositionOffsetY = (mBounds.bottom()) / 2 + lBorder;
			mPositionVelocityY = 0;
		}

		mPositionVelocityX *= 0.87f;
		mPositionVelocityY *= 0.87f;

		mGameCamera.setPosition(mPositionOffsetX, mPositionOffsetY);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}