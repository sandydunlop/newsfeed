package io.github.sandydunlop.newsfeed.app;

import io.github.sandydunlop.cupra.common.CupraApp;
import io.github.sandydunlop.cupra.platform.desktop.DesktopServices;


public class Newsfeed extends CupraApp {
    private static MainScreen mainScreen = null;
    private static ConfigScreen configScreen = null;
    private static BackgroundThread backgroundThread = null;


    @Override
    public String getName() {
        return "Newsfeed";
    }

    public static void main(String[] args) {
        DesktopServices platformServices = DesktopServices.getInstance();
		NewsfeedConfig.loadConfig(platformServices.getConfigDir().resolve("newsfeed.json"));
        Newsfeed newsfeedApp = new Newsfeed();
        newsfeedApp.run();
    }


    public Newsfeed() {
        // Nothing to initialize here, as the run method will handle it.
    }


    public void run() {
        getBackgroundThread();
        openScreen(getMainScreen());
    }


    public static BackgroundThread getBackgroundThread() {
        if (backgroundThread == null) {
            backgroundThread = new BackgroundThread();
            backgroundThread.start();
        }
        return backgroundThread;
    }


    public static MainScreen getMainScreen() {
        if (mainScreen == null) {
            mainScreen = new MainScreen();
        }
        return mainScreen;
    }


    public static ConfigScreen getConfigScreen() {
        if (configScreen == null) {
            configScreen = new ConfigScreen();
        }
        return configScreen;
    }
}
