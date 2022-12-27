package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.AssignType;

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
		builder.append(from);

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
		var p = cx.getPrototype(scope, f);
		var itr = getIterable(cx, scope, p, f);

		if (itr.isEmpty()) {
			return;
		}

		for (var it : itr) {
			try {
				var s = scope.push();
				s.declareMember(name, it, AssignType.MUTABLE);
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

	protected Collection<?> getIterable(Context cx, Scope scope, Prototype prototype, Object from) {
		return prototype.values(cx, scope, from);
	}

	@Override
	public void optimize(Parser parser) {
		from = parser.optimize(from);
		body.optimize(parser);
	}
}
