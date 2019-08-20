package com.elitecore.test;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.test.channel.Channel;
import com.elitecore.test.channel.ChannelData;
import com.elitecore.test.channel.ChannelVirtualGateway;
import com.elitecore.test.channel.tcp.TCPChennelData;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.CompositeCommand;
import com.elitecore.test.command.data.CommandData;
import com.elitecore.test.command.data.ConditionCommandData;
import com.elitecore.test.command.data.LoopCommandData;
import com.elitecore.test.command.data.ParallelCommandData;
import com.elitecore.test.command.data.ReceiveRequestCommandData;
import com.elitecore.test.command.data.SendCommandData;
import com.elitecore.test.command.data.StoreCommandData;
import com.elitecore.test.command.data.ValidationData;
import com.elitecore.test.command.data.WaitCommandData;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.VirtualGateway;
import com.elitecore.test.diameter.factory.AttributeFactory;
import com.elitecore.test.diameter.factory.PacketFactory;
import com.elitecore.test.util.FileUtil;
import com.elitecore.test.util.IntervalBasedTask;
import com.elitecore.test.util.SingleExecutionAsyncTask;
import com.elitecore.test.util.TaskScheduler;
import com.elitecore.test.util.XMLDeserializer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


@XmlRootElement(name = "scenario")
public class ScenarioData{
	
	
	private static final String MODULE = "SCNARIO-CMD";


	private static String serverHome;
	private static String resourcePath;


