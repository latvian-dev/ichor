package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class JavaClassJS {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("JavaClass")
			.asString((scope, self) -> c(self).getName())
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

	private static Class<?> c(Object self) {
		return (Class<?>) self;
	}

	private static Object name(Scope scope, Object self) {
		return c(self).getName();
	}

	private static Object superclass(Scope scope, Object self) {
		return c(self).getSuperclass();
	}

	private static Object isInterface(Scope scope, Object self) {
		return c(self).isInterface();
	}

	private static Object isArray(Scope scope, Object self) {
		return c(self).isArray();
	}

	private static Object isEnum(Scope scope, Object self) {
		return c(self).isEnum();
	}

	private static Object isPrimitive(Scope scope, Object self) {
		return c(self).isPrimitive();
	}

	private static Object isAnnotation(Scope scope, Object self) {
		return c(self).isAnnotation();
	}

	private static Object isSynthetic(Scope scope, Object self) {
		return c(self).isSynthetic();
	}

	private static Object getPackage(Scope scope, Object self) {
		return c(self).getPackageName();
	}

	private static Object descriptor(Scope scope, Object self) {
		return c(self).descriptorString();
	}

	private static Object simpleName(Scope scope, Object self) {
		return c(self).getSimpleName();
	}

	private static Object componentType(Scope scope, Object self) {
		return c(self).getComponentType();
	}

	private static Object isInstance(Scope scope, Object self, Object[] args) {
		return c(self).isInstance(args[0]);
	}

	private static Object isAssignableFrom(Scope scope, Object self, Object[] args) {
		return c(self).isAssignableFrom(scope.getContext().as(scope, args[0], Class.class));
	}
}
