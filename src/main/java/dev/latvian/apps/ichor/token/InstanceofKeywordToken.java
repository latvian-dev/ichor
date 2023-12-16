package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.binary.AstInstanceOf;

public class InstanceofKeywordToken extends KeywordToken {
	public InstanceofKeywordToken() {
		super("instanceof");
	}

	@Override
	public AstBinary createBinaryAst(PositionedToken pos) {
		return new AstInstanceOf();
	}
}
