package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.math.BigInteger;

public class AstTypeOf extends AstExpression {
	public Object of;

	public AstTypeOf(Object of) {
		this.of = of;
	}

	@Override
	public Object eval(Scope scope) {
		var o = scope.eval(of);

		if (o == Special.UNDEFINED) {
			return "undefined";
		} else if (o instanceof String) {
			return "string";
		} else if (o instanceof Number) {
			return "number";
		} else if (o instanceof Boolean) {
			return "boolean";
		} else if (o instanceof BigInteger) {
			return "bigint";
		} else if (o instanceof Callable) {
			return "function";
		} else {
			return "object";
		}
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("typeof ");
		builder.appendValue(of);
	}

	@Override
	public Object optimize(Parser parser) {
		of = parser.optimize(of);

		if (of == Special.UNDEFINED) {
			return "undefined";
		} else if (of instanceof String) {
			return "string";
		} else if (of instanceof Number) {
			return "number";
		} else if (of instanceof Boolean) {
			return "boolean";
		} else if (of instanceof BigInteger) {
			return "bigint";
		} else {
			return this;
		}
	}
}
