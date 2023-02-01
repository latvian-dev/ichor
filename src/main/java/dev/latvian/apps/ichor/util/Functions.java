package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.WIPFeatureError;

public class Functions {
	public static final Callable WIP = (cx, scope, args, hasNew) -> {
		throw new WIPFeatureError();
	};

	public static Callable ofN(ArgN function) {
		return function;
	}

	public static Callable of1(Arg1 function) {
		return function;
	}

	public static Callable of2(Arg2 function) {
		return function;
	}

	public static Callable of3(Arg3 function) {
		return function;
	}

	@FunctionalInterface
	public interface Bound<T> {
		Object call(Context cx, Scope scope, T self, Object[] args);

		default Callable with(T self) {
			return new BoundCallable<>(self, this);
		}
	}

	public record BoundCallable<T>(T self, Bound<T> function) implements Callable {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return function.call(cx, scope, self, args);
		}
	}

	@FunctionalInterface
	public interface ArgN extends Callable {
		Object call(Context cx, Scope scope, Object[] args);

		@Override
		default Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return call(cx, scope, args);
		}
	}

	@FunctionalInterface
	public interface Arg1 extends Callable {
		Object call(Context cx, Scope scope, Object arg);

		@Override
		default Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			if (args.length < 1) {
				throw new FunctionInstance.ArgumentCountMismatchError(1, args.length);
			}

			return call(cx, scope, args[0]);
		}
	}

	@FunctionalInterface
	public interface Arg2 extends Callable {
		Object call(Context cx, Scope scope, Object arg1, Object arg2);

		@Override
		default Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			if (args.length < 2) {
				throw new FunctionInstance.ArgumentCountMismatchError(2, args.length);
			}

			return call(cx, scope, args[0], args[1]);
		}
	}

	@FunctionalInterface
	public interface Arg3 extends Callable {
		Object call(Context cx, Scope scope, Object arg1, Object arg2, Object arg3);

		@Override
		default Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			if (args.length < 3) {
				throw new FunctionInstance.ArgumentCountMismatchError(3, args.length);
			}

			return call(cx, scope, args[0], args[1], args[2]);
		}
	}
}
