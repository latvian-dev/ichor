package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.binary.AstIn;
import dev.latvian.apps.ichor.token.BinaryOpToken;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.PositionedToken;

public class InKeywordJS extends KeywordToken implements BinaryOpToken {
	public InKeywordJS() {
		super("in", true);
	}

	@Override
	public AstBinary createBinaryAst(PositionedToken pos) {
		return new AstIn();
	}
}
