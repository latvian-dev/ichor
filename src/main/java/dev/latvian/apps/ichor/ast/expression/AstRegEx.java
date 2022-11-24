package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.util.regex.Pattern;

public class AstRegEx extends AstExpression {
	public final Pattern pattern;

	public AstRegEx(Pattern p) {
		pattern = p;
	}

	@Override
	public Object eval(Scope scope) {
		return pattern;
	}

	@Override
	public void append(AstStringBuilder builder) {
		appendRegEx(builder.builder, pattern);
	}

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

	@Override
	public boolean equals(Evaluable right, Scope scope, boolean shallow) {
		if (right.eval(scope) instanceof Pattern r) {
			return pattern.pattern().equals(r.pattern()) && pattern.flags() == r.flags();
		}

		return false;
	}
}