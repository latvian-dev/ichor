package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstDebugger extends AstStatement {

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("debugger");
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		cx.debugger.debuggerStatement(cx, scope);
	}
}
