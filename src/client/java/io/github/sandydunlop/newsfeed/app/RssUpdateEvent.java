package io.github.sandydunlop.newsfeed.app;

import java.util.EventObject;


public class RssUpdateEvent extends EventObject {
    public RssUpdateEvent(Object source) {
        super(source);
    }
}