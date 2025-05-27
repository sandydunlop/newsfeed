package io.github.sandydunlop.newsfeed;

import java.util.EventObject;


public class RssUpdateEvent extends EventObject {
    public RssUpdateEvent(Object source) {
        super(source);
    }
}