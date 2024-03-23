package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.util.Empty;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface CallableTypeAdapter extends Callable, TypeAdapter, InvocationHandler {
	Scope getEvalScope();

	@Override
	@SuppressWarnings("unchecked")
	default <T> T adapt(Scope scope, Class<T> type) {
		return (T) Proxy.newProxyInstance(scope.root.context.getClassLoader() == null ? type.getClassLoader() : scope.root.context.getClassLoader(), new Class[]{type}, this);
	}

	@Override
	default Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == Object.class) {
			return switch (method.getName()) {
				case "toString" -> toString();
				case "hashCode" -> hashCode();
				case "equals" -> args != null && args.length >= 1 && proxy == args[0];
				default -> null;
			};
		}

		if (method.isDefault()) {
			return InvocationHandler.invokeDefault(proxy, method, args);
		}

		var scope = getEvalScope();
		return scope.as(call(scope, args == null ? Empty.OBJECTS : args, false), method.getReturnType());
	}
}
