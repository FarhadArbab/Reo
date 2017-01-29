package nl.cwi.reo.constraintautomata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import nl.cwi.reo.automata.Automaton;
import nl.cwi.reo.automata.State;
import nl.cwi.reo.automata.Transition;
import nl.cwi.reo.semantics.api.Expression;
import nl.cwi.reo.semantics.api.Port;
import nl.cwi.reo.semantics.api.Semantics;
import nl.cwi.reo.semantics.api.SemanticsType;

public class ConstraintAutomaton extends Automaton<CAMLabel> implements Semantics<ConstraintAutomaton> {
	
	private static CAMLabel label = new CAMLabel();
	
	public ConstraintAutomaton() {
		super(label);
	}
	
	public ConstraintAutomaton(SortedSet<State> Q, SortedSet<Port> P, Map<State, Set<Transition<CAMLabel>>> T, State q0) {
		super(Q, P, T, q0, label);
	}
	
	private ConstraintAutomaton(Automaton<CAMLabel> A) {
		super(A.getStates(), A.getInterface(), A.getTransitions(), A.getInitial(), label);
	}

	@Override
	public SemanticsType getType() {
		return SemanticsType.PA;
	}

	@Override
	public ConstraintAutomaton getNode(SortedSet<Port> node) {
		return new ConstraintAutomaton(super.getNode(node));
	}

	@Override
	public ConstraintAutomaton rename(Map<Port, Port> links) {
		return new ConstraintAutomaton(super.rename(links));
	}

	@Override
	public ConstraintAutomaton evaluate(Map<String, Expression> params) {
		return new ConstraintAutomaton(super.evaluate(params));
	}

	@Override
	public ConstraintAutomaton compose(List<ConstraintAutomaton> automata) {
		return new ConstraintAutomaton(super.compose(automata));
	}

	@Override
	public ConstraintAutomaton restrict(Collection<? extends Port> intface) {
		return new ConstraintAutomaton(super.restrict(intface));
	}
}
