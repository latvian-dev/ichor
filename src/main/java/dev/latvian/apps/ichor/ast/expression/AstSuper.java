package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

public class AstSuper extends AstExpression implements Prototype {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("super");
	}

	@Override
	public String getPrototypeName() {
		return "super";
	}

	@Override
	public Object call(Scope scope, Object self, Object[] args) {
		return Special.NOT_FOUND;
	}

	@Override
	@Nullable
	public Object get(Scope scope, Object self, String name) {
		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Scope scope, Object self, String name, @Nullable Object value) {
		return false;
	}
}
