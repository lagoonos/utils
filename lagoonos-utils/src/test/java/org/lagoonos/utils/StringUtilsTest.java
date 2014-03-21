package org.lagoonos.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void readFileAsStringTest() throws IOException{
		String stringFromFile = StringUtils.readFileAsString(getClass().getClassLoader().getResource("fileToBeRead.txt").getFile());
		String expected = "Test Textfile"
				+ "\nnextline"
				+ "\nend";
		assertEquals(expected, stringFromFile);
	}
	
	@Test
	public void readResourceAsStringTest() throws IOException{
		String stringFromFile = StringUtils.readResourceAsString("fileToBeRead.txt");
		String expected = "Test Textfile"
				+ "\nnextline"
				+ "\nend";
		assertEquals(expected, stringFromFile);
	}
	
	@Test
	public void convertStreamToStringTest() throws IOException{
		String response = StringUtils.convertStreamToString(null);
		assertEquals("", response);
	}
	
	@Test
	public void getEncapsulatedString_NoBorders_Test(){
		String result = StringUtils.getEncapsulatedString("abcstart_tobefound_endefg", "start", "end", false);
		assertEquals("_tobefound_", result);
	}
	
	@Test
	public void getEncapsulatedString_WithBorders_Test(){
		String result = StringUtils.getEncapsulatedString("abcstart_tobefound_endefg", "start", "end", true);
		assertEquals("start_tobefound_end", result);
	}
	
	@Test
	public void getEncapsulatedString_WrongStart_Test(){
		String result = StringUtils.getEncapsulatedString("abcstart_tobefound_endefg", "xxx", "end", true);
		assertEquals("", result);
	}
	
	@Test
	public void getEncapsulatedString_WrongEnd_Test(){
		String result = StringUtils.getEncapsulatedString("abcstart_tobefound_endefg", "start", "xxx", true);
		assertEquals("", result);
	}
	
	@Test
	public void getEncapsulatedStrings_NoBorders_Test(){
		List<String> results = StringUtils.getEncapsulatedStrings("abcstart_foundone_endefgstart_foundtwo_end", "start", "end", false);
		assertEquals(results.get(0), "_foundone_");
		assertEquals(results.get(1), "_foundtwo_");
	}
	
	@Test
	public void getEncapsulatedStrings_WithBorders_Test(){
		List<String> results = StringUtils.getEncapsulatedStrings("abcstart_foundone_endefgstart_foundtwo_end", "start", "end", true);
		assertEquals("start_foundone_end", results.get(0));
		assertEquals("start_foundtwo_end", results.get(1));
	}
	
	@Test
	public void getEncapsulatedStrings_WrongStart_Test(){
		List<String> results = StringUtils.getEncapsulatedStrings("abcstart_foundone_endefgstart_foundtwo_end", "xxx", "end", false);
		assertEquals(results.size(), 0);
	}
	
	@Test
	public void getEncapsulatedStrings_WrongEndTest(){
		List<String> results = StringUtils.getEncapsulatedStrings("abcstart_foundone_endefgstart_foundtwo_end", "start", "xxx", false);
		assertEquals(results.size(), 0);
	}
	
	@Test
	public void toLowerCaseBeginTest(){
		String value = StringUtils.toLowerCaseBegin("Testok");
		assertEquals("testok", value);
	}
	
	@Test
	public void toUpperCaseBeginTest(){
		String value = StringUtils.toUpperCaseBegin("testok");
		assertEquals("Testok", value);
	}
	
	@Test
	public void removeFromStringTest(){
		String result = StringUtils.removeFromString("txxesiitxx syucvwzcess", new String[]{"xx","ii","vwz", "y"});
		assertEquals("test success", result);
	}
	
	@Test
	public void countOccurrenceTest(){
		int num = StringUtils.countOccurrence("teststrings", "s");
		assertEquals(3, num);
	}
	
	@Test
	public void escapeRegExCharactersTest(){
		String escaped = StringUtils.escapeRegExCharacters("\\$[](){}|^*+?.");
		assertEquals("\\\\\\$\\[\\]\\(\\)\\{\\}\\|\\^\\*\\+\\?\\.", escaped);
	}
	
	@Test
	public void replaceAtPositionTest(){
		String replaced = StringUtils.replaceAtPosition("a_text_that_is_to_be_replaced",12, 20, "was");
		assertEquals("a_text_that_was_replaced", replaced);
	}
	
	@Test
	public void endIndexOfTest(){
		int i = StringUtils.endIndexOf("teststring", "str");
		assertEquals(7, i);
		i = StringUtils.endIndexOf("teststring", "notfound");
		assertEquals(-1, i);
	}
	
	@Test
	public void getLongestCommonSubstringTest(){
		String result = StringUtils.getLongestCommonSubstring("abcuvwxyzabc","uvwxyzzzabc");
		assertEquals("uvwxyz", result);
	}
	
	@Test
	public void getDistinctValuesTest(){
		
		List<String> testinput = new ArrayList<String>();
		testinput.add("aaa");
		testinput.add("bbb");
		testinput.add("bbb");
		testinput.add("aaa");
		testinput.add("ccc");
		List<String> output = StringUtils.getDistinctValues(testinput);
		
		List<String> expected = createTestList();
		
		assertArrayEquals(expected.toArray(new String[0]), output.toArray(new String[0]));
	}
	
	@Test
	public void isContainedInArrayTest(){
		boolean isContained = StringUtils.isContainedInArray("bb", new String[]{"a","bb","ccc"});
		assertEquals(true, isContained);
		
		isContained = StringUtils.isContainedInArray("yyy", new String[]{"a","bb","ccc"});
		assertEquals(false, isContained);
	}
	
	@Test
	public void countOccurrenceInListTest(){
		List<String> testinput = new ArrayList<String>();
		testinput.add("aaa");
		testinput.add("bbb");
		testinput.add("aa");
		testinput.add("bbb");
		testinput.add("aaa");
		testinput.add("ccc");
		int count = StringUtils.countOccurrenceInList(testinput, "aaa");
		assertEquals(2, count);
	}
	
	@Test
	public void copyListTest(){
		List<String> testinput = createTestList();
		List<String> copiedList = StringUtils.copyList(testinput);
		for(int i=0; i< testinput.size();i++){
			assertTrue(testinput.get(i) == copiedList.get(i));
		}
	}
	
	@Test
	public void containsWhitespaceTest(){
		boolean contains = StringUtils.containsWhitespace("conta	ins");
		assertEquals(true, contains);
		contains = StringUtils.containsWhitespace("conta ins");
		assertEquals(true, contains);
		contains = StringUtils.containsWhitespace("contains");
		assertEquals(false, contains);
	}
	
	@Test
	public void isSubstringContainedInList(){
		List<String> testinput = createTestList();
		boolean contained = StringUtils.isSubstringContainedInList(testinput, "aa");
		assertEquals(true, contained);
		contained = StringUtils.isSubstringContainedInList(testinput, "aaaa");
		assertEquals(false, contained);
	}
	
	@Test
	public void replaceFirstOccurrenceTest(){
		String response = StringUtils.replaceFirstOccurrence("abcdabcdabcd", "bcd", "_first_");
		assertEquals("a_first_abcdabcd", response);
		
		response = StringUtils.replaceFirstOccurrence("abcdabcdabcd", "xyz", "_first_");
		assertEquals("abcdabcdabcd", response);
	}
	
	@Test
	public void replaceLastOccurrenceTest(){
		String response = StringUtils.replaceLastOccurrence("abcdabcdabcd", "bcd", "_last_");
		assertEquals("abcdabcda_last_", response);
		
		response = StringUtils.replaceLastOccurrence("abcdabcdabcd", "xyz", "_last_");
		assertEquals("abcdabcdabcd", response);
	}
	
	@Test
	public void trimAllItemsTest(){
		String[] testinput = new String[]{
				" aaa  ",
				"	bbb	",
				"	 	ccc 	\n"};		
		String[] actualOutput = StringUtils.trimAllItems(testinput);
		String[] expectedOutput = new String[]{"aaa", "bbb", "ccc"};
		assertArrayEquals(expectedOutput, actualOutput);
	}
	
	@Test
	public void isNotNullOrEmptyTest(){
		boolean result = StringUtils.isNotNullOrEmpty("");
		assertEquals(false, result);
		result = StringUtils.isNotNullOrEmpty(null);
		assertEquals(false, result);
		result = StringUtils.isNotNullOrEmpty("abc");
		assertEquals(true, result);
	}
	
	@Test
	public void concatStringsTest(){
		List<String> testinput = createTestList();
		String result = StringUtils.concatStrings(testinput);
		assertEquals("aaabbbccc", result);
	}
	
	@Test
	public void emptyIfNullTest(){
		String result = StringUtils.emptyIfNull(null);
		assertEquals("", result);
		result = StringUtils.emptyIfNull("abc");
		assertEquals("abc", result);
		result = StringUtils.emptyIfNull("");
		assertEquals("", result);
	}
	
	@Test
	public void replaceFirstOccurrence(){
		String testInput = "abcDEFghiDEFjkl";
		String find = "DEF";
		String replace = "XYZ";
		
		String result = StringUtils.replaceFirstOccurrence(testInput, find, replace);
		assertEquals("abcXYZghiDEFjkl", result);
		
		testInput = "DEFghiDEFjkl";
		find = "DEF";
		replace = "XYZ";
		
		result = StringUtils.replaceFirstOccurrence(testInput, find, replace);
		assertEquals("XYZghiDEFjkl", result);
	}
	
	@Test
	public void replaceLastOccurrence(){
		String testInput = "abcDEFghiDEFjkl";
		String find = "DEF";
		String replace = "XYZ";
		
		String result = StringUtils.replaceLastOccurrence(testInput, find, replace);
		assertEquals("abcDEFghiXYZjkl", result);
		
		testInput = "DEFghiDEFjklDEF";
		find = "DEF";
		replace = "XYZ";
		
		result = StringUtils.replaceLastOccurrence(testInput, find, replace);
		assertEquals("DEFghiDEFjklXYZ", result);
	}
	
	@Test
	public void endsWithIgnoreCaseTest(){
		boolean actual = StringUtils.endsWithIgnoreCase("AbCdEfG", "EFG");
		assertEquals(true, actual);
		actual = StringUtils.endsWithIgnoreCase("AbCdEfG", "efg");
		assertEquals(true, actual);
		actual = StringUtils.endsWithIgnoreCase("AbCdEfG", "EfG");
		assertEquals(true, actual);
		actual = StringUtils.endsWithIgnoreCase("AbCdEfG", "ABC");
		assertEquals(false, actual);
		actual = StringUtils.endsWithIgnoreCase("AbCd", "ABCD");
		assertEquals(true, actual);
		actual = StringUtils.endsWithIgnoreCase("AbC", "ABCD");
		assertEquals(false, actual);
	}
	
	@Test
	public void startsWithIgnoreCaseTest(){
		boolean actual = StringUtils.startsWithIgnoreCase("AbCdEfG", "ABC");
		assertEquals(true, actual);
		actual = StringUtils.startsWithIgnoreCase("AbCdEfG", "abc");
		assertEquals(true, actual);
		actual = StringUtils.startsWithIgnoreCase("AbCdEfG", "ABC");
		assertEquals(true, actual);
		actual = StringUtils.startsWithIgnoreCase("AbCdEfG", "aBc");
		assertEquals(true, actual);
		actual = StringUtils.startsWithIgnoreCase("AbCdEfG", "BCD");
		assertEquals(false, actual);
		actual = StringUtils.startsWithIgnoreCase("AbCd", "abcd");
		assertEquals(true, actual);
		actual = StringUtils.startsWithIgnoreCase("AbC", "abcd");
		assertEquals(false, actual);
	}
	
	@Test
	public void writeStringToFileTest() throws IOException{
		File temp = File.createTempFile("StringUtilsTest_tempfile", ".tmp"); 
		StringUtils.writeStringToFile("abc", temp);
		String read = StringUtils.readFileAsString(temp);
		assertEquals("abc", read);
	}
	
	@Test
	public void oneHundredPercentCoverTest(){
		List<String> testinput = createTestList();
		StringUtils.printList(testinput);
	}

	private List<String> createTestList() {
		List<String> testinput = new ArrayList<String>();
		testinput.add("aaa");
		testinput.add("bbb");
		testinput.add("ccc");
		return testinput;
	}
}
