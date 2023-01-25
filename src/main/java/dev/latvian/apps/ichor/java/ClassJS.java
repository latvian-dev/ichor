package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.util.SimpleFunction;
import org.jetbrains.annotations.Nullable;

public class ClassJS implements WrappedObject {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("JavaClass")
			.constant("class", Class.class)
			.function("isInstance", ClassJS::isInstance)
			.function("isAssignableFrom", ClassJS::isAssignableFrom);

	private static Class<?> c(Object self) {
		return (Class<?>) self;
	}

	private static Object isInstance(Context cx, Scope scope, Object self, Object[] args) {
		return c(self).isInstance(args[0]);
	}

	private static Object isAssignableFrom(Context cx, Scope scope, Object self, Object[] args) {
		return c(self).isAssignableFrom(cx.as(scope, args[0], Class.class));
	}

	private static final SimpleFunction.Callback<Class<?>> IS_INSTANCE = (cx, scope, cl, args) -> cl.isInstance(args[0]);
	private static final SimpleFunction.Callback<Class<?>> IS_ASSIGNABLE_FROM = (cx, scope, cl, args) -> cl.isAssignableFrom(cx.as(scope, args[0], Class.class));

	public final Class<?> self;

	public ClassJS(Class<?> self) {
		this.self = self;
	}

	@Override
	public Object unwrap() {
		return self;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return PROTOTYPE;
	}

	@Override
	public String toString() {
		return "[JavaClass " + self.getName() + "]";
	}

	@Override
	public void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		builder.append(self.getName());
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		return switch (name) {
			case "name" -> self.getName();
			case "superclass" -> self.getSuperclass();
			case "interface" -> self.isInterface();
			case "array" -> self.isArray();
			case "enum" -> self.isEnum();
			case "primitive" -> self.isPrimitive();
			case "annotation" -> self.isAnnotation();
			case "synthetic" -> self.isSynthetic();
			case "package" -> self.getPackageName();
			case "descriptor" -> self.descriptorString();
			case "simpleName" -> self.getSimpleName();
			case "componentType" -> self.getComponentType();
			case "canonicalName" -> self.getCanonicalName();
			case "typeName" -> self.getTypeName();
			case "isInstance" -> SimpleFunction.of(self, IS_INSTANCE);
			case "isAssignableFrom" -> SimpleFunction.of(self, IS_ASSIGNABLE_FROM);
			default -> Special.NOT_FOUND;
		};
	}
}
