package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class StringJS {
	public static final Prototype PROTOTYPE = PrototypeBuilder.create("String")
			.constructor((scope, args, hasNew) -> args.length == 0 ? "" : scope.getContext().asString(scope, args[0]))
			.asNumber((scope, self) -> scope.toString().isEmpty() ? NumberJS.ZERO : NumberJS.ONE)
			.asBoolean((scope, self) -> !scope.toString().isEmpty())
			.property("length", StringJS::length)
			.function("fromCharCode", StringJS::fromCharCode)
			.function("fromCodePoint", StringJS::fromCodePoint)
			.function("raw", StringJS::raw)
			.function("charAt", StringJS::charAt)
			.function("charCodeAt", StringJS::unimpl)
			.function("indexOf", StringJS::unimpl)
			.function("lastIndexOf", StringJS::unimpl)
			.function("split", StringJS::unimpl)
			.function("substring", StringJS::unimpl)
			.function("toLowerCase", StringJS::unimpl)
			.function("toUpperCase", StringJS::unimpl)
			.function("substr", StringJS::unimpl)
			.function("concat", StringJS::unimpl)
			.function("slice", StringJS::unimpl)
			.function("equalsIgnoreCase", StringJS::unimpl)
			.function("match", StringJS::unimpl)
			.function("search", StringJS::unimpl)
			.function("replace", StringJS::unimpl)
			.function("localeCompare", StringJS::unimpl)
			.function("toLocaleLowerCase", StringJS::unimpl)
			.function("trim", StringJS::trim)
			.function("trimLeft", StringJS::unimpl)
			.function("trimRight", StringJS::unimpl)
			.function("includes", StringJS::unimpl)
			.function("startsWith", StringJS::unimpl)
			.function("endsWith", StringJS::unimpl)
			.function("normalize", StringJS::unimpl)
			.function("repeat", StringJS::unimpl)
			.function("codePointAt", StringJS::unimpl)
			.function("padStart", StringJS::unimpl)
			.function("padEnd", StringJS::unimpl)
			.function("trimStart", StringJS::unimpl)
			.function("trimEnd", StringJS::unimpl);

	private static String s(Object self) {
		return self.toString();
	}

	private static CharSequence cs(Object self) {
		return self instanceof CharSequence c ? c : self.toString();
	}

	private static String unimpl(Scope scope, Object self, Object[] args) {
		throw new ScriptError("This function is not yet implemented!");
	}

	private static String fromCharCode(Scope scope, Object self, Object[] args) {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		char[] chars = new char[n];

		for (int i = 0; i != n; ++i) {
			chars[i] = scope.getContext().asChar(scope, args[i]);
		}

		return new String(chars);
	}

	private static String fromCodePoint(Scope scope, Object self, Object[] args) {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		int[] codePoints = new int[n];

		for (int i = 0; i != n; i++) {
			Object arg = args[i];
			int codePoint = scope.getContext().asInt(scope, arg);

			if (!Character.isValidCodePoint(codePoint)) {
				throw new ScriptError("Invalid code point " + scope.getContext().asString(scope, arg));
			}

			codePoints[i] = codePoint;
		}

		return new String(codePoints, 0, n);
	}

	private static String raw(Scope scope, Object self, Object[] args) {
		return scope.getContext().asString(scope, args[0]);
	}

	private static Object length(Scope scope, Object self) {
		return cs(self).length();
	}

	private static Object charAt(Scope scope, Object self, Object[] objects) {
		return cs(self).charAt(scope.getContext().asInt(scope, objects[0]));
	}

	private static String trim(Scope scope, Object self, Object[] args) {
		return s(self).trim();
	}
}
