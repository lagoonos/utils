package org.lagoonos.utils.testing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;

import org.lagoonos.utils.ObjectUtils;

/**
 * Helper class with helpful methods simulating ejb container functionality when running unit tests.
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
public class CdiTestHelper {

	/**
	 * Invokes methods in an ejb bean that are annotated with a @PostConstruct annotation
	 * @param bean The object where @PostConstruct annotations are to be detected and invoked.
	 */
	public static void runPostConstructMethods(Object bean){
		List<Method> methods = ObjectUtils.getAnnotatedMethods(bean.getClass(), PostConstruct.class);
		if(bean.getClass().getSuperclass()!=null){
			methods.addAll(ObjectUtils.getAnnotatedMethods(bean.getClass().getSuperclass(), PostConstruct.class));
		}
		for(Method method : methods){
			try {
				method.setAccessible(true);
				method.invoke(bean, new Object[0]);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new InitializingForTestFailedException(e);
			}
		}
	}
	
}
