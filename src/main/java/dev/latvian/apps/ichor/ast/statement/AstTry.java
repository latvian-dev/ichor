package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import org.jetbrains.annotations.Nullable;

public class AstTry extends AstStatement {
	public record AstCatch(String name, Interpretable body) {
	}

	public final Interpretable tryBlock;
	public final AstCatch catchBlock;
	public final Interpretable finallyBlock;

	public AstTry(Interpretable tryBlock, @Nullable AstCatch catchBlock, @Nullable Interpretable finallyBlock) {
		this.tryBlock = tryBlock;
		this.catchBlock = catchBlock;
		this.finallyBlock = finallyBlock;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("try ");
		if (tryBlock != null) {
			builder.append(tryBlock);
		} else {
			builder.append("{}");
		}

		if (catchBlock != null) {
			builder.append(" catch (");
			builder.append(catchBlock.name);
			builder.append(") ");

			if (catchBlock.body != null) {
				builder.append(catchBlock.body);
			} else {
				builder.append("{}");
			}
		}

		if (finallyBlock != null) {
			builder.append(" finally ");
			builder.append(finallyBlock);
		}
	}

	@Override
	public void interpret(Scope scope) {
		try {
			if (tryBlock != null) {
				tryBlock.interpretSafe(scope);
			}
		} catch (Exception ex) {
			if (catchBlock != null && catchBlock.body != null) {
				var s = scope.push();
				s.addMutable(catchBlock.name, ex);
				catchBlock.body.interpretSafe(s);
			}
		} finally {
			if (finallyBlock != null) {
				finallyBlock.interpretSafe(scope);
			}
		}
	}

	@Override
	public void optimize(Parser parser) {
		if (tryBlock != null) {
			tryBlock.optimize(parser);
		}

		if (catchBlock != null && catchBlock.body != null) {
			catchBlock.body.optimize(parser);
		}

		if (finallyBlock != null) {
			finallyBlock.optimize(parser);
		}
	}
}
