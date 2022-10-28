package dev.latvian.apps.ichor;

public class RootScope extends Scope {
	public final Context context;

	public RootScope(Context cx) {
		context = cx;
		root = this;
	}
}
