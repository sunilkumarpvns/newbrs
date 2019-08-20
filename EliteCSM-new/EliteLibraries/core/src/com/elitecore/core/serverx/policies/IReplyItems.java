package com.elitecore.core.serverx.policies;

import java.util.ArrayList;
import java.util.Map;

import com.elitecore.core.serverx.policies.data.ITreeData;

public interface IReplyItems {
	public ArrayList<?> getReplyItems(ITreeData treeData, Map<String, Map<String, ArrayList<String>>> overideValues);
	public void parseReplyItems() throws ParserException;
}
