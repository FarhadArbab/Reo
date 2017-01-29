package nl.cwi.reo.constraintautomata;

import java.util.Collection;
import java.util.Map;

import nl.cwi.reo.semantics.api.Expression;
import nl.cwi.reo.semantics.api.Port;

public final class Relation implements Formula {

	private final String name;
	
	private final Term[] terms;
	
	public Relation(String name, Term... terms) {
		this.name = name;
		this.terms = terms;
	}
	
	public String toString() {
		return name + terms;
	}

	@Override
	public Formula evaluate(Map<String, Expression> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInternalName(Collection<? extends Port> intface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Term findAssignment(String variable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Formula substitute(String variable, Term expression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Formula rename(Map<Port, Port> links) {
		// TODO Auto-generated method stub
		return null;
	}
}
