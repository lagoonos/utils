package org.lagoonos.utils.sql;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.codehaus.jackson.JsonProcessingException;
import org.junit.Test;

public class JsonToSqlTest {
	private static final String EXPECTED_OUTPUT = 
			"INSERT INTO role (id, role_type) VALUES (1, 'admin');\r\n"
			+"INSERT INTO role (id, role_type) VALUES (2, 'read_only');\r\n"
			+"INSERT INTO user (id, role_id, name) VALUES (1, 1, 'frank');\r\n"
			+"INSERT INTO user (id, role_id, name) VALUES (2, 1, 'mike');\r\n"
			+"INSERT INTO user (id, role_id, name) VALUES (3, 2, 'tom');\r\n";
	
	@Test
	public void getSqlInsertsFromJsonTest() throws JsonProcessingException, IOException{
		File file = new File(JsonToSql.class.getClassLoader().getResource("jsonTest.json").getFile());
		String sqlInserts = JsonToSql.getSqlInsertsFromJson(file);
		System.out.println(sqlInserts);
		Assert.assertEquals(EXPECTED_OUTPUT, sqlInserts);
	}
}
