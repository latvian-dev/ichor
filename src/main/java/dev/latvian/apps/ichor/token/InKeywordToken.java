package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.binary.AstIn;

public class InKeywordToken extends KeywordToken {
	public InKeywordToken() {
		super("in");
		identifier();
	}

	@Override
	public AstBinary createBinaryAst(PositionedToken pos) {
		return new AstIn();
	}
}
