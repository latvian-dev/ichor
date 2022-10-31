package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.js.ArrayJS;
import dev.latvian.apps.ichor.js.BooleanJS;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.js.ObjectJS;
import dev.latvian.apps.ichor.js.StringJS;
import dev.latvian.apps.ichor.parser.Parser;
import dev.latvian.apps.ichor.token.TokenStream;

public class RootScope extends Scope {
	public final Context context;
	public Scope current;

	public RootScope(Context cx) {
		context = cx;
		root = this;
		current = this;
	}

	public void addSafeClasses() {
		add(StringJS.PROTOTYPE);
		add(NumberJS.PROTOTYPE);
		add(BooleanJS.PROTOTYPE);
		add(ObjectJS.PROTOTYPE);
		add(ArrayJS.PROTOTYPE);
	}

	@Override
	public Context getContext() {
		return context;
	}

	public Object interpret(String code) {
		var tokens = new TokenStream(code).getTokens();
		var ast = new Parser(tokens).parse();
		ast.interpret(this);
		return Special.NOT_FOUND;
	}

	@Override
	public String toString() {
		return "RootScope";
	}
}
