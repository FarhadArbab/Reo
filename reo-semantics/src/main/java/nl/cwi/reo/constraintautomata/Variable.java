package nl.cwi.reo.constraintautomata;

import java.util.Map;
import java.util.Objects;

import nl.cwi.reo.semantics.api.Expression;
import nl.cwi.reo.semantics.api.Port;

public class Variable implements Term {
	
	private final String name;
	
	Variable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public Term evaluate(Map<String, Expression> params) {
		return this;
	}

	@Override
	public boolean contains(String variable) {
		return name.equals(variable);
	}

	@Override
	public Term substitute(String variable, Term expression) {
		if (name.equals(variable))
			return expression;
		return this;
	}

	@Override
	public Term rename(Map<Port, Port> links) {
		Port p = links.get(new Port(name));
		if (p != null)
			return new Variable(p.getName());
		return this;
	}
	
	@Override
	public boolean equals(Object other) {
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Variable)) return false;
	    Variable p = (Variable)other;
	   	return Objects.equals(this.name, p.name);
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(this.name);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
