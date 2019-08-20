
package com.elitecore.core.commons.util.sequencer;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.elitecore.core.commons.InitializationFailedException;


/**
 * 
 * @author narendra.pathai
 *
 */
public class Sequencer implements ISequencer, Serializable{

	private static final long serialVersionUID = 1L;

	/*
	 * This String holds the start string of the sequence. Useful when the sequence wants to reset itself
	 */
	private String startSequence;
	
	/*
	 * This string holds the end string of the sequence. When the current sequence value becomes same as the end
	 * sequence then the sequencer will reset itself
	 */
	private String endSequence;
	
	 /*
	  * This holds the value of the present sequence.
	  */
	private String currentSequence;
	
	/*
	 * This is the pointer to the first node in the link list of sequences
	 */
	private Sequence head; //NOSONAR
	
	/*						  HEAD
	 -----		 -----		 -----
	|	  |CARRY|     |CARRY|     |
	|  4  |<----|  A  |<----|  a  |<----- (+1)INCREMENT
	 -----		 -----		 -----
	
	*/
	
	
	
	private boolean bIsInitialized;
	private String prefix;
	private String suffix;
	private static final String seqRegx = "[a-zA-Z0-9]+";
	
	public Sequencer(String startSequence, String endSequence, String prefix, String suffix){
		this.startSequence = startSequence;
		this.endSequence = endSequence;
		this.currentSequence = this.startSequence;
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	/**
	 * This method sets the current sequence of the sequencer
	 * @param sequence the sequence from where to resume the sequence
	 * @throws SequencerException if the sequence specified is not reachable or is invalid
	 */
	
	public void init() throws InitializationFailedException{
		if(startSequence != null && endSequence != null && startSequence.trim().length() > 0 && endSequence.trim().length() > 0
				&& endSequence.length() >= startSequence.length()){
		
			if(!Pattern.matches(seqRegx, startSequence) && !Pattern.matches(seqRegx, endSequence))
				throw new InitializationFailedException("Invalid sequence specified");
			
			if(startSequence.length() == endSequence.length()){
				if(endSequence.compareTo(startSequence) < 0)
					throw new InitializationFailedException("Invalid sequence specified");
			}
			
			//now here will be the task of creating the character adder link list
			if(Character.isDigit(startSequence.charAt(startSequence.length()-1)))
				head = new IntegerSequenceImpl(startSequence.charAt(startSequence.length()-1), endSequence.charAt(endSequence.length()-1));
			else if(Character.isLetter(startSequence.charAt(startSequence.length()-1)))
				head = new CharacterSequenceImpl(startSequence.charAt(startSequence.length()-1), endSequence.charAt(endSequence.length()-1));
			
			Sequence temp = head;
			for(int i = endSequence.length() - 2, diff = endSequence.length() - startSequence.length();i >= 0 ; i--){
				if(i >= diff){
					if(Character.isDigit(endSequence.charAt(i)))
						temp.addNode(new IntegerSequenceImpl(startSequence.charAt(i-diff), endSequence.charAt(i)));
					else if(Character.isLetter(endSequence.charAt(i)))
						temp.addNode(new CharacterSequenceImpl(startSequence.charAt(i-diff), endSequence.charAt(i)));
				}else{
					//switch to append mode
					if(Character.isDigit(endSequence.charAt(i)))
						temp.addNode(new IntegerSequenceImpl(null, endSequence.charAt(i)));
					else if(Character.isLetter(endSequence.charAt(i)))
						temp.addNode(new CharacterSequenceImpl(null, endSequence.charAt(i)));
				}
				temp = temp.getNext();	
			}
			
			head.initThreshold();
			if(!head.isValid())
				throw new InitializationFailedException("Invalid sequence specified");
			head.cache();
			
			bIsInitialized = true;
		}else{
			throw new InitializationFailedException("Invalid sequence specified.");
		}
		
	}
	
	public String getNextSequence(){
		if(bIsInitialized){
			if(!currentSequence.equals(endSequence)){
				currentSequence = head.getSequence();
				head.increment();
				return prefix + currentSequence + suffix;
			}else{
				reset();
				head.increment();
				return prefix + currentSequence + suffix;
			}
		}
		return "";
	}
	
	private void reset(){
		head.reset();
		head.initThreshold();
		head.cache();
		currentSequence = head.getSequence();
	}
	
	@Override
	public String getSequence(){
		if(bIsInitialized){
			return prefix + head.getSequence() + suffix;
		}
		return "";
	}
	
	@Override
	public void increment(){
		if(currentSequence.equalsIgnoreCase(endSequence)){
			reset();
		}else{
			head.increment();
			currentSequence = head.getSequence();
		}
	}
	
	@Override
	public boolean equals(Object obj){
		
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof ISequencer){
			ISequencer sequencer = (ISequencer)obj;
			if(isInitialized() == sequencer.isInitialized() && getStartSequence().equals(sequencer.getStartSequence())
					&& getEndSequence().equals(sequencer.getEndSequence()) && getPrefix().equals(sequencer.getPrefix())
					&& getSuffix().equals(sequencer.getSuffix())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getPrefix(){
		return prefix;
	}
	
	@Override
	public String getSuffix(){
		return suffix;
	}
	
	@Override
	public boolean isInitialized(){
		return bIsInitialized;
	}
	
	@Override
	public String getStartSequence(){
		return startSequence;
	}
	
	@Override
	public String getEndSequence(){
		return endSequence;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		Sequencer sequencer = (Sequencer)super.clone();
		sequencer.head = (Sequence) this.head.clone();
		return sequencer;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nStart Sequence: " + startSequence);
		buffer.append("\nCurrent Sequence: " + currentSequence);
		buffer.append("\nEnd Sequence: " + endSequence);
		buffer.append("\nPrefix: " + prefix);
		buffer.append("\nSuffix: " + suffix);
		buffer.append("\nInitialized: " + bIsInitialized);
		return buffer.toString();
	}
	
	interface Sequence extends Cloneable{

		/**
		 * This method will add 1 in the present value of the node
		 * Also if the present node gets full then it will reset its value
		 * and call increment on the next node
		 * If the next node is visible then it will add 1 in it value
		 * but if the next node is not visible then it will get visible and not 
		 * add 1 in it
		 */
		public void increment();
		
		/**
		 * This method will get the present value of the sequence
		 * It uses the cached value of the previous nodes
		 * @return PRESENT SEQUENCE
		 */
		public String getSequence();
		
		/**
		 * This method will tell whether there is a next node available
		 * @return
		 */
		public boolean hasNext();
		
		/**
		 * This node will return the next node
		 * @return returns instance of next node if available, null if there
		 * is no node next to the present node
		 */
		public Sequence getNext();
		
		/**
		 * This method tells whether the present value of the node assigned to it is actually a valid value
		 * @return
		 */
		public boolean isValid();
		
		/**
		 * This method caches the value of the sequence of nodes next to it
		 * Any present node will have the value of the sequence next to it
		 */
		public String cache();
		
		/**
		 * This method will add the next node to the present node
		 * @param next
		 */
		public void addNode(Sequence next);
		
		/**
		 * This signals whether the next nodes have already reached their end and are not in a state to bear any more carries 
		 */
		public boolean isNextThreshold();
		
		/**
		 * Initialize the threshold value for the next nodes
		 */
		public boolean initThreshold();
		
		/**
		 * This will set the current seq char of the sequence node
		 * @param currentSeq
		 */
		public void setSequence(Character currentSeq) throws InitializationFailedException;
		
		
		public void reset();
		
		public Object clone() throws CloneNotSupportedException;
			
	}
	
	
	class CharacterSequenceImpl implements Sequence,Serializable{

		private static final long serialVersionUID = 1969786496770161515L;
		private Character startChar;
		private Character endChar;
		private Character currentChar;
		private Sequence nextSequenceNode; //NOSONAR
		private boolean bIsVisible;
		private boolean bIsNextThreshold;
		private String cache;
		private char MAX_VAL;
		
		CharacterSequenceImpl(Character startChar, Character endChar) throws InitializationFailedException{
			this.startChar = startChar;
			this.cache = "";
			this.bIsVisible = startChar!=null;
			this.endChar = endChar;
			if(bIsVisible){
				if(!(Character.isLetter(this.startChar) && Character.isLetter(this.endChar)))
						throw new InitializationFailedException("Invalid sequence specified");
				else if(!(Character.isUpperCase(endChar) && Character.isUpperCase(startChar))){
					if(!(Character.isLowerCase(endChar) && Character.isLowerCase(startChar))){
						throw new InitializationFailedException("Invalid sequence specified");
					}

				}
			}
			this.MAX_VAL = Character.isUpperCase(this.endChar)?'Z':'z';
			this.currentChar = this.startChar;
		}
		
		@Override
		public void increment() {
			if(bIsVisible){
				if(bIsNextThreshold){
					if(currentChar == endChar){
						//this is a critical region
					}else{
						currentChar++;
					}
				}else{
					if(currentChar == MAX_VAL){
						if(hasNext()){
							/*
							 * This will reset the value of the char node from 'Z' to 'A' or 
							 * from 'z' to 'a'
							 */
							currentChar = (char) (currentChar - 25);
							getNext().increment();
							bIsNextThreshold = getNext().isNextThreshold();
							cache = getNext().cache();
						}else{

						}
					}else{
						currentChar++;
					}
				}
			}else{
				currentChar = (char) (MAX_VAL - 25);
				bIsVisible = true;
			}
		}

		@Override
		public String getSequence() {
			if(bIsVisible)
				return cache + currentChar;
			else return "";
		}

		@Override
		public boolean hasNext() {
			return nextSequenceNode!=null;
		}

		@Override
		public Sequence getNext() {
			return nextSequenceNode;
		}

		@Override
		public boolean isValid() {
			if(hasNext()){
				if(getNext().isValid()){
					if(bIsNextThreshold){
						return currentChar<=endChar;
					}else{
						return true;
					}
				}else{
					return false;
				}
			}else{
				if(bIsVisible){
					return currentChar<=endChar;
				}else{
					/*if(endChar == '0')
						return false;*/
					return true;
				}
			}
		}

		@Override
		public String cache() {
			if(bIsVisible){
				if(hasNext()){
					cache = getNext().cache();
					return cache + currentChar;
				}else{
					return cache + currentChar;
				}
			}else{
				return "";
			}
		}


		@Override
		public void addNode(Sequence next) {
			this.nextSequenceNode = next;
		}

		@Override
		public boolean isNextThreshold() {
			if(hasNext()){
				if(bIsNextThreshold)
					return currentChar == endChar;
				else return false;
			}else{
				return currentChar == endChar;
			}
		}

		@Override
		public void setSequence(Character currentSeq) throws InitializationFailedException {
			if(!isValidChar(currentSeq))
				throw new InitializationFailedException("Invalid sequence specified");
			this.currentChar = currentSeq;
			this.bIsVisible = true;
		}

		@Override
		public boolean initThreshold() {
			if(hasNext()){
				bIsNextThreshold = getNext().initThreshold();
				return (bIsNextThreshold)?(currentChar==endChar):false;
			}else{
				if(bIsVisible){
					if(currentChar == endChar)
						return true;
					else return false;
				}else{
					return false;
				}
			}
		}

		@Override
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("current char : " + currentChar + "\n" );
			buffer.append("end char : " + endChar + "\n" );
			buffer.append("Threshold : " + bIsNextThreshold + "\n" );
			buffer.append("Cache : " + cache + "\n" );
			buffer.append("Visibility :" + bIsVisible + "\n");
			buffer.append("Next : \t" + nextSequenceNode + "\n" );
			return buffer.toString();
		}

		@Override
		public void reset() {
			currentChar = startChar;
			bIsVisible = startChar!=null;
			bIsNextThreshold = false;
			cache = "";
			if(hasNext())
				getNext().reset();
		}
		
		private boolean isValidChar(Character sequenceChar){
			return Character.isLetter(sequenceChar);
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException{
			CharacterSequenceImpl charSeq = null; 
			charSeq = (CharacterSequenceImpl)super.clone();
			if(nextSequenceNode != null)
				charSeq.nextSequenceNode = (Sequence) this.nextSequenceNode.clone();
			else
				charSeq.nextSequenceNode = null;
			return charSeq;
		}
	}
	
	class IntegerSequenceImpl implements Sequence,Serializable{

		private static final long serialVersionUID = -791143136049625709L;
		private Character startChar;
		private Character endChar;
		private Character currentChar;
		private Sequence nextSequenceNode; //NOSONAR
		private boolean bIsVisible;
		private boolean bIsNextThreshold;
		private String cache;
		
		
		IntegerSequenceImpl(Character startChar, Character endChar) throws InitializationFailedException{
			this.startChar = startChar;
			this.cache = "";
			this.bIsVisible = startChar!=null;
			this.endChar = endChar;
			if(bIsVisible){
				if(!(Character.isDigit(this.startChar) && Character.isDigit(this.endChar)))
						throw new InitializationFailedException("Invalid sequence specified");
			}
			this.currentChar = this.startChar;
		}
		
		@Override
		public void increment() {
			if(bIsVisible){
				if(bIsNextThreshold){
					if(currentChar == endChar){
						//this is a critical region
					}else{
						currentChar++;
					}
				}else{
					if(currentChar == '9'){
						if(hasNext()){
							currentChar = '0';
							getNext().increment();
							bIsNextThreshold = getNext().isNextThreshold();
							cache = getNext().cache();
						}else{
							
						}
					}else{
						currentChar++;
					}
				}
			}else{
				currentChar = '1';
				bIsVisible = true;
			}
		}

		@Override
		public String getSequence() {
			if(bIsVisible)
				return cache + currentChar;
			else return "";
		}

		@Override
		public boolean hasNext() {
			return nextSequenceNode!=null;
		}

		@Override
		public Sequence getNext() {
			return nextSequenceNode;
		}

		@Override
		public boolean isValid() {
			if(hasNext()){
				if(getNext().isValid()){
					if(bIsNextThreshold){
						return currentChar<=endChar;
					}else{
						return true;
					}
				}else{
					return false;
				}
			}else{
				if(bIsVisible){
					return currentChar<=endChar;
				}else{
					if(endChar == '0')
						return false;
					return true;
				}
			}
		}

		@Override
		public String cache() {
			if(bIsVisible){
				if(hasNext()){
					cache = getNext().cache();
					return cache + currentChar;
				}else{
					return cache + currentChar;
				}
			}else{
				return "";
			}
		}

		@Override
		public void addNode(Sequence next) {
			this.nextSequenceNode = next;
		}

		@Override
		public boolean isNextThreshold() {
			if(hasNext()){
				if(bIsNextThreshold)
					return currentChar == endChar;
				else return false;
			}else{
				return currentChar == endChar;
			}
		}

		@Override
		public void setSequence(Character currentSeq) throws InitializationFailedException {
			if(!isValidChar(currentSeq))
				throw new InitializationFailedException("Invalid sequence specified");
			this.currentChar = currentSeq;
			this.bIsVisible = true;
		}

		@Override
		public boolean initThreshold() {
			if(hasNext()){
				bIsNextThreshold = getNext().initThreshold();
				return (bIsNextThreshold)?(currentChar==endChar):false;
			}else{
				if(bIsVisible){
					if(currentChar == endChar)
						return true;
					else return false;
				}else{
					return false;
				}
			}
		}

		@Override
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("current char : " + currentChar + "\n" );
			buffer.append("end char : " + endChar + "\n" );
			buffer.append("Threshold : " + bIsNextThreshold + "\n" );
			buffer.append("Cache : " + cache + "\n" );
			buffer.append("Visibility :" + bIsVisible + "\n");
			buffer.append("Next : \t" + nextSequenceNode + "\n" );
			return buffer.toString();
		}

		@Override
		public void reset() {
			currentChar = startChar;
			bIsVisible = startChar!=null;
			bIsNextThreshold = false;
			cache = "";
			if(hasNext())
				getNext().reset();
		}

		private boolean isValidChar(Character sequenceChar){
			return Character.isDigit(sequenceChar);
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException{
			IntegerSequenceImpl intSeq = null; 
			intSeq = (IntegerSequenceImpl)super.clone();
			if(nextSequenceNode != null)
				intSeq.nextSequenceNode = (Sequence) this.nextSequenceNode.clone();
			else
				intSeq.nextSequenceNode = null;
			return intSeq;
		}
	}

}
