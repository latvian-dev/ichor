package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.binary.AstInstanceOf;
import dev.latvian.apps.ichor.token.BinaryOpToken;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.PositionedToken;

public class InstanceofKeywordJS extends KeywordToken implements BinaryOpToken {
	public InstanceofKeywordJS() {
		super("instanceof", false);
	}

	@Override
	public AstBinary createBinaryAst(PositionedToken pos) {
		return new AstInstanceOf();
	}
}
