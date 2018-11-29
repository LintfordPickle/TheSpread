package com.ruse.emptyproject;

import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;

public class BaseApp extends LintfordCore {

	public static void main(String[] args) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String applicationName() {
				return "";
			}

			@Override
			public String windowTitle() {
				return "";
			}

			@Override
			public int defaultWindowWidth() {
				return 640;
			}

			@Override
			public int defaultWindowHeight() {
				return 480;
			}

			@Override
			public boolean windowResizeable() {
				return false;
			}

		};

		BaseApp lClient = new BaseApp(lGameInfo);
		lClient.createWindow();
	}

	public BaseApp(GameInfo pGameInfo) {
		super(pGameInfo);
		
	}

}
