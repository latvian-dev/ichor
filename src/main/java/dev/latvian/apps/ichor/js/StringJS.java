package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class StringJS {
	public static final Prototype PROTOTYPE = PrototypeBuilder.create("String")
			.constructor((cx, args, hasNew) -> args.length == 0 ? "" : cx.asString(args[0]))
			.asString((cx, self) -> self.toString())
			.asNumber((cx, self) -> self.toString().isEmpty() ? NumberJS.ZERO : NumberJS.ONE)
			.asBoolean((cx, self) -> !self.toString().isEmpty())
			.property("length", StringJS::length)
			.function("fromCharCode", StringJS::fromCharCode)
			.function("fromCodePoint", StringJS::fromCodePoint)
			.function("raw", StringJS::raw)
			.function("charAt", StringJS::unimpl)
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

	private static String unimpl(Context cx, Object self, Object[] args) {
		throw new ScriptError("This function is not yet implemented!");
	}

	private static String fromCharCode(Context cx, Object self, Object[] args) {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		char[] chars = new char[n];

		for (int i = 0; i != n; ++i) {
			chars[i] = cx.asChar(args[i]);
		}

		return new String(chars);
	}

	private static String fromCodePoint(Context cx, Object self, Object[] args) {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		int[] codePoints = new int[n];

		for (int i = 0; i != n; i++) {
			Object arg = args[i];
			int codePoint = cx.asInt(arg);

			if (!Character.isValidCodePoint(codePoint)) {
				throw new ScriptError("Invalid code point " + cx.asString(arg));
			}

			codePoints[i] = codePoint;
		}

		return new String(codePoints, 0, n);
	}

	private static String raw(Context cx, Object self, Object[] args) {
		return cx.asString(args[0]);
	}

	private static CharSequence asCS(Object value) {
		return value instanceof CharSequence c ? c : value.toString();
	}

	private static int length(Context cx, Object self) {
		return asCS(self).length();
	}

	private static String trim(Context cx, Object self, Object[] args) {
		return self.toString().trim();
	}
}
