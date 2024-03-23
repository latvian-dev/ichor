package dev.latvian.apps.ichor;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Context {
	private int maxScopeDepth;
	private long interpretingTimeout;
	private long tokenStreamTimeout;
	private ClassLoader classLoader;
	private Executor timeoutExecutor, timeoutExecutorAfter;
	private DebuggerCallback debuggerCallback;

	public Context() {
		maxScopeDepth = 1000;
		interpretingTimeout = 30000L;
		tokenStreamTimeout = 5000L;
		classLoader = null;
		timeoutExecutor = null;
		timeoutExecutorAfter = null;
	}

	public int getMaxScopeDepth() {
		return maxScopeDepth;
	}

	public void setMaxScopeDepth(int maxScopeDepth) {
		this.maxScopeDepth = maxScopeDepth;
	}

	public long getInterpretingTimeout() {
		return interpretingTimeout;
	}

	public void setInterpretingTimeout(long interpretingTimeout) {
		this.interpretingTimeout = interpretingTimeout;
	}

	public long getTokenStreamTimeout() {
		return tokenStreamTimeout;
	}

	public void setTokenStreamTimeout(long tokenStreamTimeout) {
		this.tokenStreamTimeout = tokenStreamTimeout;
	}

	@Nullable
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader cl) {
		classLoader = cl;
	}

	@Nullable
	public Executor getTimeoutExecutor() {
		if (timeoutExecutor == null) {
			timeoutExecutor = CompletableFuture.completedFuture(null).defaultExecutor();
		}

		return timeoutExecutor;
	}

	public void setTimeoutExecutor(Executor executor) {
		timeoutExecutor = executor;
	}

	@Nullable
	public Executor getTimeoutExecutorAfter() {
		return timeoutExecutorAfter;
	}

	public void setTimeoutExecutorAfter(Executor executor) {
		timeoutExecutorAfter = executor;
	}

	public void setDebuggerCallback(DebuggerCallback callback) {
		debuggerCallback = callback;
	}

	public void onDebugger(Scope scope) {
		if (debuggerCallback != null) {
			debuggerCallback.onDebugger(scope);
		}
	}

	@Override
	public String toString() {
		return "Context";
	}
}
