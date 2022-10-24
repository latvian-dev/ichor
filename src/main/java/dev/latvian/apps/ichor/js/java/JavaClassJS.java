package dev.latvian.apps.ichor.js.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class JavaClassJS {
	public static final Prototype PROTOTYPE = PrototypeBuilder.create("JavaClass")
			.asString((cx, self) -> ((Class<?>) self).getName())
			.property("name", JavaClassJS::name)
			.property("superclass", JavaClassJS::superclass)
			.property("interface", JavaClassJS::isInterface)
			.property("array", JavaClassJS::isArray)
			.property("enum", JavaClassJS::isEnum)
			.property("primitive", JavaClassJS::isPrimitive)
			.property("annotation", JavaClassJS::isAnnotation)
			.property("synthetic", JavaClassJS::isSynthetic)
			.property("package", JavaClassJS::getPackage)
			.property("descriptor", JavaClassJS::descriptor)
			.property("simpleName", JavaClassJS::simpleName)
			.property("componentType", JavaClassJS::componentType)
			.function("isInstance", JavaClassJS::isInstance)
			.function("isAssignableFrom", JavaClassJS::isAssignableFrom);

	private static Object name(Context cx, Object self) {
		return ((Class<?>) self).getName();
	}

	private static Object superclass(Context cx, Object self) {
		return ((Class<?>) self).getSuperclass();
	}

	private static Object isInterface(Context cx, Object self) {
		return ((Class<?>) self).isInterface();
	}

	private static Object isArray(Context cx, Object self) {
		return ((Class<?>) self).isArray();
	}

	private static Object isEnum(Context cx, Object self) {
		return ((Class<?>) self).isEnum();
	}

	private static Object isPrimitive(Context cx, Object self) {
		return ((Class<?>) self).isPrimitive();
	}

	private static Object isAnnotation(Context cx, Object self) {
		return ((Class<?>) self).isAnnotation();
	}

	private static Object isSynthetic(Context cx, Object self) {
		return ((Class<?>) self).isSynthetic();
	}

	private static Object getPackage(Context cx, Object self) {
		return ((Class<?>) self).getPackageName();
	}

	private static Object descriptor(Context cx, Object self) {
		return ((Class<?>) self).descriptorString();
	}

	private static Object simpleName(Context cx, Object self) {
		return ((Class<?>) self).getSimpleName();
	}

	private static Object componentType(Context cx, Object self) {
		return ((Class<?>) self).getComponentType();
	}

	private static Object isInstance(Context cx, Object self, Object[] args) {
		return ((Class<?>) self).isInstance(args[0]);
	}

	private static Object isAssignableFrom(Context cx, Object self, Object[] args) {
		return ((Class<?>) self).isAssignableFrom(cx.as(args[0], Class.class));
	}
}
