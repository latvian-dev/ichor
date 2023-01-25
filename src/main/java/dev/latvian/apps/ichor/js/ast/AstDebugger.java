package dev.latvian.apps.ichor.js.ast;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.statement.AstStatement;
import dev.latvian.apps.ichor.js.ContextJS;

public class AstDebugger extends AstStatement {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("debugger");
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		((ContextJS) cx).onDebugger(scope);
	}
}
