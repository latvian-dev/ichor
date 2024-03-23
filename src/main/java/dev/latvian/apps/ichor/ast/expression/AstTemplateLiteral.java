package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.util.IchorUtils;

import java.util.ArrayList;

public class AstTemplateLiteral extends AstExpression {
	public Object tag;
	public final Object[] parts;

	public AstTemplateLiteral(Object[] parts) {
		this.parts = parts;
	}

	@Override
	public void append(AstStringBuilder builder) {
		if (tag != null) {
			builder.appendValue(tag);
		}

		builder.append('`');

		for (var part : parts) {
			if (part instanceof CharSequence token) {
				builder.append(token.toString());
			} else {
				builder.append("${");
				builder.append(part);
				builder.append('}');
			}
		}

		builder.append('`');
	}

	@Override
	public String eval(Scope scope) {
		if (tag != null) {
			var func = scope.eval(tag);

			if (func instanceof Callable c) {
				var args = new ArrayList<>((int) Math.floor(parts.length / 2D));
				var strings = new ArrayList<>((int) Math.ceil(parts.length / 2D));
				args.add(strings);

				for (var part : parts) {
					if (part instanceof CharSequence str) {
						strings.add(str.toString());
					} else {
						args.add(scope.eval(part));
					}
				}

				return scope.asString(c.call(scope, args.toArray(), false), false);
			} else {
				throw new AstCall.CallError(tag, func, scope.getPrototype(func));
			}
		}

		var builder = new StringBuilder();
		evalString(scope, builder);
		return builder.toString();
	}

	@Override
	public void evalString(Scope scope, StringBuilder builder) {
		for (var part : parts) {
			if (part instanceof Evaluable eval) {
				eval.evalString(scope, builder);
			} else {
				builder.append(part);
			}
		}
	}

	@Override
	public double evalDouble(Scope scope) {
		try {
			return IchorUtils.parseNumber(eval(scope)).doubleValue();
		} catch (Exception ex) {
			return Double.NaN;
		}
	}

	@Override
	public int evalInt(Scope scope) {
		try {
			return IchorUtils.parseNumber(eval(scope)).intValue();
		} catch (Exception ex) {
			throw new InternalScriptError(ex);
		}
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !eval(scope).isEmpty();
	}

	@Override
	public Object optimize(Parser parser) {
		if (parts.length == 0) {
			return "";
		}

		tag = parser.optimize(tag);

		boolean onlyStrings = tag == null;

		for (int i = 0; i < parts.length; i++) {
			parts[i] = parser.optimize(parts[i]);

			if (!(parts[i] instanceof CharSequence)) {
				onlyStrings = false;
			}
		}

		if (onlyStrings) {
			var sb = new StringBuilder();

			for (var part : parts) {
				sb.append(part);
			}

			return sb.toString();
		}

		return this;
	}
}
