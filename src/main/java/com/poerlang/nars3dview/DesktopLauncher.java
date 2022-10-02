package com.poerlang.nars3dview;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		int samples = 4;//抗锯齿采样级别，越高锯齿越少、画面越平滑，但太高会消耗过多显卡性能。此值默认为0（不进行抗锯齿处理）
		config.setBackBufferConfig(8,8,8,8,16,0,samples);
		config.setForegroundFPS(60);
		config.setWindowSizeLimits(1600,800,99999,99999);
		config.setTitle("NARS 3D View");
		new Lwjgl3Application(new MainGame(), config);
	}
}
