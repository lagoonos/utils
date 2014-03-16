package org.lagoonos.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 	Utility class with helper methods for Collection classes.
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
public class CollectionUtils {

	public static void removeMultipleIndexes(List<?> list,
			final List<Integer> removeIds) {
		java.util.Collections.sort(removeIds, new Comparator<Integer>() {
			@Override
			public int compare(Integer i1, Integer i2) {
				return i2.compareTo(i1);
			}

		});
		for (Integer idx : removeIds) {
			list.remove(idx.intValue());
		}
	}

	public static boolean checkForEqualityOnAllValues(Collection<?> values) {
		Object o = values.iterator().next();
		for (Object oCompare : values) {
			if (!o.equals(oCompare)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes list of items from another list and returns a new list with the
	 * result. Useful to create a new list without a subset. The function
	 * doesn't effect the original lists.
	 * 
	 * @param items
	 * @param oneInstanceItems
	 * @return
	 */
	public static List<?> removeItems(List<?> items, List<?> removings) {
		List<?> sublist = copyList(items);
		sublist.removeAll(removings);
		return sublist;
	}

	/**
	 * Creates a new list and copies the original (no cloning!) values into the
	 * new list.
	 * 
	 * @param items
	 * @return
	 */
	public static List<?> copyList(List<?> items) {
		List<Object> copy = new ArrayList<Object>();
		for (Object o : items) {
			copy.add(o);
		}
		return copy;
	}

	public static boolean isSublist(final List<?> sublist, final List<?> parent) {
		return indexOfSublist(sublist, parent)>-1;
	}

	public static int indexOfSublist(List<?> sublist,
			List<?> parent) {
		
		boolean isSublist = false;
		Object o = sublist.get(0);
		List<?> lookUp = copyList(parent);
		int startIndex = 0;
		int tempIndex = -1;
		while((tempIndex=lookUp.indexOf(o))>-1 && tempIndex+sublist.size()<=lookUp.size() && !isSublist){
			if(sublist.size()==1){
				isSublist = true;
			}
			for(int i=1;i<sublist.size();i++){
				if(!sublist.get(i).equals(lookUp.get(tempIndex+i))){
					break;
				}else if(i==sublist.size()-1){
					isSublist = true;
				}
			}
			lookUp = lookUp.subList(tempIndex+1, lookUp.size());
			startIndex += tempIndex;
		}
		if(!isSublist){
			startIndex = -1;
		}
		return startIndex;
	}
	public static List<?> removeIndexRange(List<?> list,
			int startIndex, int endIndex) {
		List<?> workList = copyList(list);
		List sublist = new ArrayList();
		sublist.addAll(workList.subList(0, startIndex));
		sublist.addAll(workList.subList(endIndex+1, workList.size()));
		return sublist;
	}

	public static int count(Object obj, List<?> list) {
		int count = 0;
		for(Object listObj:list){
			if(obj.equals(listObj)){
				count++;
			}
		}
		return count;
	}

	public static boolean equalsAll(List<?> listA, List<?> listB) {
		if(listA.size()==listB.size()){
			for(int i=0;i<listA.size();i++){
				if(!listA.get(i).equals(listB.get(i))){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static List<?> getItemsNotIn(List<?> lookupItems,
			List<?> mustNotEqualItems) {
		
		List<Object> filtered = new ArrayList<Object>();
		for(Object lookup:lookupItems){
			if(!mustNotEqualItems.contains(lookup)){
				filtered.add(lookup);
			}
		}
		return filtered;
	}

	public static <T> List<T> getWrappedInList(
			T toBeWrapped) {
		List<T> wrapperList = new ArrayList<T>();
		wrapperList.add(toBeWrapped);
		return wrapperList;
	}
	
	public static <T> List<T> getItemsAddedToNewList(
			List<T> itemsList) {
		List<T> newList = new ArrayList<T>();
		newList.addAll(itemsList);
		return newList;
	}

	public static <T> void sortBySize(List<List<T>> lists) {
		Collections.sort(lists, new BySizeComparator());
	}
	
	private static class BySizeComparator implements Comparator<List>{
		@Override
		public int compare(List o1, List o2) {
			return Integer.compare(o1.size(), o2.size());
		}
	}

	/**
	 * Inserts a value at a specified position in a list.
	 * @param list
	 * @param insertion
	 * @param pos The position where the insertion item is inserted (inserting before and right shifting old items >=pos). I.e. pos=0 inserts a new item at position 0. The previous position 0 is then 1;
	 */
	public static <T> List<T> insertAtPosition(List<T> list,
			int pos, T ... insertion) {

		
		List<T> newList = new ArrayList<T>();
		
		List<T> pre;
		if(pos == 0){			
			pre = Collections.EMPTY_LIST;
		}else{
			pre = list.subList(0, pos);			
		}
		List<T> post = list.subList(pos, list.size());
		
		newList.addAll(pre);
		newList.addAll(createListFromIterable(insertion));
		newList.addAll(post);
		
		return newList;
	}
	
	public static <T> List<T> createListFromIterable(T[] insertion){
		
		List<T> out = new ArrayList<T>();
		for(T item:insertion){
			out.add(item);
		}
		return out;
	}
	public static <T> List<T> createListFromCollection(Collection<T> collection){
		List<T> out = new ArrayList<T>();
		for(T item:collection){
			out.add(item);
		}
		return out;
	}
}
