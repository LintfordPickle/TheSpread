package com.ruse.spread.data;

import net.lintford.library.data.BaseData;

public abstract class PooledInstanceData extends BaseData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -5852978310474471242L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private boolean mIsFree;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isFree() {
		return mIsFree;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void init() {
		mIsFree = false;

	}

	public void kill() {
		mIsFree = true;

	}

}
