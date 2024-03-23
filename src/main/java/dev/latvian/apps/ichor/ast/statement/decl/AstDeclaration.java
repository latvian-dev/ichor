package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AppendableAst;

public interface AstDeclaration extends AppendableAst {
	AstDeclaration[] EMPTY = new AstDeclaration[0];

	void declare(Scope scope, byte flags, Object of);
}