	@XmlElementRefs({
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class),
		@XmlElementRef(name = "validate", type = ValidationData.class),
		@XmlElementRef(name = "parallel", type = ParallelCommandData.class),
		@XmlElementRef(name = "loop", type = LoopCommandData.class),
		@XmlElementRef(name = "wait", type = WaitCommandData.class),
		@XmlElementRef(name = "echo", type = EchoCommandData.class),
		@XmlElementRef(name = "condition", type = ConditionCommandData.class),
		@XmlElementRef(name = "store", type = StoreCommandData.class)
	})
	private List<CommandData> commandDatas = new ArrayList<CommandData>();
	
	private String name;

	@XmlElementRefs({
		@XmlElementRef(name = "tcp", type = TCPChennelData.class)
	})
	private List<ChannelData>  chennelData = new ArrayList<ChannelData>();

	public List<ChannelData> getChennelData() {
		return chennelData;
	}

	public List<CommandData> getCommandDatas() {
		return commandDatas;
	}
	
	@XmlAttribute(name = "name",required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public static void main(String [] args){
		String filePath = args[0];
		Channel channel = null;
		TaskSchedulerImpl taskScheduler = null;
		try{
			
			Compiler.getDefaultCompiler().addFunction(new ValueExpression());
			
			URL currentPath = ScenarioData.class.getClassLoader().getResource(".");
			
			File parentDirectory = new File(URLDecoder.decode(currentPath.getPath(), "UTF-8")).getParentFile();
			
			serverHome = URLDecoder.decode(parentDirectory.getAbsolutePath(),"UTF-8");
			
			resourcePath = serverHome + File.separator  + "resources";
			
			File file = new File(resourcePath + File.separator + "dictionary" + File.separator +"diameter");
			
			DiameterDictionary.getInstance().load(file);
			
			AttributeFactory attributeFactory = new AttributeFactory();
			
			final PacketFactory packetFactory = new PacketFactory(attributeFactory);
			
			ScenarioData command = null;
			if(filePath.endsWith("xml")){
				command  = createCommandWithXML(filePath,packetFactory);
			} else {
				command  = createCommandWithJSon(filePath,packetFactory);
			}
			
			channel = command.getChennelData().get(0).create();
			channel.open();
			final ChannelVirtualGateway virtualGateway = new ChannelVirtualGateway(channel);
			channel.registerEventObserver(virtualGateway);
			
			Command command2 = command.create(new ScenarioContext() {

				@Override
				public PacketFactory getPacketFactory() {
					return packetFactory;
				}

				@Override
				public VirtualGateway getVirtualGateway() {
					return virtualGateway;
				}
				
			});
			
			taskScheduler = new TaskSchedulerImpl();
			Process process = new Process((command.getName()));
			command2.execute(new ExecutionContextImpl(new TaskSchedulerImpl(),process,packetFactory));
			
		}catch(Throwable ex){
			ex.printStackTrace();
			if(channel != null){
				try {
					channel.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(taskScheduler != null){
				taskScheduler.scheduledThreadPoolExecutor.shutdownNow();
			}
		} finally {
			getLogger().debug(MODULE, "shutting down task scheduler");
		}
		
	
	}

	
	private static ScenarioData createCommandWithJSon(String filePath, final PacketFactory packetFactory) throws JsonSyntaxException, Exception {
		
		File newFile = new File(filePath);
		
		if (newFile.isAbsolute() == false) {
			newFile = new File(serverHome + File.separator + filePath);
		}
		
		Preconditions.checkArgument(newFile.exists() == true, filePath + " does not exist");
		Preconditions.checkArgument(newFile.isDirectory() == false, filePath + " is dictionary");
		Preconditions.checkArgument(newFile.canRead() == true, filePath + " does not have read permission");
		
		
		
		BufferedReader fileInputStream = new BufferedReader(new FileReader(newFile));
		
		String json = fileInputStream.readLine();
		
		FileUtil.closeQuietly(fileInputStream);
		
		return new Gson().fromJson(json, ScenarioData.class);
	}

	private static ScenarioData createCommandWithXML(String filePath, final PacketFactory packetFactory) throws Exception {

		File newFile = new File(filePath);
		
		if (newFile.isAbsolute() == false) {
			newFile = new File(serverHome + File.separator + "resources" + File.separator + filePath);
		}

		Preconditions.checkArgument(newFile.exists() == true, filePath + " does not exist");
		Preconditions.checkArgument(newFile.isDirectory() == false, filePath + " is dictionary");
		Preconditions.checkArgument(newFile.canRead() == true, filePath + " does not have read permission");
		
		return XMLDeserializer.deserialize(newFile, ScenarioData.class);
		
	
		
	}

	public Command create(ScenarioContext scenarioContext) throws Exception{
		
		List<Command> commands = new ArrayList<Command>();
		
		for (CommandData commandData : commandDatas) {
			commands.add(commandData.create(scenarioContext));
		}
		return new CompositeCommand(name, commands){

			@Override
			public void execute(ExecutionContext context) throws Exception {
				
				if(getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Executing scenario("+getName()+")");
				try{
					for(Command command :  commands){
						if(context.getProcess().isStopRequested()){
							org.junit.Assert.fail(context.getProcess().getResonse());
						}
						command.execute(context);
					}
				} finally {
					LogManager.getLogger().debug(MODULE,context.toString());
				}
				
				if(getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Scenario("+getName()+") execution completed");
				
			}
			
			@Override
			public String getName() {
				return name;
			}
			
		};
	}
	
	static class TaskSchedulerImpl implements TaskScheduler {
		
		private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

		public TaskSchedulerImpl() {
			this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(0, 
					new EliteThreadFactory("SVR-SCH", "SVR-SCH", Thread.NORM_PRIORITY)
					);
		}

	

		@Override
		public Future<?> scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
			if (task == null) {
	    		return null;
	    	}
	    
	    	Future<?> future = null;
	    	if (task.getInitialDelay() > 0) {
	    		future = scheduledThreadPoolExecutor.schedule(new Runnable(){
	    			@Override
	    			public void run() {
	    				try {
	    					task.execute();
	    				}catch(Throwable t) {

	    				} finally {
	    					
	    				}
	    			}}, task.getInitialDelay(), task.getTimeUnit());
	    		
	    			
	    	} else {
	    		future = scheduledThreadPoolExecutor.submit(new Runnable() {

	    			@Override
	    			public void run() {
	    				try {
	    					task.execute();
	    				}catch(Throwable t) {

	    				}
	    			}});
	    	}
	    	
	    	return future;

		}

		@Override
		public Future<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {
			if (task == null) {
	    		return null;
	    	}
	    	
	    	Future<?> future = null;
	    	if (task.isFixedDelay()) {
	    		try{
	    			future = scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
	    				@Override
	    				public void run() {

	    					try {
	    						task.preExecute();
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.execute();
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.postExecute();
	    					}catch(Throwable t) {

	    					}

	    				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());

	    		}catch(Exception e){
	    			LogManager.getLogger().error(MODULE, "Error in scheduling Fixed Delay task reason: " + e.getMessage());
	    			LogManager.getLogger().trace(MODULE,e);
	    		}

	    	}else {
	    		try{
	    			future = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable(){
	    				@Override
	    				public void run() {
	    					try {
	    						task.preExecute();
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.execute();
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.postExecute();
	    					}catch(Throwable t) {

	    					}

	    				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
	    		}catch(Exception e){
	    			LogManager.getLogger().error(MODULE, "Error in scheduling task reason: " + e.getMessage());
	    			LogManager.getLogger().trace(MODULE,e);
	    		}

	    	}
	    	
	    	return future;
		}
	}

}
