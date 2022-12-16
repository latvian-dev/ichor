package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.unary.AstUnary;
import org.jetbrains.annotations.Nullable;

public interface Token {
	@Nullable
	default Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return null;
	}

	@Nullable
	default AstUnary createUnaryAst(PositionedToken pos) {
		return null;
	}

	@Nullable
	default AstBinary createBinaryAst(PositionedToken pos) {
		return null;
	}
}
