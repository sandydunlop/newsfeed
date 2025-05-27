package io.github.sandydunlop.newsfeed;

import java.util.EventListener;


public interface ListBoxSelectionChangedListener extends EventListener {
    void selectionChanged(ListBoxSelectionChangedEvent event);
}