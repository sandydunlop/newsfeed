package io.github.sandydunlop.newsfeed.app;

import io.github.sandydunlop.cupra.common.CupraApp;
import io.github.sandydunlop.cupra.platform.desktop.DesktopServices;


public class Newsfeed extends CupraApp {
    private static NewsfeedScreen mainScreen = null;

    @Override
    public String getName() {
        return "Newsfeed";
    }

    public static void main(String[] args) {
        System.setProperty("apple.awt.application.appearance", "system");
        DesktopServices.getInstance();
        Newsfeed newsfeedApp = new Newsfeed();
        newsfeedApp.open(Newsfeed.getMainScreen());
    }

    public Newsfeed(){
        // Nothing to see here
    }

    public static NewsfeedScreen getMainScreen() {
        if (mainScreen == null) {
            mainScreen = new NewsfeedScreen();
        }
        return mainScreen;
    }
}
