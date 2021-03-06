package nl.cwi.reo.interpret.terms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.cwi.reo.interpret.Scope;
import nl.cwi.reo.interpret.values.IntegerValue;
import nl.cwi.reo.interpret.variables.Identifier;
import nl.cwi.reo.util.Monitor;

/**
 * Interpretation of a range of terms.
 */
public final class Range implements TermExpression {

	/**
	 * First term in range.
	 */
	private final TermExpression t1;

	/**
	 * Last term in range.
	 */
	private final TermExpression t2;

	/**
	 * Constructs a new range expression
	 * 
	 * @param t1
	 *            first term in range
	 * @param t2
	 *            last term in range
	 */
	public Range(TermExpression t1, TermExpression t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	public Scope findParamFromSize(int size) {
		Scope s = new Scope();
		List<Term> e1 = t1.evaluate(new Scope(), null);
		List<Term> e2 = t2.evaluate(new Scope(), null);

		if (e1 != null && e1.size() == 1 && e2 != null && e2.size() == 1) {
			Term g1 = e1.get(0);
			Term g2 = e2.get(0);

			if (g1 instanceof Identifier && g2 instanceof IntegerValue)
				s.put((Identifier) g1, new IntegerValue(((IntegerValue) g2).getValue() - size + 1));
			else if (g1 instanceof IntegerValue && g2 instanceof Identifier)
				s.put((Identifier) g2, new IntegerValue(((IntegerValue) g1).getValue() + size - 1));
			else
				return null;
		}

		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Identifier> getVariables() {
		Set<Identifier> vars = new HashSet<Identifier>();
		vars.addAll(t1.getVariables());
		vars.addAll(t2.getVariables());
		return vars;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Nullable
	public List<Term> evaluate(Scope s, Monitor m) {
		List<Term> list = new ArrayList<Term>();
		List<Term> te1 = t1.evaluate(s, m);
		if (te1 == null)
			return null;
		List<Term> te2 = t2.evaluate(s, m);
		if (te2 == null)
			return null;
		list.addAll(te1);
		if (!te1.isEmpty() && !te2.isEmpty() && te1.get(te1.size() - 1) instanceof IntegerValue
				&& te2.get(0) instanceof IntegerValue)
			for (int k = ((IntegerValue) te1.get(te1.size() - 1)).getValue() + 1; k < ((IntegerValue) te2.get(0))
					.getValue(); k++)
				list.add(new IntegerValue(k));
		if (!te1.equals(te2))
			list.addAll(te2);
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return t1 + ".." + t2;
	}

}
