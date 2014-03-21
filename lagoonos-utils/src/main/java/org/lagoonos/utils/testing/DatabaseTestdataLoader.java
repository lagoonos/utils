package org.lagoonos.utils.testing;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.lagoonos.utils.sql.JsonToSql;

/**
 * Helper class for loading testdata provided in a resource file into a (testing) database.
 * Usually needed for setting up the right unit test preconditions.
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
public class DatabaseTestdataLoader {

	public static void loadTestData(EntityManager entityManager, String jsonDataResourceName){
		String sql;
		try {
			sql = JsonToSql.getSqlInsertsFromJsonResource(jsonDataResourceName);
		} catch (IOException e) {
			throw new InjectionForTestingFailedException(e);
		}
		entityManager.getTransaction().begin();
		Query q = entityManager.createNativeQuery(sql);
		q.executeUpdate();
		entityManager.getTransaction().commit();
	}
	
}
