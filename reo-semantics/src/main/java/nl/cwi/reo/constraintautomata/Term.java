package nl.cwi.reo.constraintautomata;

import java.util.Map;

import nl.cwi.reo.semantics.api.Evaluable;
import nl.cwi.reo.semantics.api.Port;

public interface Term extends Evaluable<Term> {
	
	public boolean contains(String variable);
	
	public Term substitute(String variable, Term expression);

	public Term rename(Map<Port, Port> links);
	
}
