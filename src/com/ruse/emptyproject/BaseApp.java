package com.ruse.emptyproject;

import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;

public class BaseApp extends LintfordCore {

	public static void main(String[] args) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String applicationName() {
				return "Unnamed";
			}

			@Override
			public String windowTitle() {
				return "EmptyGame";
			}

			@Override
			public int windowWidth() {
				return 800;
			}

			@Override
			public int windowHeight() {
				return 600;
			}

			@Override
			public boolean windowResizeable() {
				return true;
			}

		};

		// ExcavationClient def constructor will automatically create a window and load the previous
		// settings (if they exist).
		BaseApp lClient = new BaseApp(lGameInfo);
		lClient.createWindow();
	}

	public BaseApp(GameInfo pGameInfo) {
		super(pGameInfo);
	}

}
