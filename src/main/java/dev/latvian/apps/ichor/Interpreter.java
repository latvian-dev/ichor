package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.parser.Parser;
import dev.latvian.apps.ichor.token.TokenStream;

public class Interpreter {
	public final Context context;
	public Scope scope;

	public Object returnValue;

	public Interpreter(Context cx) {
		context = cx;
		scope = cx.rootScope;
	}

	public Object interpret(String code) {
		returnValue = null;
		var tokens = new TokenStream(code).getTokens();
		var ast = new Parser(tokens).parse();
		ast.interpret(this);
		return Special.NOT_FOUND;
	}

	public void pushScope() {
		scope = scope.createChildScope();
	}

	public void popScope() {
		scope = scope.parent;
	}
}
