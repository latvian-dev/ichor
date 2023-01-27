package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.util.Functions;
import dev.latvian.apps.ichor.util.NativeArrayList;
import org.jetbrains.annotations.Nullable;

public class StringJS implements WrappedObject {
	private static String s(Object self) {
		return self.toString();
	}

	public static class InvalidCodePointError extends ScriptError {
		public final String codePoint;

		public InvalidCodePointError(String codePoint) {
			super("Invalid code point " + codePoint);
			this.codePoint = codePoint;
		}
	}

	private static final Callable RAW = Functions.ofN((cx, scope, args) -> {
		var sb = new StringBuilder();

		for (var o : NativeArrayList.of(args[0])) {
			sb.append(cx.asString(scope, o, false));
		}

		return sb.toString();
	});

	private static final Callable FROM_CHAR_CODE = Functions.ofN((cx, scope, args) -> {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		var chars = new char[n];

		for (int i = 0; i != n; ++i) {
			chars[i] = cx.asChar(scope, args[i]);
		}

		return new String(chars);
	});

	private static final Callable FROM_CODE_POINT = Functions.ofN((cx, scope, args) -> {
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
	});

	private static final Functions.Bound<String> CHAR_AT = (cx, scope, str, args) -> str.charAt(cx.asInt(scope, args[0]));
	private static final Functions.Bound<String> TRIM = (cx, scope, str, args) -> str.trim();

	public static final Prototype PROTOTYPE = new PrototypeBuilder("String") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? "" : cx.asString(scope, args[0], false);
		}

		@Override
		@Nullable
		public Object get(Context cx, Scope scope, String name) {
			return switch (name) {
				case "raw" -> RAW;
				case "fromCharCode" -> FROM_CHAR_CODE;
				case "fromCodePoint" -> FROM_CODE_POINT;
				default -> super.get(cx, scope, name);
			};
		}
	};

	public final String self;

	public StringJS(String self) {
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
	@Nullable
	@SuppressWarnings("DuplicateBranchesInSwitch")
	public Object get(Context cx, Scope scope, String name) {
		return switch (name) {
			case "length" -> self.length();
			case "charAt" -> Functions.bound(self, CHAR_AT);
			case "charCodeAt" -> Functions.WIP;
			case "indexOf" -> Functions.WIP;
			case "lastIndexOf" -> Functions.WIP;
			case "split" -> Functions.WIP;
			case "substring" -> Functions.WIP;
			case "toLowerCase" -> Functions.WIP;
			case "toUpperCase" -> Functions.WIP;
			case "substr" -> Functions.WIP;
			case "concat" -> Functions.WIP;
			case "slice" -> Functions.WIP;
			case "equalsIgnoreCase" -> Functions.WIP;
			case "match" -> Functions.WIP;
			case "search" -> Functions.WIP;
			case "replace" -> Functions.WIP;
			case "localeCompare" -> Functions.WIP;
			case "toLocaleLowerCase" -> Functions.WIP;
			case "trim" -> Functions.bound(self, TRIM);
			case "trimLeft" -> Functions.WIP;
			case "trimRight" -> Functions.WIP;
			case "includes" -> Functions.WIP;
			case "startsWith" -> Functions.WIP;
			case "endsWith" -> Functions.WIP;
			case "normalize" -> Functions.WIP;
			case "repeat" -> Functions.WIP;
			case "codePointAt" -> Functions.WIP;
			case "padStart" -> Functions.WIP;
			case "padEnd" -> Functions.WIP;
			case "trimStart" -> Functions.WIP;
			case "trimEnd" -> Functions.WIP;
			default -> PROTOTYPE.get(cx, scope, this, name);
		};
	}

	@Override
	public void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		if (escape) {
			AstStringBuilder.wrapString(self, builder);
		} else {
			builder.append(s(self));
		}
	}

	@Override
	public Number asNumber(Context cx, Scope scope) {
		try {
			return TokenStreamJS.parseNumber(self);
		} catch (NumberFormatException ex) {
			return NumberJS.NaN;
		}
	}

	@Override
	public boolean asBoolean(Context cx, Scope scope) {
		return !self.isEmpty();
	}
}
