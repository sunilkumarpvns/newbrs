package com.elitecore.aaa.core.util.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.elitecore.core.util.cli.cmd.DetailProvider;

public abstract class ProfileProvider extends DetailProvider{
	
	protected static JMXServiceURL jmxUrl ;
	protected static JMXConnector jmxCon ;
	
	public static final String HELP = "-help";

	private static int port;
	
	private String mbeanCons;
	
	private HashMap<String, DetailProvider> detailProvideMap = new HashMap<String, DetailProvider>();
	
	public ProfileProvider(String mbeanCons) {
		this.mbeanCons = mbeanCons;
	}
	
	static{
		
		List<String> list = ManagementFactory.getRuntimeMXBean().getInputArguments();
		String strPort = null;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				String arg = list.get(i);
				if (arg.contains("-Dcom.sun.management.jmxremote.port")) {
					strPort = arg.substring(arg.lastIndexOf("=") + 1);
					break;
				}
			}
		}
		if (strPort != null) {
			port = Integer.parseInt(strPort);
		} 
		
		 try {
	        	
        	 jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:"+port+"/jmxrmi");
             jmxCon = JMXConnectorFactory.connect(jmxUrl, null);
            
        } catch (Exception e) {
            if(jmxCon != null) {
                try {
					jmxCon.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        }
		
	}
	
	@Override
	public String execute(String[] parameters) {

		if(parameters.length >0  ){

			if("?".equals(parameters[0]) || HELP.equals(parameters[0])){
				return getHelpMsg();
			}
			
	        Object placementXML = null;
	        
	        try {
	            
	            MBeanServerConnection catalogServerConnection = jmxCon.getMBeanServerConnection();
	            
	            ObjectName mbeanName1 = new ObjectName(mbeanCons+parameters[0]);

	            Set<ObjectName> policyObjectSetSet = catalogServerConnection.queryNames(mbeanName1, null);
	            if(policyObjectSetSet!=null && policyObjectSetSet.size()>0){
	            	ObjectName placementService = (ObjectName) policyObjectSetSet.iterator().next();
	            	
	            	placementXML = catalogServerConnection.invoke(placementService,
	    	                "getSubscriberProfile", Arrays.copyOfRange(parameters,1,parameters.length),null);
	            }else {
	              	return getKey()+" not found : "+parameters[0];
				}
	            
	            if(placementXML!=null){
	            	return placementXML.toString();
	            }else {
	            	return "error during getting profile";
				}
	            
	            
	        } catch (Exception e) {
	        
	        }
			
		}
		return "Invalid Option\n"+getHelpMsg();
	
	}
		
	@Override
	public String getHotkeyHelp() {
		StringWriter writer =new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		
		printWriter.print("'" + getKey() +"':{");
        
        try {
            MBeanServerConnection catalogServerConnection = jmxCon.getMBeanServerConnection();
            ObjectName mbeanName1 = new ObjectName(mbeanCons+"*");

            Set<ObjectName> policyObjectSetSet = catalogServerConnection.queryNames(mbeanName1, null);
            
            Iterator<ObjectName> iterator = policyObjectSetSet.iterator();
            int size = policyObjectSetSet.size();
            int i=1;
    		while(iterator.hasNext()) {
    			ObjectName objectName = (ObjectName)iterator.next();
    			String policyOrDriverName = objectName.getKeyProperty("data");
    			
    			printWriter.print("'" + policyOrDriverName +"':{");
    			if(i!=size)
    				printWriter.print("},");
    			else {
    				printWriter.print("}");
				}
        		
        		i =i+1;
    		}
    		printWriter.print("}");
            
        } catch (Exception e) {
        
        }
		return writer.toString();	
	}
	

	@Override
	public String getHelpMsg() {
		return "";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProvideMap;
	}

}
