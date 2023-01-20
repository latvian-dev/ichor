package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.error.WIPFeatureError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class StringJS {
	private static String s(Object self) {
		return self.toString();
	}

	public static final Prototype PROTOTYPE = new PrototypeBuilder("String") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? "" : cx.asString(scope, args[0], false);
		}

		@Override
		public void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape) {
			if (escape) {
				AstStringBuilder.wrapString(self, builder);
			} else {
				builder.append(s(self));
			}
		}

		@Override
		public Number asNumber(Context cx, Scope scope, Object self) {
			try {
				return Double.parseDouble(s(self));
			} catch (NumberFormatException ex) {
				return Double.NaN;
			}
		}

		@Override
		public boolean asBoolean(Context cx, Scope scope, Object self) {
			return !s(self).isEmpty();
		}
	}
			.staticFunction("fromCharCode", StringJS::fromCharCode)
			.staticFunction("fromCodePoint", StringJS::fromCodePoint)
			.staticFunction("raw", StringJS::raw)
			.property("length", StringJS::length)
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

	public static class InvalidCodePointError extends ScriptError {
		public final String codePoint;

		public InvalidCodePointError(String codePoint) {
			super("Invalid code point " + codePoint);
			this.codePoint = codePoint;
		}
	}

	private static CharSequence cs(Object self) {
		return self instanceof CharSequence c ? c : self.toString();
	}

	private static String unimpl(Context cx, Scope scope, Object self, Object[] args) {
		throw new WIPFeatureError();
	}

	private static String fromCharCode(Context cx, Scope scope, Object[] args) {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		var chars = new char[n];

		for (int i = 0; i != n; ++i) {
			chars[i] = cx.asChar(scope, args[i]);
		}

		return new String(chars);
	}

	private static String fromCodePoint(Context cx, Scope scope, Object[] args) {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		var codePoints = new int[n];

		for (int i = 0; i != n; i++) {
			int codePoint = cx.asInt(scope, args[i]);

			if (!Character.isValidCodePoint(codePoint)) {
				throw new InvalidCodePointError(cx.asString(scope, args[i], true));
			}

			codePoints[i] = codePoint;
		}

		return new String(codePoints, 0, n);
	}

	private static String raw(Context cx, Scope scope, Object[] args) {
		return cx.asString(scope, args[0], false);
	}

	private static Object length(Context cx, Scope scope, Object self) {
		return cs(self).length();
	}

	private static Object charAt(Context cx, Scope scope, Object self, Object[] args) {
		return cs(self).charAt(cx.asInt(scope, args[0]));
	}

	private static String trim(Context cx, Scope scope, Object self, Object[] args) {
		return s(self).trim();
	}
}
