package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.error.ParseErrorType;

public class AstThisExpression extends AstTempExpression {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("this");
	}

	@Override
	public Object optimize(Parser parser) {
		throw new ParseError(pos, ParseErrorType.INVALID_THIS);
	}
}
