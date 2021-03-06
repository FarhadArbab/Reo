package nl.cwi.reo.semantics.symbolicautomata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.cwi.reo.interpret.Scope;
import nl.cwi.reo.interpret.ports.Port;
import nl.cwi.reo.util.Monitor;

public class Negation implements Formula {
	
	private final Formula f;

	public Negation(Formula f) {
		this.f = f;
	}

	@Override
	public Formula getGuard() {
//		throw new UnsupportedOperationException();
		return new Negation(f.getGuard());
	}

	@Override
	public Map<Port, Term> getAssignment() {
//		if(f instanceof Synchron)
//			return new HashMap<Port,Term>();
//		if(f instanceof Equality)
//			return ((Equality)f).getAssignment(false);
		return new HashMap<Port,Term>();
	}

	@Override
	public Formula rename(Map<Port, Port> links) {
		return new Negation(f.rename(links));
	}
	
	public Formula getFormula(){
		return f;
	}

	@Override
	public Set<Port> getInterface() {
		return null;
	}

	@Override
	public @Nullable Formula evaluate(Scope s, Monitor m) {
		return null;
	}
	
	public String toString(){
		return "!(" + f.toString() + ")";
	}

	@Override
	public Formula DNF() {
		return null;
	}

	@Override
	public Formula propNegation(boolean isNegative) {
		return f.propNegation(!isNegative);
	}

}
