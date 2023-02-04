package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.TypeAdapter;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JavaArray extends AbstractList<Object> implements TypeAdapter {
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static List<Object> of(Object array) {
		if (array instanceof List list) {
			return list;
		} else if (array instanceof Object[] arr) {
			return arr.length == 0 ? List.of() : Arrays.asList(arr);
		}

		return new JavaArray(array);
	}

	public final Object array;
	private int size = -1;

	public JavaArray(Object array) {
		this.array = array;
	}

	@Override
	public Object get(int index) {
		return Array.get(array, index);
	}

	@Override
	public Object set(int index, Object element) {
		var old = get(index);
		Array.set(array, index, element);
		return old;
	}

	@Override
	public int size() {
		if (size == -1) {
			size = Array.getLength(array);
		}

		return size;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T adapt(Context cx, Scope scope, Class<T> type) {
		if (type == array.getClass()) {
			return (T) array;
		} else if (type.isArray()) {
			var cType = type.getComponentType();
			var arr = Array.newInstance(cType, size());

			for (int i = 0; i < size(); i++) {
				Array.set(arr, i, cx.as(scope, get(i), cType));
			}

			return (T) arr;
		}

		return null;
	}

	public static Object adaptToArray(Context cx, Scope scope, Iterable<?> itr, Class<?> toType) {
		var cType = toType.getComponentType();

		if (itr instanceof List<?> list) {
			var arr = Array.newInstance(cType, list.size());

			for (int i = 0; i < list.size(); i++) {
				Array.set(arr, i, cx.as(scope, list.get(i), cType));
			}

			return arr;
		} else if (itr instanceof Collection<?> collection) {
			var arr = Array.newInstance(cType, collection.size());
			int index = 0;

			for (var o1 : collection) {
				Array.set(arr, index, cx.as(scope, o1, cType));
				index++;
			}

			return arr;
		} else {
			int size = 0;

			for (var ignore : itr) {
				size++;
			}

			var arr = Array.newInstance(cType, size);
			int index = 0;

			for (var o1 : itr) {
				Array.set(arr, index, cx.as(scope, o1, cType));
				index++;
			}

			return arr;
		}
	}
}
