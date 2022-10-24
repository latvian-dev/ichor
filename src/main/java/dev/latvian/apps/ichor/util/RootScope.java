package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.IchorError;
import org.jetbrains.annotations.Nullable;

public class RootScope extends ScopeImpl {
	public final Context context;

	public RootScope(Context cx) {
		context = cx;
	}

	@Override
	@Nullable
	public Scope getParentScope() {
		return null;
	}

	@Override
	public RootScope getRootScope() {
		return this;
	}

	@Override
	public void setParentScope(@Nullable Scope s) {
		throw new IchorError("Root scope cannot have a parent!");
	}
}
