package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.statement.decl.AstDeclaration;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.util.IchorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class AstForOf extends AstLabeledStatement {
	public DeclaringToken assignToken;
	public AstDeclaration declaration;
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
		builder.append(assignToken);
		builder.append(' ');
		builder.append(declaration);
		builder.append(' ');
		builder.append(appendKeyword());
		builder.append(' ');
		builder.appendValue(from);

		builder.append(')');

		if (body != null) {
			builder.append(body);
		} else {
			builder.append("{}");
		}
	}

	@Override
	public void interpret(Scope scope) {
		var self = scope.eval(from);
		var itr = getIterable(scope, self);

		while (itr != null && itr.hasNext()) {
			var it = itr.next();

			try {
				var s = scope.push();
				declaration.declare(s, assignToken.flags, it);
				body.interpretSafe(s);
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

	@Nullable
	protected Iterator<?> getIterable(Scope scope, Object self) {
		var itr = IchorUtils.iteratorOf(self);

		if (itr != null) {
			return itr;
		}

		var p = scope.getPrototype(self);
		return IchorUtils.iteratorOf(p.values(scope, p.cast(self)));
	}

	@Override
	public void optimize(Parser parser) {
		from = parser.optimize(from);
		body.optimize(parser);
	}
}
