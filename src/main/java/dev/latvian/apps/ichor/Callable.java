package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.util.Empty;

@FunctionalInterface
public interface Callable {
	Object call(Context cx, Scope scope, Object[] args, boolean hasNew);

	default Object[] evalArgs(Context cx, Scope scope, Object[] arguments) {
		if (arguments.length == 0) {
			return Empty.OBJECTS;
		}

		var args = arguments;

		for (int i = 0; i < args.length; i++) {
			var a = cx.eval(scope, args[i]);

			if (a != args[i]) {
				if (args == arguments) {
					args = arguments.clone();
				}

				args[i] = a;
			}
		}

		return args;
	}
}