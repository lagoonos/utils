package org.lagoonos.utils.testing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Helper class for unit tests on CDI beans.
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
public class CdiInjector {

	public static void inject(Object bean, Object producer){
		List<Field> injectionPointFields = CdiReader.getInjectionPointFields(bean);
		for(Field ip : injectionPointFields){
			List <Class<? extends Annotation>> qualifiers = CdiReader.getQualifiers(ip);
			List<Field> producerFields = CdiReader.getProducerFields(producer.getClass(), ip.getType(), qualifiers);
			if(producerFields.size() > 0){
				try {
					Object value = producerFields.get(0).get(producer);
					ip.set(bean, value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new InjectionForTestingFailedException(e);
				}
			}
		}
	}
	
}
