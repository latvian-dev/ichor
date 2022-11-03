package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstGetFrom;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstDelete extends AstStatement {
	public final AstGetFrom get;

	public AstDelete(AstGetFrom get) {
		this.get = get;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("delete ");
		get.append(builder);
	}

	@Override
	public void interpret(Scope scope) {
		var o = scope.eval(get.from);
		var p = scope.getContext().getPrototype(o);
		var k = get.evalKey(scope);

		if (!(k instanceof Number n ? p.delete(scope, o, n.intValue()) : p.delete(scope, o, scope.getContext().asString(scope, k)))) {
			throw new ScriptError("Cannot delete " + get);
		}
	}
}
