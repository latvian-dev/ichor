package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstExpression;
import dev.latvian.apps.ichor.ast.expression.AstGetBase;

public class AstDelete extends AstExpression {
	public final AstGetBase get;

	public AstDelete(AstGetBase get) {
		this.get = get;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("delete ");
		get.append(builder);
	}

	@Override
	public Object eval(Scope scope) {
		return get.delete(scope);
	}
}
