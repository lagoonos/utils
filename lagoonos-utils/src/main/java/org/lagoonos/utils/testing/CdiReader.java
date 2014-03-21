package org.lagoonos.utils.testing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.lagoonos.utils.ObjectUtils;

/**
 * Helper class for reading CDI beans / Objects with CDI Producer or Injection fields.
 * <br>Can be used to inject Mocks or real objects into an object containing CDI injections.
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
public class CdiReader {

	public static List<Class<? extends Annotation>> getQualifiers(Field field){
		List<Class<? extends Annotation>> qualifiers = new ArrayList<Class<? extends Annotation>>();
		for(Annotation annt : field.getAnnotations()){
			if(annt.annotationType().isAnnotationPresent(Qualifier.class)){
				qualifiers.add(annt.annotationType());
			}
		}
		return qualifiers;
	}
	
	public static List<Field> getInjectionPointFields(Object bean){
		List<Field> fields = ObjectUtils.getAnnotatedFields(bean.getClass(), Inject.class);
		if(bean.getClass().getSuperclass()!=null){
			fields . addAll(ObjectUtils.getAnnotatedFields(bean.getClass().getSuperclass(), Inject.class));
		}
		makeAllAccessible(fields);
		return fields;
	}
	
	public static List<Field> getProducerFields(Class<?> producerContainer, Class<?> fieldType, List<Class<? extends Annotation>> qualifiers){
		List<Field> fields = ObjectUtils.getAnnotatedFields(producerContainer, Produces.class);
		fields = filterType(fields, fieldType);
		makeAllAccessible(fields);
		return fields;
	}
	
	private static List<Field> filterType(List<Field> fields, Class<?> type) {
		
		List<Field> filtered = new ArrayList<Field>();
		for(Field field : fields){
			if(field .getType() == type){
				filtered.add(field);
			}
		}
		return filtered;
	}
	
	private static void makeAllAccessible(List<Field> fields){
		for(Field field : fields){
			field.setAccessible(true);
		}
	}
}
