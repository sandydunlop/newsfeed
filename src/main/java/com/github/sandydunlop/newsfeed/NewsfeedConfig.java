package com.github.sandydunlop.newsfeed;

import eu.midnightdust.lib.config.MidnightConfig;

public class NewsfeedConfig extends MidnightConfig {
        public static final String FEED = "feed";

        @Entry(category = FEED) public static boolean feedEnable = true;
        @Entry(category = FEED) public static String feedName = "BBC World News";
        @Entry(category = FEED) public static String feedUrl = "https://feeds.bbci.co.uk/news/world/rss.xml";
}
