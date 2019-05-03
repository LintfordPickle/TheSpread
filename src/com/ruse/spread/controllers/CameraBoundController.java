package com.ruse.spread.controllers;

import org.lwjgl.glfw.GLFW;

import com.ruse.spread.GameConstants;
import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.data.entities.WorldEntity;

public class CameraBoundController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraBoundController";

	private static final float CAMERA_MAN_MOVE_SPEED = 2.2f;
	private static final float CAMERA_MAN_MOVE_SPEED_MAX = 10f;

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

	private float mHomePositionX;
	private float mHomePositionY;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public Rectangle bounds() {
		return mBounds;
	}

	@Override
	public boolean isInitialised() {
		return mGameCamera != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraBoundController(ControllerManager pControllerManager, ICamera pCamera, WorldEntity pTrackEntity, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

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

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_SPACE)) {
			mGameCamera.setPosition(mHomePositionX, mHomePositionY);
			mPositionOffsetX = mHomePositionX;
			mPositionOffsetY = mHomePositionY;

		}

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

		// Limit velocity
		mPositionVelocityX = MathHelper.clamp(mPositionVelocityX, -CAMERA_MAN_MOVE_SPEED_MAX, CAMERA_MAN_MOVE_SPEED_MAX);
		mPositionVelocityY = MathHelper.clamp(mPositionVelocityY, -CAMERA_MAN_MOVE_SPEED_MAX, CAMERA_MAN_MOVE_SPEED_MAX);

		mPositionOffsetX += mPositionVelocityX;
		mPositionOffsetY += mPositionVelocityY;

		final float lBorder = 0;
		if (mPositionOffsetX + lBorder < (mBounds.left())) {
			mPositionOffsetX = (mBounds.left()) - lBorder;
			mPositionVelocityX = 0;
		}

		if (mPositionOffsetX - lBorder > (mBounds.right())) {
			mPositionOffsetX = (mBounds.right()) + lBorder;
			mPositionVelocityX = 0;
		}

		if (mPositionOffsetY + lBorder < (mBounds.top())) {
			mPositionOffsetY = (mBounds.top()) - lBorder;
			mPositionVelocityY = 0;
		}

		if (mPositionOffsetY - lBorder > (mBounds.bottom())) {
			mPositionOffsetY = (mBounds.bottom()) + lBorder;
			mPositionVelocityY = 0;
		}

		mPositionVelocityX *= 0.87f;
		mPositionVelocityY *= 0.87f;

		mGameCamera.setPosition(mPositionOffsetX, mPositionOffsetY);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void setup(float pPosX, float pPosY, float pLeft, float pTop, float pRight, float pBottom) {
		if (mGameCamera != null) {
			mHomePositionX = -pPosX - GameConstants.TILE_SIZE * 0.5f;
			mHomePositionY = -pPosY - GameConstants.TILE_SIZE * 0.5f;

			mGameCamera.setPosition(mHomePositionX, mHomePositionY);
			mPositionOffsetX = mHomePositionX;
			mPositionOffsetY = mHomePositionY;

		}

		mBounds.set(pLeft, pTop, pRight, pBottom);

	}

}