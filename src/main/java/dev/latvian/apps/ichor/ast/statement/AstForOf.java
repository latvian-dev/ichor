package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;

import java.util.Collection;

public class AstForOf extends AstLabeledStatement {
	public String name;
	public Object from;
	public Interpretable body;

	protected String appendKeyword() {
		return " of ";
	}

	@Override
	public void append(AstStringBuilder builder) {
		if (!label.isEmpty()) {
			builder.append(label);
			builder.append(':');
		}

		builder.append("for(");
		builder.append(name);
		builder.append(appendKeyword());
		builder.appendValue(from);

		builder.append(')');

		if (body != null) {
			builder.append(body);
		} else {
			builder.append("{}");
		}
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		var f = cx.eval(scope, from);
		var itr = getIterable(cx, scope, f);

		if (itr.isEmpty()) {
			return;
		}

		for (var it : itr) {
			try {
				var s = scope.push();
				s.addMutable(name, it);
				body.interpretSafe(cx, s);
			} catch (BreakExit exit) {
				if (exit.stop == this) {
					break;
				} else {
					throw exit;
				}
			} catch (ContinueExit exit) {
				if (exit.stop != this) {
					throw exit;
				}
			}
		}
	}

	protected Collection<?> getIterable(Context cx, Scope scope, Object from) {
		if (from instanceof Collection<?>) {
			return (Collection<?>) from;
		}

		return cx.wrap(scope, from).values(cx, scope);
	}

	@Override
	public void optimize(Parser parser) {
		from = parser.optimize(from);
		body.optimize(parser);
	}
}
