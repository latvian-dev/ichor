package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.util.Empty;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface CallableTypeAdapter extends Callable, TypeAdapter, InvocationHandler {
	Context getEvalContext();

	Scope getEvalScope();

	@Override
	@SuppressWarnings("unchecked")
	default <T> T adapt(Context cx, Scope scope, Class<T> type) {
		return (T) Proxy.newProxyInstance(cx.getClassLoader() == null ? type.getClassLoader() : cx.getClassLoader(), new Class[]{type}, this);
	}

	@Override
	default Object invoke(Object proxy, Method method, Object[] args) {
		var cx = getEvalContext();
		var scope = getEvalScope();

		if (method.getDeclaringClass() == Object.class) {
			return switch (method.getName()) {
				case "toString" -> toString();
				case "hashCode" -> hashCode();
				case "equals" -> args != null && args.length >= 1 && proxy == args[0];
				default -> null;
			};
		}

		return cx.as(scope, call(cx, scope, args == null ? Empty.OBJECTS : args, false), method.getReturnType());
	}
}
