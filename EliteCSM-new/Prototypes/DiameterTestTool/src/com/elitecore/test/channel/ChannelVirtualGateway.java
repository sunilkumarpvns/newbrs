package com.elitecore.test.channel;

import static org.junit.Assert.fail;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.test.dependecy.diameter.VirtualGateway;
import com.elitecore.test.dependecy.diameter.packet.DiameterAnswer;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.dependecy.diameter.packet.DiameterRequest;
import com.elitecore.test.exception.ChennelClosedException;
import com.elitecore.test.util.EventObserver;

public class ChannelVirtualGateway implements Observer,VirtualGateway,EventObserver<String, DiameterPacket> {
	
	private final ConcurrentHashMap<Integer, SettableFuture<DiameterAnswer>> answerMap;
	private final ConcurrentHashMap<String, SettableFuture<DiameterRequest>> requestMap;
	private final Object object;
	private final Channel chennel;

	public ChannelVirtualGateway(@Nonnull Channel chennel) {
		
		this.chennel = chennel;
		chennel.registerChnnelEventListener(new ChennelEventListenerImpl());
		this.answerMap = new ConcurrentHashMap<Integer,SettableFuture<DiameterAnswer>>();
		this.requestMap = new ConcurrentHashMap<String,SettableFuture<DiameterRequest>>();
		this.object = new Object();
		
	}
	
	
	@Override
	public Future<DiameterAnswer> send(@Nonnull DiameterRequest diameterRequest) throws ChennelClosedException{
		
		SettableFuture<DiameterAnswer> settableFuture = SettableFuture.create();
		chennel.write(diameterRequest);
		answerMap.put(diameterRequest.getHop_by_hopIdentifier(), settableFuture);
		
		return settableFuture;
		
	}
	
	


	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof DiameterAnswer) {
			DiameterAnswer answer = (DiameterAnswer) arg;
			SettableFuture<DiameterAnswer> result = answerMap.remove(answer.getHop_by_hopIdentifier());
			result.set(answer);
		} else if (arg instanceof DiameterRequest) {
			DiameterRequest answer = (DiameterRequest) arg;
			synchronized (object) {
				SettableFuture<DiameterRequest> result = requestMap.remove(answer.getCommandCode());
				if(result == null){
					result = SettableFuture.create();
					requestMap.put(answer.getCommandCode() + "-" + answer.getApplicationID(), result);
				}
				result.set(answer);
			}
			
		} else {
			fail("UnExpected response received" + arg);
		}
	}

	@Override
	public void send(DiameterAnswer diameterAnswer) throws ChennelClosedException{
		chennel.write(diameterAnswer);
	}
	
	@Nonnull
	@Override
	public SettableFuture<DiameterRequest> receive(String appIdAndCommandCode) {
		synchronized (object) {
			System.out.println("Search for packet:"+appIdAndCommandCode);
			SettableFuture<DiameterRequest> settableFuture = requestMap.remove(appIdAndCommandCode);
			if(settableFuture == null){
				settableFuture = SettableFuture.create();
				requestMap.put(appIdAndCommandCode, settableFuture);
			}
			return settableFuture;
		}
		
	}


	@Override
	public void onEvent(String event, DiameterPacket data) {

		if (data instanceof DiameterAnswer) {
			DiameterAnswer answer = (DiameterAnswer) data;
			SettableFuture<DiameterAnswer> result = answerMap.remove(answer.getHop_by_hopIdentifier());
			if(result != null){
				result.set(answer);
			}
		} else if (data instanceof DiameterRequest) {
			DiameterRequest request = (DiameterRequest) data;
			synchronized (object) {
				SettableFuture<DiameterRequest> result = requestMap.remove(request.getCommandCode()+"-"+request.getApplicationID());
				if(result == null){
					result = SettableFuture.create();
					requestMap.put(request.getCommandCode() + "-" + request.getApplicationID(), result);
				}
				result.set(request);
			}
			
		} else {
			fail("UnExpected response received" + data);
		}	
	}
	
	private class ChennelEventListenerImpl implements ChannelEventListener {

		@Override
		public void chennelOpen() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void chennelClosed() {
		
			for(SettableFuture<DiameterAnswer> answer : answerMap.values()) {
				if(answer.isDone() == false && answer.isCancelled() == false){
					answer.setException(new ChennelClosedException(chennel.getName()));
				}
			}
			
			for(SettableFuture<DiameterRequest> request : requestMap.values()) {
				if(request.isDone() == false && request.isCancelled() == false){
					request.setException(new ChennelClosedException(chennel.getName()));
				}
			}
		}
		
	}
	
}