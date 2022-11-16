package dev.latvian.apps.ichor.ast;

public class AstStringBuilder {
	public static void wrapString(Object self, StringBuilder builder) {
		if (self == null) {
			builder.append("null");
		} else if (self instanceof CharSequence) {
			var s = self.toString().replace("\\", "\\\\");

			if (s.isEmpty()) {
				builder.append('\'');
				builder.append('\'');
			} else {
				int sq = s.indexOf('\'');

				if (sq >= 0 && s.indexOf('"') >= 0) {
					builder.append('\'');
					builder.append(s.replace("'", "\\'"));
					builder.append('\'');
				} else if (sq >= 0) {
					builder.append('"');
					builder.append(s);
					builder.append('"');
				} else {
					builder.append('\'');
					builder.append(s);
					builder.append('\'');
				}
			}
		} else {
			builder.append(self);
		}
	}

	public static boolean isKey(String key) {
		char[] c = key.toCharArray();

		if (c.length == 0) {
			return false;
		}

		if (!Character.isJavaIdentifierStart(c[0])) {
			return false;
		}

		for (int i = 1; i < c.length; i++) {
			if (!Character.isJavaIdentifierPart(c[i])) {
				return false;
			}
		}

		return true;
	}

	public static void wrapKey(String key, StringBuilder builder) {
		if (isKey(key)) {
			builder.append(key);
		} else {
			wrapString(key, builder);
		}
	}

	public final StringBuilder builder = new StringBuilder();

	public void append(CharSequence string) {
		builder.append(string);
	}

	public void append(String string) {
		builder.append(string);
	}

	public void append(Object o) {
		if (o instanceof AstAppendable ast) {
			ast.append(this);
		} else {
			builder.append(o);
		}
	}

	public void appendValue(Object o) {
		if (o instanceof CharSequence) {
			builder.append('"');
		}

		if (o instanceof AstAppendable ast) {
			ast.append(this);
		} else {
			builder.append(o);
		}

		if (o instanceof CharSequence) {
			builder.append('"');
		}
	}

	public void append(char c) {
		builder.append(c);
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
