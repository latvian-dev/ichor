package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ScopeExit;

import java.util.Collection;

public class AstBlock extends AstInterpretableGroup {

	public AstBlock(Collection<Interpretable> statements) {
		super(statements);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('{');
		super.append(builder);
		builder.append('}');
	}

	@Override
	public void interpret(Scope scope) throws ScopeExit {
		super.interpret(scope.push());
	}
}
