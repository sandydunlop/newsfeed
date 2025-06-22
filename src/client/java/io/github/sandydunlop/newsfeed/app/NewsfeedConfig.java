package io.github.sandydunlop.newsfeed.app;


public class NewsfeedConfig {
    // These are the default values overridden by the config file
    public static String feedName = "AskReddit";
    // public static String feedUrl = "https://lorem-rss.herokuapp.com/feed"; 
    public static String feedUrl = "https://www.reddit.com/r/AskReddit/new/.rss";; 
    public static boolean feedEnabled = true;
    public static boolean updateCheckEnabled = true;
}
