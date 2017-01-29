package nl.cwi.reo.constraintautomata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.cwi.reo.automata.Label;
import nl.cwi.reo.semantics.api.Expression;
import nl.cwi.reo.semantics.api.Port;
import nl.cwi.reo.semantics.api.PortType;

public final class CAMLabel implements Label<CAMLabel> {
	
	/**
	 * Data constraint.
	 */
	private final Formula dc;
	
	/**
	 * Constructs a true data constraint label.
	 */
	public CAMLabel() {
		this.dc = new True();
	}
	
	/**
	 * Constructs a data constraint label from a given formula.
	 * @param dc	data constraint formula
	 */
	public CAMLabel(Formula dc) {
		if (dc == null)
			throw new NullPointerException();
		this.dc = dc;
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public CAMLabel compose(List<CAMLabel> lbls) {
		Formula dc = this.dc;
		for (CAMLabel lbl : lbls)
			dc = new Conjunction(dc, lbl.dc);
		return new CAMLabel(dc);
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public CAMLabel restrict(Collection<? extends Port> intface) {
		String x = dc.getInternalName(intface);
		Term t;
		if (x != null && (t = dc.findAssignment(x)) != null)
			return new CAMLabel(dc.substitute(x, t));
		return this;
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public CAMLabel rename(Map<Port, Port> links) {
		return new CAMLabel(dc.rename(links));
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public CAMLabel getDefault(Set<Port> N) {
		Formula dc = new True();
		for (Port p : N) {
			if (p.getType() == PortType.OUT) {
				for (Port q : N) {
					if (q.getType() == PortType.IN) {
						Variable snk = new Variable(q.getName());
						Variable src = new Variable(p.getName());
						Formula f = new Equation(snk, src);
						dc = new Conjunction(dc, f);
					}
				}
				break;
			}
		}
		return new CAMLabel(dc);
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public CAMLabel evaluate(Map<String, Expression> params) {
		return new CAMLabel(dc.evaluate(params));
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public String toString() {
		return dc.toString();
	}
}
