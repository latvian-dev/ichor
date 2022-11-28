package dev.latvian.apps.ichor.ast;

import dev.latvian.apps.ichor.token.TokenPos;
import dev.latvian.apps.ichor.token.TokenPosSupplier;

public abstract class Ast implements AstAppendable, TokenPosSupplier {
	public TokenPos pos = TokenPos.UNKNOWN;

	@Override
	public String toString() {
		var sb = new AstStringBuilder();
		append(sb);

		if (pos != TokenPos.UNKNOWN) {
			sb.append(" @ ");
			sb.append(pos);
		}

		return sb.toString();
	}

	public Ast pos(TokenPosSupplier pos) {
		this.pos = pos.getPos();
		return this;
	}

	@Override
	public TokenPos getPos() {
		return pos;
	}
}
