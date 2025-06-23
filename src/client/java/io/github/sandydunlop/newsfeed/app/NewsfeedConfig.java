package io.github.sandydunlop.newsfeed.app;

import java.nio.file.Path;

public class NewsfeedConfig {
    public static Path configFilePath = null;

    // These are the default values overridden by the config file
    public static String feedName = "AskReddit";
    // public static String feedUrl = "https://lorem-rss.herokuapp.com/feed"; 
    public static String feedUrl = "https://www.reddit.com/r/AskReddit/new/.rss";; 
    public static boolean feedEnabled = true;
    public static boolean updateCheckEnabled = true;
}
