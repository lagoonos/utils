package org.lagoonos.utils.sql;

import static org.lagoonos.utils.StringUtils.replaceLastOccurrence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 	Helper class for creating sql insert statements from json.
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
 */
public class JsonToSql {

	public static String getSqlInsertsFromJsonResource(String resourceName) throws JsonProcessingException, IOException{
		File jsonFile = new File(JsonToSql.class.getClassLoader().getResource(resourceName).getFile());
		return getSqlInsertsFromJson(jsonFile);
	}
	
	public static String getSqlInsertsFromJson(File jsonFile) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader fileReader = new BufferedReader(
			new FileReader(jsonFile));
		JsonNode rootNode = mapper.readTree(fileReader);
		return createSQL(rootNode);
	}

	private static String createSQL(JsonNode node) {
		String sql = "";
		Iterator<Entry<String, JsonNode>> fieldsIterator = node.getFields();
		while(fieldsIterator.hasNext()){
			Entry<String, JsonNode> entry = fieldsIterator.next();
			if(!entry.getValue().isValueNode()){				
				Iterator<JsonNode> childNodeIterator = entry.getValue().getElements();
				String tableName = "";
				if(childNodeIterator.hasNext()){
					tableName = entry.getKey();
				}
				while(childNodeIterator.hasNext()){
					sql += createSQL(tableName, childNodeIterator.next());
				}
			}
		}
		return sql;
	}

	private static String createSQL(String tableName, JsonNode fieldsNode) {
		Iterator<Entry<String, JsonNode>> fieldsIterator = fieldsNode.getFields();
		Map<String, String> insertValuesMap = new HashMap<String, String>();
		
		while(fieldsIterator.hasNext()){
			Entry<String, JsonNode> entry = fieldsIterator.next();
			if(entry.getValue().isValueNode()){
				addInsertValueToMap(insertValuesMap, entry);
			}
		}
		return createSqlFromValuesMap(tableName, insertValuesMap);
	}

	private static void addInsertValueToMap(Map<String, String> insertValuesMap,
			Entry<String, JsonNode> entry) {
		String fieldName = entry.getKey();
		String value = entry.getValue().getValueAsText();
		if(entry.getValue().isTextual()){
			value = "'"+value+"'";
		}
		insertValuesMap.put(fieldName, value);
	}

	private static String createSqlFromValuesMap(String tableName, Map<String, String> insertValuesMap) {
		String sql = "INSERT INTO "+tableName;
		sql = addColumns(insertValuesMap, sql);
		sql	+= "VALUES";
		sql = addValues(insertValuesMap, sql);
		sql += ";\r\n";
		return sql;
	}

	private static String addColumns(Map<String, String> insertValuesMap,
			String sql) {
		sql += " (";
		for(String name : insertValuesMap.keySet()){
			sql += name+", ";
		}
		sql = replaceLastOccurrence(sql, ", ", "");
		sql += ") ";
		return sql;
	}
	
	private static String addValues(Map<String, String> insertValuesMap,
			String sql) {
		sql	+= " (";
		for(String value : insertValuesMap.values()){
			sql += value+", ";
		}
		sql = replaceLastOccurrence(sql, ", ", "");
		sql +=")";
		return sql;
	}
}
