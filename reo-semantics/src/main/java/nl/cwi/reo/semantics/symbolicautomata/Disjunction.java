package nl.cwi.reo.semantics.symbolicautomata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.cwi.reo.interpret.Scope;
import nl.cwi.reo.interpret.ports.Port;
import nl.cwi.reo.util.Monitor;

public class Disjunction implements Formula {
	
	private final List<Formula> g;
	
	public Disjunction(List<Formula> g) {
		this.g = g;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Formula getGuard() {
		List<Formula> h = new ArrayList<Formula>();
		for (Formula f : g)
			h.add(f.getGuard());
		return new Disjunction(h);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Port, Term> getAssignment() {
		Map<Port, Term> assignment = new HashMap<Port, Term>();
		for (Formula f : g) {
			Map<Port, Term> assignment1 = f.getAssignment();
			if (assignment1 == null)
				return null;
			for (Map.Entry<Port, Term> pair : assignment1.entrySet())
				if (assignment.put(pair.getKey(), pair.getValue()) != null)
					return null;
		}
		return assignment;
	}

	@Override
	public Formula rename(Map<Port, Port> links) {
		List<Formula> h = new ArrayList<Formula>();
		for (Formula f : g)
			h.add(f.rename(links));
		return new Disjunction(h);
	}

	@Override
	public Set<Port> getInterface() {
		Set<Port> P = new HashSet<Port>();
		for (Formula f : g)
			if(f instanceof Disjunction || f instanceof Conjunction)
				P.addAll(f.getInterface());
		return P;
	}

	@Override
	public @Nullable Formula evaluate(Scope s, Monitor m) {
		return this;
	}
	
	public String toString(){
		String s = "[" + g.get(0).toString() +"]";
		for(int i=1;i<g.size(); i++){
			s = s + "OR" + "[" + g.get(i).toString() + "]";
		}
		return s;
	}

	public List<Formula> getFormula(){
		return g;
	}
	
	@Override
	public Disjunction DNF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Formula propNegation(boolean isNegative) {
		List<Formula> h = new ArrayList<Formula>();
		for (Formula f : g)
			h.add(f.propNegation(isNegative));
		if(isNegative){
			return new Conjunction(h);
		}
		else{
			return new Disjunction(h);			
		}
	}

}
