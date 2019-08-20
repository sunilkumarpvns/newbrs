package com.elitecore.core.commons.util.sequencer;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import com.elitecore.core.commons.InitializationFailedException;


public class SequencerTest {

	@Test
	public void testIntSequencing(){
		Sequencer seq;
		try{
			seq = new Sequencer("1", "001","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		try{
			seq = new Sequencer("31", "101","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		try{
			seq = new Sequencer("31", "021","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		try{
			seq = new Sequencer("1", "111","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		try{
			seq = new Sequencer("11", "011","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		try{
			seq = new Sequencer("11", "01","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		try{
			seq = new Sequencer("99", "100","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		try{
			seq = new Sequencer("000", "000","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		try{
			seq = new Sequencer("11", "11","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		try{
			seq = new Sequencer("000", "00","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		try{
			seq = new Sequencer("909", "990","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		try{
			seq = new Sequencer("0", "0","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		try{
			seq = new Sequencer("0", "1","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		
	}
	
	@Test
	public void testCharSequencing(){
		Sequencer seq;
		try{
			seq = new Sequencer("z", "a","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("Z", "A","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("A", "z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("a", "Z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("az", "aa","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("az", "az","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		
		try{
			seq = new Sequencer("AZ", "AZ","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		
		try{
			seq = new Sequencer("Az", "AZ","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("A", "A","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		
		try{
			seq = new Sequencer("z", "Z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("Z", "z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
	}
	
	@Test
	public void testAlphaNumericSequencing(){
		Sequencer seq;
		try{
			seq = new Sequencer("z9", "a1","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("a1", "z9","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		
		try{
			seq = new Sequencer("1a", "29","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("2aa", "9az","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		
		try{
			seq = new Sequencer("a22", "a22","","");
			seq.init();
			assertEquals(true, true);
		}catch (InitializationFailedException ex) {
			assertEquals(true, false);
		}
		
		try{
			seq = new Sequencer("1", "z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("11", "0z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("11", "9z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("a11", "90z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("11111", "aaa0z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
		
		try{
			seq = new Sequencer("11111", "aaa0z","","");
			seq.init();
			assertEquals(true, false);
		}catch (InitializationFailedException ex) {
			assertEquals(true, true);
		}
	}
	
	@Test
	public void testSequenceRange(){
		String sequenceRange = "[a-z]";
		String regx = "[a-z0-9A-Z]*\\[[a-z0-9A-Z]+\\-[a-z0-9A-Z]+\\][a-z0-9A-Z]*";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, true);
		}else{
			assertEquals(true, false);
		}
		
		sequenceRange = "[a--z]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "[a@-z]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "[a-z@]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "[a!a-b^z]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "a-z]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "[a-z";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "a-z";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "[az]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "az";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
		
		sequenceRange = "test[a-z]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, true);
		}else{
			assertEquals(true, false);
		}
		
		sequenceRange = "[a-z]test";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, true);
		}else{
			assertEquals(true, false);
		}
		
		sequenceRange = "test[a-z]test";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, true);
		}else{
			assertEquals(true, false);
		}
		
		sequenceRange = "[[a-z]]";
		if(Pattern.matches(regx, sequenceRange)){
			assertEquals(true, false);
		}else{
			assertEquals(true, true);
		}
	}
	
	@Test
	public void startRangeCanBeSameAsEndRange() throws InitializationFailedException {
		Sequencer seq = new Sequencer("a", "a", null, null);
		seq.init();
	}
}
