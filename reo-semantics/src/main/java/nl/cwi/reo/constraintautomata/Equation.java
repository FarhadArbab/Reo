package nl.cwi.reo.constraintautomata;

import java.util.Collection;
import java.util.Map;

import nl.cwi.reo.semantics.api.Expression;
import nl.cwi.reo.semantics.api.Port;

public final class Equation implements Formula {

	private final Term t1;
	
	private final Term t2;
	
	public Equation(Term t1, Term t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public Formula evaluate(Map<String, Expression> params) {
		return new Equation(t1.evaluate(params), t2.evaluate(params));
	}

	@Override
	public String getInternalName(Collection<? extends Port> intface) {
		if (t1 instanceof Variable) {
			String name = ((Variable)t1).getName();
			if (intface.contains(new Port(name)) && !t2.contains(name))
				return name;
		} else if (t2 instanceof Variable) {
			String name = ((Variable)t2).getName();
			if (intface.contains(new Port(name)) && !t1.contains(name))
				return name;
		}
		return null;
	}

	@Override
	public Term findAssignment(String variable) {
		if (t1 instanceof Variable) {
			String name = ((Variable)t1).getName();
			if (name.equals(variable))
				return t2;
		} else if (t2 instanceof Variable) {
			String name = ((Variable)t2).getName();
			if (name.equals(variable))
				return t1;
		}
		return null;
	}

	@Override
	public Formula substitute(String variable, Term expression) {
		Term t1_s = t1.substitute(variable, expression);
		Term t2_s = t2.substitute(variable, expression);
		if (t1_s.equalto(t2_s)) return new True();
		return new Equation(t1_s, t2_s);
	}

	@Override
	public Formula rename(Map<Port, Port> links) {
		return new Equation(t1.rename(links), t2.rename(links));
	}
}