package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstDelete extends AstExpression {
	public final AstGetBase get;

	public AstDelete(AstGetBase get) {
		this.get = get;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("delete ");
		get.append(builder);
		builder.append(';');
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return get.delete(cx, scope);
	}

	@Override
	public Object optimize(Parser parser) {
		get.optimize(parser);
		return this;
	}
}
