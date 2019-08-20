package com.elitecore.test.aaa.anttask.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Task;

import com.elitecore.test.aaa.anttask.radius.RadiusAttributeTask;


public class AttributesTask extends Task {
	
	private List<BaseAttributeTask> attributes = new ArrayList<BaseAttributeTask>();

	public void addAttribute(RadiusAttributeTask attributeTask){
		attributes.add(attributeTask);
	}
	
	public List<BaseAttributeTask> getAttributes(){
		return attributes;
	}
}
