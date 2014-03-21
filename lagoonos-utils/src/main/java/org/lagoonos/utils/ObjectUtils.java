package org.lagoonos.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.lagoonos.utils.LagoonUtilsException.ExceptionType;

/**
 * 	Utility class with helper methods for java.lang.Object operations and java.lang.reflection enhancement.
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
public class ObjectUtils {
	
	public static boolean contains(Object[] container, Object included) {
		for (Object c : container) {
			if (c.equals(included))
				return true;
		}
		return false;
	}

	public static Class<?>[] objectsToClasses(Object[] objects) {
		Class<?>[] classes = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++) {
			classes[i] = objects[i].getClass();
		}
		return classes;
	}

	public static boolean contains(List<?> container, Object included) {
		for (Object c : container) {
			if (c.equals(included))
				return true;
		}
		return false;
	}

	public static Method getMethodByNameOnly(Class<?> cl, String name) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(name))
				return m;
		}
		return null;
	}

	/**
	 * Determines a getter method within a Class with a specific return type or
	 * a Collection as return type with elements of a specific type:
	 * 
	 * @param getterContainer
	 *            A class containing a getter with the desired return type.
	 * @param type
	 *            The return type the getter method to be looked up provides.
	 * @return A getter Method with the same return type as the input type or A
	 *         getter Method with a Collection instance containing elements of
	 *         the given input type.
	 */
	public static Method getGetterByReturnType(
			Class<? extends Object> getterContainer,
			Class<? extends Object> type) {

		for (Method method : getterContainer.getMethods()) {
			if (method.getName().startsWith("get")) {
				if (method.getReturnType().equals(type)) {
					return method;
				} else if (method.getGenericReturnType() instanceof ParameterizedType
						&& !method.getName().equals("getClass")) {
					ParameterizedType parType = (ParameterizedType) method
							.getGenericReturnType();
					for (Type t : parType.getActualTypeArguments()) {
						Class<?> methodType = (Class<?>) t;
						if (methodType.equals(type)) {
							return method;
						}
					}
				}
			}
		}
		return null;
	}

	public static boolean isNullOrEmptyCollection(Object dependency) {
		if (dependency instanceof Collection) {
			Collection<?> coll = (Collection<?>) dependency;
			if (coll.size() == 0) {
				return true;
			}
		} else if (dependency == null) {
			return true;
		}
		return false;
	}

	public static Class<?> getGenericReturnClass(Method method) {
		Type type = method.getGenericReturnType();
		if (type instanceof ParameterizedType) {
			ParameterizedType parType = (ParameterizedType) method
					.getGenericReturnType();
			if (parType != null) {
				Type[] typeArgs = parType.getActualTypeArguments();
				if (typeArgs.length > 0) {
					return (Class<?>) typeArgs[0];
				}
			}
		}
		return null;
	}

	public static Class<?> getReturnClassGenericSafe(Method method) {
		Class<?> cl = method.getReturnType();
		if (Collection.class.isAssignableFrom(cl)) {
			cl = getGenericReturnClass(method);
		} else {
			cl = method.getReturnType();
		}
		return cl;
	}

	public static Method getGetterForSetter(
			Class<? extends Object> objectClass, String setterName) {
		String getterName = "get" + setterName.substring(3);
		for (Method getter : objectClass.getMethods()) {
			if (getterName.equals(getter.getName())) {
				return getter;
			}
		}
		return null;
	}

	public static Method getSetterForGetter(
			Class<? extends Object> objectClass, String getterName) {
		String setterName = "set";
		if (getterName.startsWith("get")) {
			setterName += StringUtils.toUpperCaseBegin(getterName.substring(3));
		} else if (getterName.startsWith("is")) {
			setterName += StringUtils.toUpperCaseBegin(getterName.substring(2));
		}
		for (Method setter : objectClass.getMethods()) {
			if (setterName.equals(setter.getName())) {
				return setter;
			}
		}
		return null;
	}

	public static List<Method> getSettersByInputType(
			Class<? extends Object> methodContainer,
			Class<? extends Object> type) {
		List<Method> methods = new Vector<Method>();

		for (Method method : methodContainer.getMethods()) {
			for (Class<?> methodType : method.getParameterTypes()) {
				if (methodType.equals(type)) {
					methods.add(method);
				}
			}
		}
		return methods;
	}

	/**
	 * generates a String structure of an Object. Prints primitive datatypes
	 * provided through getter-methods. Recursively prints child objects as
	 * well.
	 * 
	 * @param The
	 *            (root)-Object to be printed to String
	 * @return A String representing the current Object structure with values
	 *         and child objects.
	 */
	public static String toObjectTreeString(Object o) {
		return toObjectTreeString(o, "Database", null, 0);
	}

	
	public static String toObjectGettersString(Object o){
		String out = "";
		for(Method getter:getGetters(o.getClass())){
			String valueName = getter.getName().substring(3);
			Object value;
			value = invokeMethodExceptionSafe(getter, o);
			out += valueName+":"+value.toString();
		}
		return out;
	}
	
	private static String toObjectTreeString(Object o, String fieldName,
			List<Class<?>> ignoreParents, int deep) {
		String s = "";
		if (o instanceof Collection) {
			Collection<?> c = (Collection<?>) o;
			for (Object collectionObj : c) {
				ignoreParents.add(o.getClass());
				s += toObjectTreeString(collectionObj, fieldName,
						ignoreParents, deep);
			}
		} else {

			if (ignoreParents == null) {
				ignoreParents = new Vector<Class<?>>();
				ignoreParents.add(o.getClass());
			}

			String inline = "";
			for (int i = 0; i < deep; i++) {
				inline += "     ";
			}

			s += "\r\n" + inline + "|";
			s += "\r\n" + inline + "|---" + fieldName + "("
					+ o.getClass().getSimpleName() + ")";

			List<Method> complexChildMethods = new Vector<Method>();
			for (Method m : o.getClass().getMethods()) {

				if (m.getName().startsWith("get")) {
					if (m.getReturnType().isPrimitive()
							|| m.getReturnType().equals(String.class)
							|| m.getReturnType().equals(Boolean.class)
							|| m.getReturnType().equals(Integer.class)
							|| m.getReturnType().equals(Date.class)
							|| m.getReturnType().equals(Double.class)
							|| m.getReturnType().equals(BigInteger.class)) {
						Object returnVal = invokeMethodExceptionSafe(m, o);
						s += " - " + m.getName().substring(3) + "["
									+ returnVal + "]";
					} else if (!m.getName().equals("getClass")
							&& !listContainsEqual(ignoreParents,
									m.getReturnType())
							&& !listContainsEqual(ignoreParents, m
									.getGenericReturnType().getClass())) {
						complexChildMethods.add(m);
					}
				}
			}

			for (Method m : complexChildMethods) {
				Object child = invokeMethodExceptionSafe(m, o);
				if (child != null) {
					ignoreParents.add(o.getClass());
					String name = m.getName().substring(3);
					s += toObjectTreeString(child, name, ignoreParents,
							deep + 1);
				}
			}
		}
		return s;
	}

	public static boolean listContainsEqual(List<?> containerList,
			Object findInList) {
		for (Object o : containerList) {
			if (o.equals(findInList))
				return true;
		}
		return false;
	}

	public static List<?> getListForSet(Set<?> set) {
		if (set == null)
			return null;
		List<Object> list = new Vector<Object>();
		for (Object o : set) {
			list.add(o);
		}
		return list;
	}

	public static Object cloneEntity(Object source) {
		return cloneEntityWithDeepness(source, -1, 0);
	}

	public static Object cloneEntityWithDeepness(Object source, int deep,
			int level) {

		Class<?> clazz = source.getClass();

		if (isSimpleType(clazz)) {
			return source;
		}

		if (deep == -1 || level < deep) {
			if (source instanceof Collection) {
				Collection<Object> coll = (Collection<Object>) source;
				Collection<Object> cloneColl = new Vector<Object>();
				for (Object o : coll) {
					cloneColl.add(cloneEntityWithDeepness(o, deep, level + 1));
				}
				return cloneColl;
			}

			Object clone = instanciateClassExceptionSafe(clazz);
			for (Method method : clazz.getMethods()) {
				if (method.getName().startsWith("get")) {
					Method setter = getSetterForGetter(source.getClass(),
							method.getName());
					if (setter != null) {
						invokeMethodExceptionSafe(setter, source);
					}
				}
			}
			return clone;
		}
		return null;
	}

	public static boolean isSimpleType(Class<?> type) {
		if (type.isPrimitive() || type.equals(String.class)
				|| type.equals(Boolean.class) || type.equals(Integer.class)
				|| type.equals(Long.class) || type.equals(Date.class)
				|| type.equals(Double.class)) {
			return true;
		}
		return false;
	}

	public static List<Method> getAnnotatedGetters(Class<?> entity,
			Class<? extends Annotation> annotationClass) {
		List<Class<? extends Annotation>> annotationList = new Vector<Class<? extends Annotation>>();
		annotationList.add(annotationClass);
		return getAnnotatedGetters(entity, annotationList);
	}

	public static List<Method> getAnnotatedGetters(Class<?> entity,
			List<Class<? extends Annotation>> annotationClasses) {
		List<Method> getters = new Vector<Method>();
		for (Method method : entity.getMethods()) {
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (method.getName().startsWith("get")
						&& method.isAnnotationPresent(annotationClass)) {
					getters.add(method);
				}
			}
		}
		return getters;
	}

	public static List<Method> getAnnotatedFieldGetters(Class<?> entity,
			Class<? extends Annotation> annotationClass) {
		List<Class<? extends Annotation>> annotationList = new Vector<Class<? extends Annotation>>();
		annotationList.add(annotationClass);
		return getAnnotatedFieldGetters(entity, annotationList);
	}
	
	public static List<Field> getAnnotatedGettersFields(Class<?> entity,
			Class<? extends Annotation> annotationClass){
		List<Class<? extends Annotation>> annotationList = new Vector<Class<? extends Annotation>>();
		annotationList.add(annotationClass);
		return getAnnotatedGettersFields(entity, annotationList);
	}
	
	public static List<Field> getAnnotatedGettersFields(Class<?> entity,
			List<Class<? extends Annotation>> annotationClasses) {
		List<Method> getters = getAnnotatedGetters(entity, annotationClasses);
		List<Field> fields = new ArrayList<Field>();
		for(Method getter:getters){
			fields.add(getFieldForMethod(entity, getter));
		}
		return fields;
	}

	public static List<Method> getAnnotatedFieldGetters(Class<?> entity,
			List<Class<? extends Annotation>> annotationClasses) {
		List<Method> getters = new Vector<Method>();
		List<Field> fields = getAnnotatedFields(entity, annotationClasses);
		for (Field field : fields) {
			getters.add(getGetterForField(field, entity));
		}
		return getters;
	}
	
	public static List<Method> getAnnotatedMethods(Class<?> entity,
			Class<? extends Annotation> annotationClass) {

		List<Method> methods = new ArrayList<Method>();
		for(Method method : entity.getDeclaredMethods()){
			if(method.isAnnotationPresent(annotationClass)){
				methods.add(method);
			}
		}
		return methods;
	}

	public static Method getGetterForField(Field field, Class<?> entity) {
		for (Method method : entity.getMethods()) {
			String getterName = "get"
					+ StringUtils.toUpperCaseBegin(field.getName());
			if (method.getName().equals(getterName)) {
				return method;
			}
		}
		return null;
	}

	public static List<Field> getAnnotatedFields(Class<?> entityClass,
			Class<? extends Annotation> annotationClass) {
		List<Class<? extends Annotation>> annotationList = new Vector<Class<? extends Annotation>>();
		annotationList.add(annotationClass);
		return getAnnotatedFields(entityClass, annotationList);
	}

	public static List<Field> getAnnotatedFields(Class<?> entityClass,
			List<Class<? extends Annotation>> annotationClasses) {
		List<Field> fields = new Vector<Field>();
		for (Field field : entityClass.getDeclaredFields()) {
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (field.isAnnotationPresent(annotationClass)) {
					fields.add(field);
				}
			}
		}
		return fields;
	}

	public static List<Method> getGetters(Class<?> container) {
		List<Method> getter = getPrefixedMethods(container, "get");
		getter.addAll(getIssers(container));
		return getter;
	}

	public static List<Method> getSetters(Class<?> container) {
		return getPrefixedMethods(container, "set");
	}

	private static List<Method> getIssers(Class<?> container) {
		return getPrefixedMethods(container, "is");
	}

	private static List<Method> getPrefixedMethods(Class<?> container,
			String prefix) {
		List<Method> methods = new ArrayList<Method>();
		for (Method m : container.getMethods()) {
			if (m.getName().startsWith(prefix)) {
				methods.add(m);
			}
		}
		return methods;
	}

	public static Field getFieldForMethod(Class<?> container,
			Method getterOrSetter) {
		String fieldName = StringUtils.toLowerCaseBegin(getterOrSetter
				.getName().substring(3));
		for (Field field : container.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		return null;
	}

	public static Method getGetterForSetter(Class<?> container, Method setter) {
		String getterName = "get"
				+ StringUtils.toUpperCaseBegin(setter.getName().substring(3));
		for (Method getter : getGetters(container)) {
			if (getterName.equals(getter.getName())) {
				return getter;
			}
		}
		return null;
	}

	public static Method getSetterForGetter(Class<?> container, Method getter) {
		return getSetterForGetter(container, getter.getName());
	}

	public static void addChildToParent(Object parent, Object child) {
		
		Method getter = getGetterByReturnType(parent.getClass(),
				child.getClass());
		if (Collection.class.isAssignableFrom(getter.getReturnType())) {
			@SuppressWarnings("unchecked")
			Collection<Object> children = (Collection<Object>) invokeMethodExceptionSafe(getter, parent);
			children.add(child);
		} else {
			Method setter = getSetterForGetter(parent.getClass(), getter);
			invokeMethodExceptionSafe(setter, parent, child);
		}
	}

	public static void removeChildFromParent(Object parent, Object child){
		
		Method getter = getGetterByReturnType(parent.getClass(),child.getClass());
		Object getterReturnTypeInstance = instanciateClassExceptionSafe(getter.getReturnType());
		if (getterReturnTypeInstance instanceof Collection<?>) {
			@SuppressWarnings("unchecked")
			Collection<Object> children = (Collection<Object>) invokeMethodExceptionSafe(getter, parent);
			children.remove(child);
		} else {
			Method setter = getSetterForGetter(parent.getClass(), getter);
			invokeMethodExceptionSafe(setter, parent, new Object[0]);
		}
	}

	public static List<Field> getFieldsByType(Class<?> container,
			Class<?> fieldType) {
		List<Field> out = new Vector<Field>();
		for (Field field : container.getDeclaredFields()) {
			if (field.getType() == fieldType) {
				out.add(field);
			}
		}
		return out;
	}

	public static String removePackageDeclaration(String absoluteClassName) {
		String name = absoluteClassName;
		if (name.contains(".")) {
			name = name.substring(name.lastIndexOf(".") + 1);
		}
		return name;
	}

	/**
	 * Merges Objects in the order they are provided in the list Parameter. The
	 * first being overridden by the second etc. The merge Objects must have a
	 * parameterless constructor.
	 * 
	 * @param objects
	 * @return
	 */
	public static Object mergeObjects(List<?> objects) {

		Class<?> clazz = objects.get(0).getClass();
		Object merged = instanciateClassExceptionSafe(clazz);
		List<Method> getters = getGetters(clazz);

		for (Object o : objects) {
			for (Method getter : getters) {
				Object returnVal;
				returnVal = invokeMethodExceptionSafe(getter, o);
				if (returnVal != null
						|| (returnVal instanceof Collection<?> && ((Collection<?>) returnVal)
								.isEmpty())) {
					Method setter = getSetterForGetter(clazz, getter.getName());
					if (returnVal instanceof Collection<?>) {
						Collection<?> returnCollection = ((Collection<?>) invokeMethodExceptionSafe(getter, merged, new Object[0]));
						returnCollection.clear();
						returnCollection.addAll((Collection) returnVal);
					} else if (setter != null) {
						invokeMethodExceptionSafe(setter, merged, returnVal);
					}
				}
			}
		}
		return merged;
	}

	private static Object instanciateClassExceptionSafe(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new LagoonUtilsException(e, ExceptionType.CLASS_NEW_INSTANCE,clazz.getName());
		}
	}

	private static Object invokeMethodExceptionSafe( Method method, Object invokeOnObject, Object ...parameters) {
		try {
			return method.invoke(invokeOnObject, parameters);
		} catch (IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException e) {
			throw new LagoonUtilsException(e, ExceptionType.INVOKE_METHOD, invokeOnObject.getClass().getName(), method.getName());
		}
	}

	public static void printSimpleStructure(Object object) {
		for (Method m : getGetters(object.getClass())) {
			if (!m.getName().equals("getClass")) {
				System.out.print(m.getName() + ": ");
				if (m.getParameterTypes().length == 0) {
					Object ret = invokeMethodExceptionSafe(m, object);
					if (ret == null) {
						System.out.println("null");
					} else {
						System.out.println("" + ret);
					}
				}
			}
		}
	}

	public static void printStructure(Object object) {
		System.out.println("Printing object structure:");
		printStructure(object, new ArrayList<String>());
	}

	private static void printStructure(Object object, List<String> lookedUp) {
		System.out.println(" - " + object.getClass().getName());
		String key = createKey(object);
		System.out.print("(looking up: " + key + " in "
				+ getListContent(lookedUp));

		if (!contains(lookedUp, key)) {
			System.out.println(")");
			System.out.println("Object [" + key + ":");
			lookedUp.add(key);
			for (Method m : getGetters(object.getClass())) {
				if (!m.getName().equals("getClass")) {
					System.out.print(m.getName() + ": ");
					if (m.getParameterTypes().length == 0) {
						Object ret = invokeMethodExceptionSafe(m, object);
						if (ret == null) {
							System.out.println("null");
						} else if (isSimpleType(ret.getClass())
								|| ret instanceof BigInteger) {
							System.out.println("" + ret);
						} else if (ret instanceof Collection) {
							for (Object o : (Collection) ret) {
								printStructure(o, lookedUp);
							}
						} else if (ret instanceof Set) {
							for (Object o : (Set) ret) {
								printStructure(o, lookedUp);
							}
						} else if (ret instanceof List) {
							for (Object o : (List) ret) {
								printStructure(o, lookedUp);
							}
						} else if (ret instanceof Iterable) {
							for (Object o : (Iterable) ret) {
								printStructure(o, lookedUp);
							}
						} else {
							printStructure(ret, lookedUp);
							lookedUp.add(createKey(ret));
						}
					}
				}
			}
			System.out.println("]");
		} else {
			System.out.println(" ... in list/ already looked up)");
		}
	}

	private static String getListContent(List<?> list) {
		String out = "[";
		for (Object o : list) {
			out += o.toString();
			out += ",";
		}
		if (out.length() > 1) {
			out = out.substring(0, out.length() - 1);
		}
		out += "]";
		return out;
	}

	private static String createKey(Object object) {
		return object.getClass().getName() + ":" + object.hashCode();
	}

	public static Class<?> getGenericReturnClassFromField(Field declaredField) {
		Class<?> genericClass;
		Type type = declaredField.getGenericType();
		if (type instanceof ParameterizedType) {
			genericClass = (Class<?>) ((ParameterizedType) type)
					.getActualTypeArguments()[0];
		} else {
			genericClass = (Class<?>) type;
		}
		return genericClass;
	}

	public static boolean isAnyAnnotationPresent(Method method,
			Class<? extends Annotation>[] annotationClasses) {
		for (Class<? extends Annotation> annt : annotationClasses) {
			if (method.isAnnotationPresent(annt)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAnyAnnotationPresent(Field field,
			Class<? extends Annotation>[] annotationClasses) {
		for (Class<? extends Annotation> annt : annotationClasses) {
			if (field.isAnnotationPresent(annt)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether obj is null to prevent a NullPointerException. If it's not
	 * null it returns the String provided by obj.toString(). If obj is null it
	 * returns null.
	 * 
	 * @param obj
	 * @return
	 */
	public static String getNullSafeToString(Object obj) {
		if (obj != null) {
			return obj.toString();
		}
		return "";
	}
	
	public static Object getAnnotationPropertyDefault(
			Class<? extends java.lang.annotation.Annotation> compiledAnnotation, String propertyName) {
			try {
				java.lang.reflect.Method propertyMethod = compiledAnnotation.getMethod(propertyName);
				return propertyMethod.getDefaultValue();
			} catch (NoSuchMethodException | SecurityException e) {/*expected*/}
		return null;
	}

	public static boolean areAllAnnotationsPresent(Field field,
			List<Class<? extends Annotation>> annotations) {
		for(Class<? extends Annotation> annotation : annotations){
			if(!field.isAnnotationPresent(annotation)){
				return false;
			}
		}
		return true;
	}
	
	public static boolean areAllAnnotationsPresent(Method method,
			List<Class<? extends Annotation>> annotations) {
		for(Class<? extends Annotation> annotation : annotations){
			if(!method.isAnnotationPresent(annotation)){
				return false;
			}
		}
		return true;
	}
}
