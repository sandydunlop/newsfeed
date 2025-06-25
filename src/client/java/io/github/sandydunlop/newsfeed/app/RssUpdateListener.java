package io.github.sandydunlop.newsfeed.app;

import java.util.EventListener;


public interface RssUpdateListener extends EventListener {
    void feedUpdated(RssUpdateEvent event);
}