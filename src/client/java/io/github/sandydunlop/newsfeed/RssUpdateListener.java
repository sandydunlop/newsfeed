package io.github.sandydunlop.newsfeed;

import java.util.EventListener;


public interface RssUpdateListener extends EventListener {
    void feedUpdated(RssUpdateEvent event);
}