package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.MemberNotFoundError;
import org.jetbrains.annotations.Nullable;

public class AstGetScopeMember extends AstGetBase {
	public final String name;

	public AstGetScopeMember(String name) {
		this.name = name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var r = scope.getMember(name);

		if (r == Special.NOT_FOUND) {
			throw new MemberNotFoundError(name);
		}

		cx.debugger.get(cx, scope, this, r);
		return r;
	}

	@Override
	@Nullable
	public Object evalSelf(Context cx, Scope scope) {
		var r = scope.getMemberOwner(name);

		if (r == Special.NOT_FOUND) {
			throw new MemberNotFoundError(name);
		}

		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		cx.debugger.set(cx, scope, this, value);
		scope.setMember(name, value);
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		cx.debugger.delete(cx, scope, this);
		scope.deleteDeclaredMember(name);
		return true;
	}
}
