package nl.cwi.reo.interpret;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.cwi.reo.util.Monitor;

/**
 * An expression that evaluates, for a given scope, to given type.
 * 
 * @param <T>
 *            expression type
 */
public interface Expression<T> {

	/**
	 * Evaluates this expression with respect to a given scope, and adds any
	 * messages to the monitor.
	 * 
	 * @param s
	 *            variable assignment
	 * @param m
	 *            message container
	 * @return evaluated expression.
	 */
	@Nullable
	public T evaluate(Scope s, Monitor m);

}
