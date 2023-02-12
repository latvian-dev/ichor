package dev.latvian.apps.ichor.lang.js;

import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.PositionedToken;

public class InstanceofKeywordJS extends KeywordToken {
	public InstanceofKeywordJS() {
		super("instanceof");
	}

	@Override
	public AstBinary createBinaryAst(PositionedToken pos) {
		return new AstInstanceOf();
	}
}