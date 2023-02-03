package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;

public class JavaClassPrototype extends Prototype<Class<?>> {
	private static final Functions.Bound<Class<?>> IS_INSTANCE = (cx, scope, cl, args) -> cl.isInstance(args[0]);
	private static final Functions.Bound<Class<?>> IS_ASSIGNABLE_FROM = (cx, scope, cl, args) -> cl.isAssignableFrom(cx.as(scope, args[0], Class.class));

	public JavaClassPrototype(Context cx) {
		super(cx, "JavaClass", Class.class);
	}

	@Override
	protected void initMembers() {
	}

	@Override
	protected void initParents() {
		parents = new Prototype[]{
				context.getClassPrototype(AnnotatedElement.class)
		};
	}

	@Override
	@Nullable
	public Object getLocal(Context cx, Scope scope, Class<?> self, String name) {
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
			case "declaredClasses" -> self.getDeclaredClasses();
			case "declaringClass" -> self.getDeclaringClass();
			case "enclosingClass" -> self.getEnclosingClass();
			case "isInstance" -> IS_INSTANCE.with(self);
			case "isAssignableFrom" -> IS_ASSIGNABLE_FROM.with(self);
			default -> super.getLocal(cx, scope, self, name);
		};
	}

	@Override
	public boolean asString(Context cx, Scope scope, Class<?> self, StringBuilder builder, boolean escape) {
		builder.append(self.getName());
		return true;
	}
}
