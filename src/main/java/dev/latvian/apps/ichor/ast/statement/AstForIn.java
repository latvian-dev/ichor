package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AstForIn extends AstForOf {
	public AstForIn(String name, Evaluable from, @Nullable Interpretable body, String label) {
		super(name, from, body, label);
	}

	@Override
	protected String appendKeyword() {
		return " in ";
	}

	@Override
	protected Collection<?> getIterable(Scope scope, Prototype prototype, Object from) {
		return prototype.keys(scope, from);
	}
}
