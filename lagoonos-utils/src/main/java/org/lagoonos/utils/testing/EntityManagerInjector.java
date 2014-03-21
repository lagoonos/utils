package org.lagoonos.utils.testing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

/**
 * Helper class for injecting <code>javax.persistence.EntityManager</code> instances into a cdi bean.
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
public class EntityManagerInjector {

	public static List<EntityManager> injectEntityManagers(Object bean, Class<?> producerContainer){
		List<Field> emFields = getEntityManagerFields(bean);
		
		List<EntityManager> entityManagers = new ArrayList<EntityManager>();
		for(Field field : emFields){
			List<Class<? extends Annotation>> qualifiers = CdiReader.getQualifiers(field);
			String unitName = getEntityManagerPersistenceUnitName(producerContainer, qualifiers);
			EntityManager entityManager = createEntityManager(unitName);
			entityManagers.add(entityManager);
			try {
				field.set(bean, entityManager);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InjectionForTestingFailedException("Could not inject EntityManager with unit name "+unitName+" into bean "+bean, e);
			}
		}
		return entityManagers;
	}

	private static EntityManager createEntityManager(String unitName) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(unitName);
		return emf.createEntityManager();
	}

	private static String getEntityManagerPersistenceUnitName(
			Class<?> producerContainer, List<Class<? extends Annotation>> qualifiers) {
		List<Field> producerFields = CdiReader.getProducerFields(producerContainer, EntityManager.class, qualifiers);
		List<Field> entityManagerProducers = filterEntityManagerFields(producerFields);
		PersistenceContext persistenceContext = entityManagerProducers.get(0).getAnnotation(PersistenceContext.class);
		return persistenceContext.unitName();
	}

	private static List<Field> getEntityManagerFields(Object bean) {
		List<Field> injectFields = CdiReader.getInjectionPointFields(bean);
		return filterEntityManagerFields(injectFields);
	}

	private static List<Field> filterEntityManagerFields(List<Field> injectFields) {
		List<Field> filtered = new ArrayList<Field>();
		for(Field field : injectFields){
			if(field.getType().equals(EntityManager.class)){
				field .setAccessible(true);
				filtered.add(field);
			}
		}
		return filtered;
	}
	
}
