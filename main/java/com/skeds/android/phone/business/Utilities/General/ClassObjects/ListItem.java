/**
 *
 */
package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

/**
 * Simple list item with name/id
 */
public final class ListItem implements Serializable {
    public final String name;
    public final int id;

    public ListItem(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public ListItem(Generic g) {
        this.name = g.getName();
        this.id = g.getId();
    }

    @Override
    public String toString() {
        return name;
    }
}
