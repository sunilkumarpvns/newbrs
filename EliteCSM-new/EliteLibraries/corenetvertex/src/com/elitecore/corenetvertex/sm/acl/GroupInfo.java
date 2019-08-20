package com.elitecore.corenetvertex.sm.acl;

/**
 * Created by jaidiptrivedi on 13/7/17.
 */
public class GroupInfo {
    private String id;
    private String name;
    private boolean locked;
    private String selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
