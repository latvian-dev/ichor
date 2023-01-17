package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.prototype.Prototype;

public class MemberNotFoundError extends ScriptError {
	public final String member;
	public final Prototype prototype;

	public MemberNotFoundError(String member) {
		super("Member " + member + " not found");
		this.member = member;
		this.prototype = null;
	}

	public MemberNotFoundError(String member, Prototype prototype) {
		super("Member " + member + " of " + prototype + " not found");
		this.member = member;
		this.prototype = prototype;
	}
}
