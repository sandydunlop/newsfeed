package io.github.sandydunlop.newsfeed.app;

import io.github.sandydunlop.cupra.common.CupraApp;
import io.github.sandydunlop.cupra.platform.desktop.DesktopServices;


public class Newsfeed extends CupraApp {
	private static RssFeed rssFeed = null;
    private static MainScreen mainScreen = null;
    private static ConfigScreen configScreen = null;

    @Override
    public String getName() {
        return "Newsfeed";
    }

    public static void main(String[] args) {
        System.setProperty("apple.awt.application.appearance", "system");
        DesktopServices.getInstance();
        Newsfeed newsfeedApp = new Newsfeed();
        newsfeedApp.display();
    }

    public Newsfeed() {
        getRssFeed();
    }

    public void display() {
        openScreen(getMainScreen());
    }

    public static RssFeed getRssFeed() {
        if (rssFeed == null) {
            rssFeed = new RssFeed();
        }
        return rssFeed;
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
