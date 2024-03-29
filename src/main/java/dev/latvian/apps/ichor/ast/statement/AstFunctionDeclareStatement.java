package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.token.Keyword;

public class AstFunctionDeclareStatement extends AstDeclareStatement {
	public final AstFunction function;

	public AstFunctionDeclareStatement(AstFunction function) {
		super(Keyword.CONST);
		this.function = function;
	}

	@Override
	public void interpret(Scope scope) {
		scope.addImmutable(function.functionName, scope.eval(function));
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(function);
	}

	@Override
	public void optimize(Parser parser) {
		function.optimize(parser);
	}
}
