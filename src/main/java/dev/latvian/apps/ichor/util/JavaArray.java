package dev.latvian.apps.ichor.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class JavaArray extends AbstractList<Object> {
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
}
