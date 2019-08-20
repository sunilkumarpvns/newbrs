package com.elitecore.test.channel.tcp;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.channel.Channel;
import com.elitecore.test.channel.ChannelEventListener;
import com.elitecore.test.dependecy.diameter.DiameterInputStream;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.exception.ChennelClosedException;
import com.elitecore.test.util.EventObserver;
import com.sun.istack.internal.Nullable;

public class TCPChennel  implements Channel {
	
	private static final String MODULE = "TCP-CHENNEL";
	private String name;
	private InetAddress inetAddress;
	private int port;
	
	@Nullable private ServerSocket serverSocket = null;
	@Nullable private Socket  socket = null;
	
	private AsynchWriter writer;
	private AsynchReader reader;
	private final List<EventObserver<String, DiameterPacket>> eventObservers;
	private final List<ChannelEventListener> eventListeners;
	private String action;
	private boolean isClosed;
	


	public TCPChennel(String name, InetAddress inetAddress, int port, String action) {
		super();
		this.name = name;
		this.inetAddress = inetAddress;
		this.port = port;
		this.action = action;
		this.eventObservers = new ArrayList<EventObserver<String,DiameterPacket>>();
		this.eventListeners = new ArrayList<ChannelEventListener>();
		
	}

	@Override
	public void write(DiameterPacket diameterPacket) throws ChennelClosedException{
		if(isClose()) {
			throw new ChennelClosedException(getName());
		}
		writer.write(diameterPacket);
		
		if(getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Diameter packet send successfully");
			LogManager.getLogger().info(MODULE, diameterPacket.toString());			
		}
	}

	@Override
	public void open() throws Exception {
		
		if("connect".equals(action)) {
			socket = new Socket(inetAddress, port);
			
		} else {
			serverSocket = new ServerSocket(port, 1, inetAddress);
			serverSocket.setSoTimeout(0);
			socket = serverSocket.accept();	
		}
		
		this.writer = new AsynchWriter(socket.getOutputStream());
		this.reader = new AsynchReader(new DiameterInputStream(socket.getInputStream()));
		writer.start(); 
		reader.start();
	}

	@Override
	public synchronized void close() {
		
		if(isClosed == true){
			return;
		}
		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Closing TCP chennel");
		
		writer.interrupt();
		reader.interrupt();
		closeQuitely(socket);
		Closeables.closeQuietly(serverSocket);
		isClosed = true;
		
		for(ChannelEventListener chennelEventListener : eventListeners){
			chennelEventListener.chennelClosed();
		}
		
		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "TCP chennel closed");
	}

	private void closeQuitely(Socket socket){
		try{
			socket.close();
		} catch (IOException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	

	
	
	private class AsynchWriter extends Thread{
		public LinkedBlockingQueue<WriterData> writeQueue = new LinkedBlockingQueue<WriterData>();
		public OutputStream outputStream;
		
		public AsynchWriter(OutputStream outputStream) {
			super();
			this.outputStream = outputStream;
		}

		@Override
		public void run() {
			
			Exception e = null;
			
			try {
				while(Thread.currentThread().isInterrupted() == false){
					WriterData data = writeQueue.take();
					DiameterPacket diameterPacket = data.diameterPacket;
					diameterPacket.writeTo(outputStream);
				}
			}catch (IOException ioException) {
				getLogger().trace(MODULE, ioException);
				close();
				e = ioException;
			} catch (InterruptedException interraptedEx) {
				getLogger().trace(MODULE,interraptedEx);
				e = interraptedEx;
			} finally {
				
				if(e == null){
					e = new InterruptedException("Chennel is closed, Interrupting write process");
				}
				
				writeQueue.clear();
			}
		}
		
		public void write(DiameterPacket diameterPacket){
			writeQueue.offer(new WriterData(diameterPacket));
		}
		
		private class WriterData{
			private DiameterPacket diameterPacket;
			public WriterData(DiameterPacket diameterPacket) {
				super();
				this.diameterPacket = diameterPacket;
			}
			
		}
		
	}
	
	private class AsynchReader extends Thread{

		
		private DiameterInputStream stream;
		
		public AsynchReader(DiameterInputStream stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			Exception e = null;
			try{ 
				while(Thread.currentThread().isInterrupted() == false){
					try {
						DiameterPacket diameterPacket = stream.readDiameterPacket();
						
						for(EventObserver<String, DiameterPacket> eventObserver : eventObservers){
							eventObserver.onEvent(RECEIVE_PACKET, diameterPacket);
						}
					} catch (IOException ioException) {
						throw ioException;
					} catch (Exception exception) {
						LogManager.getLogger().trace(MODULE,e);
					}
				}
			} catch(IOException ex){
				getLogger().trace(MODULE, ex);
				close();
				e = ex;
			}
			
		}
		
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void registerEventObserver(
			EventObserver<String, DiameterPacket> eventObserver) {
		this.eventObservers.add(eventObserver);
		
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public boolean isClose() {
		return isClosed;
	}

	@Override
	public void registerChnnelEventListener(ChannelEventListener event){
		this.eventListeners.add(event);
	}
	

}
