package com.elitecore.core.util.rrd;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.DsDef;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDbPool;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;

public class RRDManager {
	private static RRDManager rrdManager;
	RrdDbPool rrdPool = RrdDbPool.getInstance();
	
	static {
		rrdManager = new RRDManager();
	}
	
	public static RRDManager getInstance(){
		return rrdManager;
	}
	
	public void createOrOpenRRDFile(String rrdFileLocation,List<DsDef> dataSources,List<ArcDef> archives) throws IOException{
        File rrdFile = new File(rrdFileLocation);
        File parentFolder = rrdFile.getParentFile();
        if(!parentFolder.exists()){
        	if(!parentFolder.mkdirs())
        		throw new IOException("Unable to create directory: " + parentFolder.getAbsolutePath());
        }
        
        if(!rrdFile.exists()) {
           // create RRD file since it does not exist
           RrdDef rrdDef = new RrdDef(rrdFileLocation,1);
           rrdDef.setStartTime(Util.getTime());
           
           if(dataSources != null){
        	   for(int i=0;i<dataSources.size();i++){
        		   rrdDef.addDatasource(dataSources.get(i));   
        	   }
           }
           
           if(archives != null){
        	   for(int i=0;i<archives.size();i++){
        		   rrdDef.addArchive(archives.get(i));  
        	   }
           }
           
            // create RRD file in the pool
            RrdDb rrdDb = rrdPool.requestRrdDb(rrdDef);
            rrdPool.release(rrdDb);
        }

	}
	public void releaseRRDFile(String rrdFile) throws IOException{
		rrdPool.release(rrdPool.requestRrdDb(rrdFile));
	}
	
	public void insertIntoRRDFile(String rrdFile, String strDataSource, double value) throws IOException{
		// request RRD database reference from the pool
        RrdDb rrdDb = rrdPool.requestRrdDb(rrdFile);
        Sample sample = rrdDb.createSample();
        sample.setValue(strDataSource, value);
        // update database
        sample.update();
	}

	public void insertIntoRRDFile(String rrdFile, double... values) throws IOException{
		// request RRD database reference from the pool
        RrdDb rrdDb = rrdPool.requestRrdDb(rrdFile);
        Sample sample = rrdDb.createSample();
        sample.setValues(values);
        // update database
        sample.update();
	}
	
	public FetchData fetchData(String rrdFile,ConsolFun consolFun, long fetchStart, long fetchEnd) throws IOException{
		RrdDb rrdDb = rrdPool.requestRrdDb(rrdFile);
		FetchRequest fetchRequest = rrdDb.createFetchRequest(consolFun, fetchStart, fetchEnd);
		if(fetchRequest != null)
			return fetchRequest.fetchData();
		return null;
	}
	
	public double[] fetchData(String rrdFile,String archivName,ConsolFun consolFun, long fetchStart, long fetchEnd) throws IOException{
		FetchData fetchData = fetchData(rrdFile, consolFun, fetchStart, fetchEnd);
		if(fetchData == null)
			return null;
		return fetchData.getValues(archivName);
	}
}
