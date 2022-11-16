package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.util.Map;

public class AstMap extends AstExpression {
	public final Map<String, Evaluable> map;

	public AstMap(Map<String, Evaluable> map) {
		this.map = map;
	}

	@Override
	public Object eval(Scope scope) {
		return null;
	}

	@Override
	public void append(AstStringBuilder builder) {

	}
}
