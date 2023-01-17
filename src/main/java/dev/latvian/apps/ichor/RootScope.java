package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ScriptTimedOutError;

public class RootScope extends Scope {
	public final Context context;
	public int maxScopeDepth;
	public long interpretingTimeout;
	protected long timeoutAt;

	public RootScope(Context cx) {
		super(null);
		context = cx;
		root = this;
		owner = cx;
		maxScopeDepth = cx.getProperty(Context.MAX_SCOPE_DEPTH);
		interpretingTimeout = cx.getProperty(Context.INTERPRETING_TIMEOUT);
	}

	public void addSafeClasses() {
		for (var p : context.safePrototypes) {
			add(p.getPrototypeName(), p);
		}
	}

	@Override
	public String toString() {
		return "RootScope";
	}

	public void checkTimeout() {
		if (timeoutAt > 0L && System.currentTimeMillis() >= timeoutAt) {
			throw new ScriptTimedOutError();
		}
	}

	public void interpret(Interpretable interpretable) {
		timeoutAt = interpretingTimeout > 0L ? System.currentTimeMillis() + interpretingTimeout : 0L;

		try {
			interpretable.interpret(context, this);
		} finally {
			timeoutAt = 0L;
		}
	}
}
