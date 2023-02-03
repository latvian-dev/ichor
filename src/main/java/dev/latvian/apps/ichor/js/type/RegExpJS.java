package dev.latvian.apps.ichor.js.type;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ArgumentCountMismatchError;
import dev.latvian.apps.ichor.prototype.Prototype;

import java.util.regex.Pattern;

public class RegExpJS extends Prototype<Pattern> {
	public RegExpJS(Context cx) {
		super(cx, "RegExp", Pattern.class);
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		if (args.length == 0) {
			throw new ArgumentCountMismatchError(1, 0);
		}

		int flags = 0;

		if (args.length >= 2) {
			for (char c : cx.asString(scope, args[1], false).toCharArray()) {
				switch (c) {
					case 'd' -> flags |= Pattern.UNIX_LINES;
					case 'i' -> flags |= Pattern.CASE_INSENSITIVE;
					case 'x' -> flags |= Pattern.COMMENTS;
					case 'm' -> flags |= Pattern.MULTILINE;
					case 's' -> flags |= Pattern.DOTALL;
					case 'u' -> flags |= Pattern.UNICODE_CASE;
					case 'U' -> flags |= Pattern.UNICODE_CHARACTER_CLASS;
					case 'g' -> throw new IllegalArgumentException("g flag is not supported!");
				}
			}
		}

		return Pattern.compile(cx.asString(scope, args[0], false), flags);
	}

	@Override
	public boolean asString(Context cx, Scope scope, Pattern self, StringBuilder builder, boolean escape) {
		builder.append('/');
		builder.append(self.pattern());
		builder.append('/');

		var flags = self.flags();

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

		return true;
	}
}
