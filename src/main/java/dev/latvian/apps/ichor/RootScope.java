package dev.latvian.apps.ichor;

public class RootScope extends Scope {
	public final Context context;
	public int maxScopeDepth;

	public RootScope(Context cx) {
		super(null);
		context = cx;
		root = this;
		owner = cx;
		maxScopeDepth = cx.getProperty("maxScopeDepth", 1000);
	}

	public void addSafeClasses() {
		for (var p : context.safePrototypes) {
			add(p.getPrototypeName(), p);
		}
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public String toString() {
		return "RootScope";
	}
}
