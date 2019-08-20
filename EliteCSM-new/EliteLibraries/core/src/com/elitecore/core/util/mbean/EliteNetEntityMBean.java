package com.elitecore.core.util.mbean;

import java.util.List;
import java.util.Map;


public interface EliteNetEntityMBean extends IEliteBaseMBean{

	public List readEntities()throws Exception;
	public List readEntities(Map map)throws Exception;
	public void addEntity(Map map)throws Exception;
	public void deleteEntity(String entityId)throws Exception;
	public void updateEntity(Map map)throws Exception;
}
