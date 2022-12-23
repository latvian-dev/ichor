package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class JavaClassPrototype {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("JavaClass")
			.asString((scope, self, builder) -> builder.append(c(self).getName()))
			.property("name", JavaClassPrototype::name)
			.constant("class", Class.class)
			.property("superclass", JavaClassPrototype::superclass)
			.property("interface", JavaClassPrototype::isInterface)
			.property("array", JavaClassPrototype::isArray)
			.property("enum", JavaClassPrototype::isEnum)
			.property("primitive", JavaClassPrototype::isPrimitive)
			.property("annotation", JavaClassPrototype::isAnnotation)
			.property("synthetic", JavaClassPrototype::isSynthetic)
			.property("package", JavaClassPrototype::getPackage)
			.property("descriptor", JavaClassPrototype::descriptor)
			.property("simpleName", JavaClassPrototype::simpleName)
			.property("componentType", JavaClassPrototype::componentType)
			.function("isInstance", JavaClassPrototype::isInstance)
			.function("isAssignableFrom", JavaClassPrototype::isAssignableFrom);

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
