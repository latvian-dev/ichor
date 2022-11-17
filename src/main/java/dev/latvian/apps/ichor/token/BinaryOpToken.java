package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;

public interface BinaryOpToken {
	AstBinary createBinaryAst(PositionedToken pos);
}
