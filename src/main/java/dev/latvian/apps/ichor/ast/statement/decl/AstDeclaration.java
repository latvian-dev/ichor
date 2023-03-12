package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AppendableAst;

public interface AstDeclaration extends AppendableAst {
	AstDeclaration[] EMPTY = new AstDeclaration[0];

	void declare(Context cx, Scope scope, byte flags, Object of);
}
