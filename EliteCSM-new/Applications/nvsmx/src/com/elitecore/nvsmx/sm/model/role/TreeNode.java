package com.elitecore.nvsmx.sm.model.role;

import java.util.List;

public class TreeNode {
    private String text;
    private String checkedIcon;
    private boolean multiSelect;
    private StateInformation state;
    private List<TreeNode> nodes;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCheckedIcon() {
        return checkedIcon;
    }

    public void setCheckedIcon(String checkedIcon) {
        this.checkedIcon = checkedIcon;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public StateInformation getState() {
        return state;
    }

    public void setState(StateInformation state) {
        this.state = state;
    }

    public List<TreeNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }


}