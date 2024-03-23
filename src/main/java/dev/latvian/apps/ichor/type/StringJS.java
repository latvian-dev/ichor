package dev.latvian.apps.ichor.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import dev.latvian.apps.ichor.util.IchorUtils;
import dev.latvian.apps.ichor.util.JavaArray;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StringJS extends Prototype<String> {
	public static class InvalidCodePointError extends ScriptError {
		public final String codePoint;

		public InvalidCodePointError(String codePoint) {
			super("Invalid code point " + codePoint);
			this.codePoint = codePoint;
		}
	}

	private static final Callable RAW = Functions.ofN((scope, args) -> {
		var sb = new StringBuilder();

		for (var o : JavaArray.of(args[0])) {
			sb.append(scope.asString(o, false));
		}

		return sb.toString();
	});

	private static final Callable FROM_CHAR_CODE = Functions.ofN((scope, args) -> {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		var chars = new char[n];

		for (int i = 0; i != n; ++i) {
			chars[i] = scope.asChar(args[i]);
		}

		return new String(chars);
	});

	private static final Callable FROM_CODE_POINT = Functions.ofN((scope, args) -> {
		int n = args.length;

		if (n < 1) {
			return "";
		}

		var codePoints = new int[n];

		for (int i = 0; i != n; i++) {
			int codePoint = scope.asInt(args[i]);

			if (!Character.isValidCodePoint(codePoint)) {
				throw new InvalidCodePointError(scope.asString(args[i], true));
			}

			codePoints[i] = codePoint;
		}

		return new String(codePoints, 0, n);
	});

	private static final Functions.Bound<String> CHAR_AT = (scope, str, args) -> str.charAt(scope.asInt(args[0]));
	private static final Functions.Bound<String> TRIM = (scope, str, args) -> str.trim();

	public StringJS(Scope scope) {
		super(scope, "String", String.class);
	}

	@Override
	public Object call(Scope scope, Object[] args, boolean hasNew) {
		return args.length == 0 ? "" : scope.asString(args[0], false);
	}

	@Override
	@Nullable
	public Object getStatic(Scope scope, String name) {
		return switch (name) {
			case "raw" -> RAW;
			case "fromCharCode" -> FROM_CHAR_CODE;
			case "fromCodePoint" -> FROM_CODE_POINT;
			default -> super.getStatic(scope, name);
		};
	}

	@Override
	@Nullable
	@SuppressWarnings("DuplicateBranchesInSwitch")
	public Object getLocal(Scope scope, String self, String name) {
		return switch (name) {
			case "length" -> self.length();
			case "charAt" -> CHAR_AT.with(self);
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
			case "trim" -> TRIM.with(self);
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
			default -> super.getLocal(scope, self, name);
		};
	}

	@Override
	public int getLength(Scope scope, Object self) {
		return ((CharSequence) self).length();
	}

	@Override
	public Collection<?> keys(Scope scope, String self) {
		if (self.isEmpty()) {
			return List.of();
		}

		var obj = new Object[self.length()];

		for (int i = 0; i < obj.length; i++) {
			obj[i] = i;
		}

		return Arrays.asList(obj);
	}

	@Override
	public Collection<?> values(Scope scope, String self) {
		if (self.isEmpty()) {
			return List.of();
		}

		var obj = new Object[self.length()];

		for (int i = 0; i < obj.length; i++) {
			obj[i] = self.charAt(i);
		}

		return Arrays.asList(obj);
	}

	@Override
	public Collection<?> entries(Scope scope, String self) {
		if (self.isEmpty()) {
			return List.of();
		}

		var obj = new Object[self.length()];

		for (int i = 0; i < obj.length; i++) {
			obj[i] = List.of(i, self.charAt(i));
		}

		return Arrays.asList(obj);
	}

	@Override
	public boolean asString(Scope scope, String self, StringBuilder builder, boolean escape) {
		if (escape) {
			AstStringBuilder.wrapString(self, builder);
		} else {
			builder.append(self);
		}

		return true;
	}

	@Override
	public Number asNumber(Scope scope, String self) {
		try {
			return IchorUtils.parseNumber(self);
		} catch (NumberFormatException ex) {
			return IchorUtils.NaN;
		}
	}

	@Override
	public Boolean asBoolean(Scope scope, String self) {
		return !self.isEmpty();
	}
}
