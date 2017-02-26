package nl.cwi.reo.seepageautomata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Set;

import nl.cwi.reo.automata.Label;
import nl.cwi.reo.semantics.api.Expression;
import nl.cwi.reo.semantics.api.Port;

public class SeepageFunction implements Label<SeepageFunction> {
	
	public final List<Map<String,Set<Set<String>>>> equations;
	
	public SeepageFunction() {
		this.equations = new ArrayList<Map<String,Set<Set<String>>>>();
	}
	
	public SeepageFunction(List<Map<String,Set<Set<String>>>> eqs) {
		this.equations = eqs;
	}
	
	public SeepageFunction(SeepageFunction S) {
		this.equations = S.equations;
	}

	@Override
	public SeepageFunction compose(List<SeepageFunction> lbls) {
		List<Map<String,Set<Set<String>>>> eqs_all = new ArrayList<Map<String,Set<Set<String>>>>();
		for (SeepageFunction F : lbls)
			eqs_all.addAll(F.equations);
		return new SeepageFunction(eqs_all);
	}

	@Override
	public SeepageFunction restrict(Collection<? extends Port> intface) {
		return new SeepageFunction(this);
	}

	@Override
	public SeepageFunction rename(Map<Port, Port> links) {
		return null;
	}

	@Override
	public SeepageFunction getDefault(Set<Port> N) {
		return null;
	}

	@Override
	public SeepageFunction evaluate(Map<String, Expression> params) {
		return null;
	}
}
