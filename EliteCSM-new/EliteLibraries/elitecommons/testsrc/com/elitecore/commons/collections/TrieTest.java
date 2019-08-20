package com.elitecore.commons.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.logging.LogManager;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TrieTest {

	private static final String MODULE = "TRIE-TEST";
	private static final String ANY_STRING = "anyStr";
	private static final String KEYS1 = "123";
	private static final String KEYS2 = "124";
	private Trie<String> trieUnderTest;
	
	
	@Test
	public void testPut_InsertSingleBranch() {

		trieUnderTest = new Trie<String>();
		trieUnderTest.put(KEYS1, ANY_STRING);
		
		/*
		 * Creating expected tree with branch
		 * 1
		 *   2
		 *      3(ANY_STRING)
		 */
		Map<Character, TrieNode<String>> map = new HashMap<Character, TrieNode<String>>();
		TrieNode<String> subNode2 = new TrieNode<String>();
		TrieNode<String> subNode3 = new TrieNode<String>();
		subNode3.put('3', new TrieNode<String>(ANY_STRING));
		subNode2.put('2', subNode3);
		map.put('1', subNode2);
		Trie<String> expectedTree = new Trie<String>(map);
		
		assertEquals(expectedTree, trieUnderTest);
	}
	
	@Test
	@Parameters(method="dataFor_testPut")
	public void testPut(TriePutCaseData triePutCaseData) {
		
		LogManager.getLogger().info(MODULE, "Executing Case: " + triePutCaseData.getName());
		buildTrie(triePutCaseData);
		trieUnderTest.put(triePutCaseData.getKeys(), triePutCaseData.getValue());
		
		assertEquals(triePutCaseData.createExpectedTree(), trieUnderTest);
	}
	
	public static Object[] dataFor_testPut() 
			throws UnsupportedEncodingException, FileNotFoundException, JAXBException, IOException {

		ClasspathResource resource = ClasspathResource.at("trietestcasexmls/put.xml");
		TriePutCaseCollection trieNodeData = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()), 
				TriePutCaseCollection.class);
		return trieNodeData.getCases().toArray(new Object[]{});
	}
	
	
	private void buildTrie(TriePutCaseData triePutCaseData) {
		trieUnderTest = new Trie<String>();
		if(Collectionz.isNullOrEmpty(triePutCaseData.getBranches())) {
			return;
		}
		for (Branch branch : triePutCaseData.getBranches()) {
			trieUnderTest.put(branch.getKey(), branch.getValue());
		}
	}
	
	@Test
	@Parameters(method="dataFor_testLongestPrefixKeyMatch_ForSuccessfulMatch")
	public void testLongestPrefixKeyMatch_ForSuccessfulMatch(TrieMatchCaseData trieFetchCaseData) {

		LogManager.getLogger().info(MODULE, "Executing Case: " + trieFetchCaseData.getName());
		
		trieUnderTest = trieFetchCaseData.createTree();
		
		assertEquals(trieFetchCaseData.getExpectedValue(), 
				trieUnderTest.longestPrefixKeyMatch(trieFetchCaseData.getQueryString()));
	}
	
	public static Object[] dataFor_testLongestPrefixKeyMatch_ForSuccessfulMatch() 
			throws UnsupportedEncodingException, FileNotFoundException, JAXBException, IOException {

		return getCaseDataFromXml("match-found.xml")
				.getCases().toArray(new Object[]{});
	}

	private static TrieMatchCaseCollection getCaseDataFromXml(String filname)
			throws JAXBException, FileNotFoundException,
			UnsupportedEncodingException {
		ClasspathResource resource = ClasspathResource.at("trietestcasexmls" + File.separator + filname);
		return ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()), 
				TrieMatchCaseCollection.class);
	}
	
	
	@Test
	@Parameters(method="dataFor_testLongestPrefixKeyMatch_ForUnSuccessfulMatch")
	public void testLongestPrefixKeyMatch_ForUnSuccessfulMatch(TrieMatchCaseData trieFetchCaseData) {

		LogManager.getLogger().info(MODULE, "Executing Case: " + trieFetchCaseData.getName());
		
		trieUnderTest = trieFetchCaseData.createTree();
		
		assertEquals(trieFetchCaseData.getExpectedValue(), 
				trieUnderTest.longestPrefixKeyMatch(trieFetchCaseData.getQueryString()));
	}
	
	public static Object[] dataFor_testLongestPrefixKeyMatch_ForUnSuccessfulMatch() 
			throws UnsupportedEncodingException, FileNotFoundException, JAXBException, IOException {

		return getCaseDataFromXml("match-not-found.xml")
				.getCases().toArray(new Object[]{});
	}
	
	@Test
	@Parameters(method="dataFor_testLongestPrefixKeyMatch_ForSuccessfulBackTrack_WhenFurthurBranchCanNotBeExplored")
	public void testLongestPrefixKeyMatch_ForSuccessfulBackTrack_WhenFurthurBranchCanNotBeExplored(TrieMatchCaseData trieFetchCaseData) {

		LogManager.getLogger().info(MODULE, "Executing Case: " + trieFetchCaseData.getName());
		
		trieUnderTest = trieFetchCaseData.createTree();
		
		assertEquals(trieFetchCaseData.getExpectedValue(), 
				trieUnderTest.longestPrefixKeyMatch(trieFetchCaseData.getQueryString()));
	}
	
	public static Object[] dataFor_testLongestPrefixKeyMatch_ForSuccessfulBackTrack_WhenFurthurBranchCanNotBeExplored() 
			throws UnsupportedEncodingException, FileNotFoundException, JAXBException, IOException {

		return getCaseDataFromXml("match-with-backtrack.xml")
				.getCases().toArray(new Object[]{});
	}
	
	
	@Test
	public void testEquals_ShouldReturnTrue_ForEmptyTrie() {

		trieUnderTest = new Trie<String>();
		Trie<String> clone = new Trie<String>();
		
		assertTrue(trieUnderTest.equals(clone));
	}
	
	@Test
	public void testEquals_ShouldReturnTrue_IrrespectiveOfBranchOrder() {

		trieUnderTest = new Trie<String>();
		trieUnderTest.put(KEYS1, ANY_STRING);
		trieUnderTest.put(KEYS2, ANY_STRING);

		Trie<String> clone = new Trie<String>();
		clone.put(KEYS2, ANY_STRING);
		clone.put(KEYS1, ANY_STRING);

		assertTrue(trieUnderTest.equals(clone));
	}
	
	@Test
	public void testEquals_ShouldReturnFalse_ForDifferentBranchValues() {

		trieUnderTest = new Trie<String>();
		trieUnderTest.put(KEYS1, ANY_STRING);

		Trie<String> clone = new Trie<String>();
		clone.put(KEYS1, KEYS2);

		assertFalse(trieUnderTest.equals(clone));
	}
	
	@Test
	public void testEquals_ShouldReturnFalse_ForDifferentBranches() {

		trieUnderTest = new Trie<String>();
		trieUnderTest.put(KEYS1, ANY_STRING);

		Trie<String> clone = new Trie<String>();
		clone.put(KEYS2, ANY_STRING);

		assertFalse(trieUnderTest.equals(clone));
	}
	
	@Test
	public void testEquals_ShouldReturnFalse_ForNullObject() {

		trieUnderTest = new Trie<String>();
		trieUnderTest.put(KEYS1, ANY_STRING);

		assertFalse(trieUnderTest.equals(null));
	}
	
	@Test
	public void testClear_ShouldClearAllBranches() {

		trieUnderTest = new Trie<String>();
		trieUnderTest.put(KEYS1, ANY_STRING);
		trieUnderTest.put(KEYS2, ANY_STRING);

		assertFalse(trieUnderTest.equals(new Trie<String>()));
	}
	
	@Test
	public void testEquals_ShouldReturnFalse_ForNotInstanceOfTrie() {

		trieUnderTest = new Trie<String>();
		assertFalse(trieUnderTest.equals(ANY_STRING));
	}
	
}