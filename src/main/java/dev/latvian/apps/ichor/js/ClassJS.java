package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeWrappedObject;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

public record ClassJS(Class<?> self) implements PrototypeWrappedObject {
	private static final Functions.Bound<Class<?>> IS_INSTANCE = (cx, scope, cl, args) -> cl.isInstance(args[0]);
	private static final Functions.Bound<Class<?>> IS_ASSIGNABLE_FROM = (cx, scope, cl, args) -> cl.isAssignableFrom(cx.as(scope, args[0], Class.class));
	private static final Functions.Bound<Class<?>> GET_ANNOTATION = (cx, scope, cl, args) -> cl.getAnnotation(cx.as(scope, args[0], Class.class));
	private static final Functions.Bound<Class<?>> GET_DECLARED_ANNOTATION = (cx, scope, cl, args) -> cl.getDeclaredAnnotation(cx.as(scope, args[0], Class.class));
	private static final Functions.Bound<Class<?>> GET_ANNOTATIONS_BY_TYPE = (cx, scope, cl, args) -> cl.getAnnotationsByType(cx.as(scope, args[0], Class.class));
	private static final Functions.Bound<Class<?>> GET_DECLARED_ANNOTATIONS_BY_TYPE = (cx, scope, cl, args) -> cl.getDeclaredAnnotationsByType(cx.as(scope, args[0], Class.class));

	public static Prototype createDefaultPrototype() {
		return new Prototype("JavaClass") {
			@Override
			@Nullable
			public Object get(Context cx, Scope scope, String name) {
				return name.equals("class") ? Class.class : super.get(cx, scope, name);
			}
		};
	}

	@Override
	public Object unwrap() {
		return self;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return ((ContextJS) cx).classPrototype;
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
			case "interfaces" -> self.getInterfaces();
			case "modifiers" -> self.getModifiers();
			case "annotations" -> self.getAnnotations();
			case "declaredAnnotations" -> self.getDeclaredAnnotations();
			case "declaredClasses" -> self.getDeclaredClasses();
			case "declaringClass" -> self.getDeclaringClass();
			case "enclosingClass" -> self.getEnclosingClass();
			case "isInstance" -> IS_INSTANCE.with(self);
			case "isAssignableFrom" -> IS_ASSIGNABLE_FROM.with(self);
			case "getAnnotation" -> GET_ANNOTATION.with(self);
			case "getDeclaredAnnotation" -> GET_DECLARED_ANNOTATION.with(self);
			case "getAnnotationsByType" -> GET_ANNOTATIONS_BY_TYPE.with(self);
			case "getDeclaredAnnotationsByType" -> GET_DECLARED_ANNOTATIONS_BY_TYPE.with(self);
			case "class" -> Class.class;
			default -> PrototypeWrappedObject.super.get(cx, scope, name);
		};
	}
}
