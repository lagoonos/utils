package org.lagoonos.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 	Utility class with helper methods for java.lang.String operations.
 * <br/>
 * <p>
    Copyright (C) 2014  Sebastian Gerstenlauer ( gerstenlauer@gmx.net )

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
 	any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
    </p>
 * @author Sebastian Gerstenlauer ( gerstenlauer@gmx.net )
 *
 */
public class StringUtils implements Serializable{

	private static final long serialVersionUID = 1L;

	public static String readFileAsString(String filePath)
			throws java.io.IOException {
		return readFileAsString(new File(filePath));
	}

	public static String readFileAsString(File file) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static String readResourceAsString(String resourceName)
			throws IOException {
		InputStream is = StringUtils.class.getClassLoader()
				.getResourceAsStream(resourceName);
		return convertStreamToString(is);
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	public static String getEncapsulatedString(String content, String from,
			String to, boolean includeBorders) {

		String substring = "";
		int startPos = content.indexOf(from);
		if (startPos == -1)
			return "";
		int endPos = content.indexOf(to, startPos + from.length() + 1);
		if (endPos == -1)
			return "";
		if (includeBorders) {
			substring = content.substring(startPos, endPos + to.length());
		} else {
			substring = content.substring(startPos + from.length(), endPos);
		}
		return substring;
	}

	public static List<String> getEncapsulatedStrings(String content,
			String from, String to, boolean includeBorders) {

		List<String> strings = new Vector<String>();

		int startPos = content.indexOf(from);

		while (startPos > -1) {
			String substring = "";
			int endPos = content.indexOf(to, startPos + from.length() + 1);
			if (endPos == -1)
				break;

			if (includeBorders) {
				substring = content.substring(startPos, endPos + to.length());
			} else {
				substring = content.substring(startPos + from.length(), endPos);
			}
			strings.add(substring);
			startPos = content.indexOf(from, endPos + to.length() + 1);
		}
		return strings;
	}

	public static String toLowerCaseBegin(String str) {
		return str.substring(0, 1).toLowerCase() + "" + str.substring(1);
	}

	public static String toUpperCaseBegin(String str) {
		return str.substring(0, 1).toUpperCase() + "" + str.substring(1);
	}

	public static String removeFromString(String containingString,
			String[] toBeRemoved) {
		for (String remove : toBeRemoved) {
			containingString = containingString.replace(remove, "");
		}
		return containingString;
	}

	public static int countOccurrence(String container, String find) {
		Pattern p = Pattern.compile(find);
		Matcher m = p.matcher(container);
		int count = 0;
		while (m.find()) {
			count += 1;
		}
		return count;
	}
	
	public static String escapeRegExCharacters(final String nonRegExString) {
		String[] regExExpressions = {"\\", "$", "[", "]", "(", ")", "{", "}", "|", "^", "*", "+", "?", "."};
		String regExString = nonRegExString;
		for(String replace:regExExpressions){
			regExString = regExString.replace(replace, "\\"+replace);
		}
		return regExString;
	}

	public static String replaceAtPosition(final String text,
			int replacePosStart, int replacePosEnd, final String replacement) {
		String pre = text.substring(0, replacePosStart);
		String post = text.substring(replacePosEnd);
		return pre+replacement+post;
	}

	public static int endIndexOf(String string, String find) {
		int pos = string.indexOf(find);
		if(pos>-1){
			pos += find.length();
		}
		return pos;
	}

	public static List<String> getDistinctValues(List<String> strings) {
		
		List<String> distincts = new ArrayList<String>();
		for(String s:strings){
			if(!isContainedInList(s, distincts)){
				distincts.add(s);
			}
		}
		return distincts;
	}
	
	public static boolean isContainedInList(String checkValue, Iterable<String> values){
		for(String listValue:values){
			if(listValue.equals(checkValue)){
				return true;
			}
		}
		return false;
	}
	public static boolean isContainedInArray(String checkValue, String... values){
		for(String listValue:values){
			if(listValue.equals(checkValue)){
				return true;
			}
		}
		return false;
	}
	
	public static void writeStringToFile(String output, File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(output);
		writer.close();
	}
	
	public static String getLongestCommonSubstring(String string1, String string2){
		int start = 0;
	    int max = 0;
	    for (int i = 0; i < string1.length(); i++)
	    {
	        for (int j = 0; j < string2.length(); j++)
	        {
	            int x = 0;
	            while (string1.charAt(i + x) == string2.charAt(j + x))
	            {
	                x++;
	                if (((i + x) >= string1.length()) || ((j + x) >= string2.length())) break;
	            }
	            if (x > max)
	            {
	                max = x;
	                start = i;
	            }
	         }
	    }
	    return string1.substring(start, (start + max));
	}

	public static int countOccurrenceInList(List<String> itemNames, String name) {
		int cnt =0;
		for(String listedName:itemNames){
			if(listedName.equals(name)){
				cnt++;
			}
		}
		return cnt;
	}

	public static List<String> copyList(List<String> strings) {
		return (List<String>) CollectionUtils.copyList(strings);
	}

	public static boolean containsWhitespace(String lookup) {
		Pattern pat = Pattern.compile("\\s");
		Matcher mat = pat.matcher(lookup);
		return mat.find();
	}

	public static boolean isSubstringContainedInList(List<String> subList,
			String lookup) {
		for(String s:subList){
			if(s.contains(lookup)){
				return true;
			}
		}
		return false;
	}

	public static void printList(List<String> strings) {
		for(String s:strings){
			System.out.println(s);
		}
	}
	
	public static String replaceFirstOccurrence(String containingString, String find,
			String replace) {
		int pos = containingString.indexOf(find);
		if(pos<0){
			return containingString;
		}
			
		String pre = containingString.substring(0, pos);
		String post = containingString.substring(pos+find.length());
		
		return pre + replace +post;
	}
	
	public static String replaceLastOccurrence(String containingString, String find,
			String replace) {
		int pos = containingString.lastIndexOf(find);
		if(pos<0){
			return containingString;
		}
			
		String pre = containingString.substring(0, pos);
		String post = containingString.substring(pos+find.length());
		
		return pre + replace +post;
	}

	public static String[] trimAllItems(String[] toTrim) {
		String[] out = new String[toTrim.length];
		for(int i = 0; i < toTrim.length; i++){
			out[i] = toTrim[i].trim();
		}
		return out;
	}

	public static boolean startsWithIgnoreCase(String containingString, String prefix) {
		if(prefix.length()>containingString.length()){
			return false;
		}
		String containingPrefix = containingString.substring(0, prefix.length());
		return containingPrefix.equalsIgnoreCase(prefix);
	}
	
	public static boolean endsWithIgnoreCase(String containingString, String postfix) {
		if(postfix.length()>containingString.length()){
			return false;
		}
		int beginPos = containingString.length() - postfix.length();
		String containingPostfix= containingString.substring(beginPos, containingString.length());
		return containingPostfix.equalsIgnoreCase(postfix);
	}

	public static boolean isNullOrEmpty(String value) {
		return value==null || value.isEmpty();
	}
	public static boolean isNotNullOrEmpty(String value) {
		return !isNullOrEmpty(value);
	}
	
	public static String concatStrings(List<String> values) {
		String out = "";
		for(String value: values){
			out += value;
		}
		return out;
	}

	public static String emptyIfNull(final String value) {
		return value==null?"":value;
	}
}
