package com.elitecore.diameterapi.diameter.common.util.identifierpool;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HopByHopPool {

	private static AtomicInteger nextNumber;

	static{
		Random random = new Random();
		nextNumber = new AtomicInteger();
		try {
			nextNumber.set(random.nextInt((int)(System.currentTimeMillis()/1000)));
		} catch (Exception e){
			nextNumber.set(random.nextInt());
	}	
		}

	public static int get() {
		nextNumber.compareAndSet(Integer.MAX_VALUE, 0);
		return nextNumber.incrementAndGet();
	
		}
	}
