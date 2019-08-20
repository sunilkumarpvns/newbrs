package com.elitecore.diameterapi.core.common.transport.tcp.collections;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.diameterapi.core.common.transport.priority.Priority;
import com.elitecore.diameterapi.core.common.transport.tcp.collections.WeightedFairBlockingQueue.Range;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class WeightedFairBlockingQueueTest {

	private static final int DEFAULT_CAPACITY = 15;
	private WeightedFairBlockingQueue<Element> weightedFairBlockingQueue; 
	
	public void setUp(Range range, int capacity, Element... elementsAlreadyPresentInQueue) {
		weightedFairBlockingQueue = new WeightedFairBlockingQueue
				<Element>(capacity, new FairnessPolicyStub(range));
		for (int i = 0 ; i < elementsAlreadyPresentInQueue.length ; i++) {
			weightedFairBlockingQueue.add(elementsAlreadyPresentInQueue[i]);
		}
	}
	
	@Test
	@Parameters(method="dataFor_testOffer")
	public void testOffer(boolean expectedOfferResult, Element elementToOffer, 
			Range range, int capacity, int expectedSize,
			Element... elementsAlreadyPresentInQueue) {
		setUp(range, capacity, elementsAlreadyPresentInQueue);
		assertEquals(expectedOfferResult, weightedFairBlockingQueue.offer(elementToOffer));
		assertEquals(expectedSize, weightedFairBlockingQueue.size());
	}

	public static Object[] dataFor_testOffer() {
		return $(
				$(
						true, new Element("OfferingElement", Priority.MEDIUM), 
						Range.LOW_TO_HIGH, 1, 1, new Element[]{}
				),
				$(
						true, new Element("OfferingElement", Priority.HIGH), 
						Range.LOW_TO_HIGH, 5, 1, new Element[]{}
				),
				$(
						true, new Element("OfferingElement", Priority.HIGH), 
						Range.LOW_TO_HIGH, 5, 2, new Element[]{new Element("E1", Priority.HIGH)}
				),
				$(
						true, new Element("OfferingElement", Priority.HIGH), 
						Range.LOW_TO_HIGH, 5, 5, new Element[]{new Element("E1", Priority.HIGH), 
					 									new Element("E1", Priority.MEDIUM),
					 									new Element("E1", Priority.HIGH),
					 									new Element("E1", Priority.MEDIUM)}
				),
				$(
						 true, new Element("OfferingElement", Priority.LOW), 
						 Range.LOW_TO_HIGH, 5, 1, new Element[]{}
				),
				$(
						false, new Element("OfferingElement", Priority.HIGH), 
						Range.LOW_TO_HIGH, 5, 5, new Element[]{new Element("E1", Priority.HIGH), 
					 									new Element("E1", Priority.MEDIUM),
					 									new Element("E1", Priority.HIGH),
					 									new Element("E1", Priority.MEDIUM),
					 									new Element("E1", Priority.MEDIUM)}
				),
				$(
						false, new Element("OfferingElement", Priority.LOW), 
						Range.LOW_TO_HIGH, 5, 4, new Element[]{new Element("E1", Priority.MEDIUM), 
					 									new Element("E1", Priority.MEDIUM),
					 									new Element("E1", Priority.MEDIUM),
					 									new Element("E1", Priority.MEDIUM)}
				),
				$(
						false, new Element("OfferingElement", Priority.MEDIUM), 
						Range.LOW_TO_HIGH, 5, 4, new Element[]{new Element("E1", Priority.LOW), 
					 									new Element("E1", Priority.MEDIUM),
					 									new Element("E1", Priority.MEDIUM),
					 									new Element("E1", Priority.MEDIUM)}
				)
			);
	}
	
	@Test(expected = IllegalPriorityException.class)
	@Parameters(method="dataFor_testOffer_For_Illegal_Priorities_Must_Illegal_Priority_Exception")
	public void testOffer_For_Illegal_Priorities_Must_Illegal_Priority_Exception(
			Element elementToOffer, 
			Range range) {
		setUp(range, DEFAULT_CAPACITY);
		weightedFairBlockingQueue.offer(elementToOffer);
	}
	
	public static Object[] dataFor_testOffer_For_Illegal_Priorities_Must_Illegal_Priority_Exception() {
		return $(
					$(new Element("IllegalElt", Priority.HIGH), Range.LOW_NORMAL),
					$(new Element("IllegalElt", Priority.LOW), Range.NORMAL_HIGH)
				);
	}
	
	@Test(expected = NullPointerException.class)
	public void testOffer_For_Null_Element_Must_Throw_NPE() {
		setUp(Range.LOW_TO_HIGH, DEFAULT_CAPACITY);
		weightedFairBlockingQueue.offer(null);
	}
	
	@Test
	public void testDrainTo() {
		
		ArrayList<Element> elements = new ArrayList<Element>();
		
		setUp(Range.LOW_TO_HIGH, DEFAULT_CAPACITY, 
				new Element[]{new Element("H1", Priority.HIGH),
				new Element("L1", Priority.LOW),
				new Element("M1", Priority.MEDIUM),
				new Element("H2", Priority.HIGH),
				new Element("M2", Priority.MEDIUM)
				});
		int size = weightedFairBlockingQueue.size();
		assertEquals(size, weightedFairBlockingQueue.drainTo(elements));
		Element[] expectedElements = new Element[]{new Element("H1", Priority.HIGH),
				new Element("H2", Priority.HIGH),
				new Element("M1", Priority.MEDIUM),
				new Element("M2", Priority.MEDIUM),
				new Element("L1", Priority.LOW)
				};
		
		org.junit.Assert.assertArrayEquals(expectedElements, elements.toArray(new Element[elements.size()]));
	}
	
	@Test
	@Parameters(method = "dataFor_testDrainTo_N_Elements")
	public void testDrainTo_N_Elements(int elementsToDrain, Element[] elementsAlreadyInQueue, Element[] expectedElements) {
		
		ArrayList<Element> elements = new ArrayList<Element>();
		
		setUp(Range.LOW_TO_HIGH, DEFAULT_CAPACITY, elementsAlreadyInQueue);
		assertEquals(expectedElements.length, weightedFairBlockingQueue.drainTo(elements, elementsToDrain));
		org.junit.Assert.assertArrayEquals(expectedElements, elements.toArray(new Element[elements.size()]));
	}
	
	public static Object[] dataFor_testDrainTo_N_Elements() {
		return $(
				$(
				3, new Element[]{new Element("H1", Priority.HIGH), new Element("L1", Priority.LOW),
						new Element("M1", Priority.MEDIUM),	new Element("H2", Priority.HIGH),
						new Element("M2", Priority.MEDIUM)},
				   new Element[]{new Element("H1", Priority.HIGH),
						new Element("H2", Priority.HIGH),
						new Element("M1", Priority.MEDIUM)}
				),
				$(
				12, new Element[]{new Element("H1", Priority.HIGH), new Element("L1", Priority.LOW),
						new Element("M1", Priority.MEDIUM),	new Element("H2", Priority.HIGH),
						new Element("M2", Priority.MEDIUM)},
				   new Element[]{new Element("H1", Priority.HIGH), new Element("H2", Priority.HIGH),
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM),
				   		new Element("L1", Priority.LOW)}
				)
			);
		
	}
	
	@Test
	@Parameters(method = "dataFor_testPoll")
	public void testPoll(int expectedSize, Element[] expectedPolledElements, Element[] elementsAlreadyPresentInQueue, Range range ) {
		setUp(range, DEFAULT_CAPACITY, elementsAlreadyPresentInQueue);
		for(int i = 0 ; i < expectedPolledElements.length ; i++) {
			assertEquals(expectedPolledElements[i], weightedFairBlockingQueue.poll());
		}
		if(expectedPolledElements.length == 0) {
			assertNull(weightedFairBlockingQueue.poll());
		}
		assertEquals(expectedSize, weightedFairBlockingQueue.size());
	}
	
	public static Object[] dataFor_testPoll() {
		return $(
				
				$(3, new Element[]{new Element("H1", Priority.HIGH)}, 
						new Element[]{new Element("L1", Priority.LOW), 
							new Element("H1", Priority.HIGH),
							new Element("M1", Priority.MEDIUM),
							new Element("M2", Priority.MEDIUM)},
					Range.LOW_TO_HIGH
				),
				
				$(3, new Element[]{new Element("H1", Priority.HIGH)}, 
						new Element[]{new Element("L1", Priority.LOW), 
							new Element("H1", Priority.HIGH),
							new Element("M1", Priority.MEDIUM),
							new Element("M2", Priority.MEDIUM)},
							Range.LOW_TO_HIGH
				),
				$(2, new Element[]{new Element("H1", Priority.HIGH),
								new Element("M1", Priority.MEDIUM)}, 
						new Element[]{new Element("L1", Priority.LOW), 
							new Element("H1", Priority.HIGH),
							new Element("M1", Priority.MEDIUM),
							new Element("M2", Priority.MEDIUM)},
							Range.LOW_TO_HIGH
				),
				$(1, new Element[]{new Element("H1", Priority.HIGH),
						new Element("M1", Priority.MEDIUM),
						new Element("M2", Priority.MEDIUM)}, 
					new Element[]{new Element("L1", Priority.LOW), 
						new Element("H1", Priority.HIGH),
						new Element("M1", Priority.MEDIUM),
						new Element("M2", Priority.MEDIUM)},
						Range.LOW_TO_HIGH
				),
				$(0, new Element[]{ 
						new Element("H1", Priority.HIGH),new Element("H2", Priority.HIGH),new Element("H3", Priority.HIGH),
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM),
						new Element("L1", Priority.LOW)},
					new Element[]{new Element("L1", Priority.LOW), 
						new Element("H1", Priority.HIGH),new Element("H2", Priority.HIGH),new Element("H3", Priority.HIGH),
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM)},
						Range.LOW_TO_HIGH
				),
				$(0, new Element[]{ 
						new Element("H1", Priority.HIGH),new Element("H2", Priority.HIGH),new Element("H3", Priority.HIGH),
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM)},
					new Element[]{
						new Element("H1", Priority.HIGH),new Element("H2", Priority.HIGH),new Element("H3", Priority.HIGH),
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM)},
						Range.NORMAL_HIGH
				),
				$(0, new Element[]{
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM), new Element("L1", Priority.LOW),
						new Element("M3", Priority.MEDIUM), new Element("L2", Priority.LOW)},
					new Element[]{ 
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM), new Element("M3", Priority.MEDIUM),
						new Element("L1", Priority.LOW), new Element("L2", Priority.LOW)},
						Range.LOW_NORMAL
				),
				$(0, new Element[]{
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM), 
						new Element("M3", Priority.MEDIUM)},
					new Element[]{ 
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM), new Element("M3", Priority.MEDIUM)},
						Range.LOW_TO_HIGH
				),
				$(0, new Element[]{
						new Element("M1", Priority.MEDIUM), new Element("M2", Priority.MEDIUM), new Element("L1", Priority.LOW),
						new Element("M3", Priority.MEDIUM)},
					new Element[]{ 
						new Element("M1", Priority.MEDIUM), new Element("L1", Priority.LOW),
						new Element("M2", Priority.MEDIUM), new Element("M3", Priority.MEDIUM)},
						Range.LOW_TO_HIGH
				),
				$(0, new Element[]{}, new Element[]{}, Range.LOW_TO_HIGH
				)
			);
	}
	
	@Test
	@Parameters(method = "dataFor_testPeek")
	public void testPeek(Element expectedPeekedElement, Element[] elementsAlreadyPresentInQueue) {
		setUp(Range.LOW_TO_HIGH, DEFAULT_CAPACITY, elementsAlreadyPresentInQueue);
		int size = weightedFairBlockingQueue.size();
		assertEquals(expectedPeekedElement, weightedFairBlockingQueue.peek());
		assertEquals(size, weightedFairBlockingQueue.size());
		assertEquals(expectedPeekedElement, weightedFairBlockingQueue.poll());
	}
	
	public static Object[] dataFor_testPeek() {
		return $(
				
				$(
						new Element("H1", Priority.HIGH),
						new Element[]{new Element("L1", Priority.LOW), 
							new Element("H1", Priority.HIGH),
							new Element("M1", Priority.MEDIUM),
							new Element("M2", Priority.MEDIUM)}
				),
				
				$(
						new Element("M1", Priority.MEDIUM), 
						new Element[]{new Element("L1", Priority.LOW), 
							new Element("M1", Priority.MEDIUM),
							new Element("M2", Priority.MEDIUM)}
				),
				$(
						null, new Element[]{}
				)
			);
	}

	
	class FairnessPolicyStub implements FairnessPolicy<Element> {

		private Range range;

		private FairnessPolicyStub(Range range) {
			this.range = range;
		}

		@Override
		public int prioritize(Element e) {
			return e.getPriority().val;
		}

		@Override
		public Range range() {
			return range;
		}
		
	}
}

class Element {
	
	private final String value;
	private final Priority priority;
	
	public Element(String value, Priority priority) {
		this.value = value;
		this.priority = priority;
	}

	public String getValue() {
		return value;
	}

	public Priority getPriority() {
		return priority;
	}
	
	@Override
	public boolean equals(Object obj) {

		Element e = (Element) obj;
		return e.priority == this.priority && e.value.equals(this.value);
	}
	
	@Override
	public String toString() {
		return value + "-" + priority.priority;
	}
}