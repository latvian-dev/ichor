package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.util.Signature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Remapper {
	default String getFieldName(Context cx, Field field) {
		return field.getName();
	}

	default String getMethodName(Context cx, Method method, Signature signature) {
		return method.getName();
	}
}
