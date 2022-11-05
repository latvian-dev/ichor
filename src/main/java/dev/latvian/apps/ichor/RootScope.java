package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.js.ArrayJS;
import dev.latvian.apps.ichor.js.BooleanJS;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.js.ObjectJS;
import dev.latvian.apps.ichor.js.StringJS;

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
		add(StringJS.PROTOTYPE);
		add(NumberJS.PROTOTYPE);
		add(BooleanJS.PROTOTYPE);
		add(ObjectJS.PROTOTYPE);
		add(ArrayJS.PROTOTYPE);
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
