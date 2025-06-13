package io.github.sandydunlop.cupra.gui.events;

import java.util.EventListener;


public interface CListBoxSelectionChangedListener extends EventListener {
    void selectionChanged(CListBoxSelectionChangedEvent event);
}