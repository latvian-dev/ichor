package dev.latvian.apps.ichor.js;

import java.util.regex.Pattern;

public class RegExpJS {
	public static void appendRegEx(StringBuilder builder, Pattern pattern) {
		builder.append('/');
		builder.append(pattern.pattern());
		builder.append('/');

		var flags = pattern.flags();

		if ((flags & Pattern.UNIX_LINES) != 0) {
			builder.append('d');
		}

		if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
			builder.append('i');
		}

		if ((flags & Pattern.COMMENTS) != 0) {
			builder.append('x');
		}

		if ((flags & Pattern.MULTILINE) != 0) {
			builder.append('m');
		}

		if ((flags & Pattern.DOTALL) != 0) {
			builder.append('s');
		}

		if ((flags & Pattern.UNICODE_CASE) != 0) {
			builder.append('u');
		}

		if ((flags & Pattern.UNICODE_CHARACTER_CLASS) != 0) {
			builder.append('U');
		}
	}
}
