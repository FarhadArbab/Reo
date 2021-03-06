package nl.cwi.reo.interpret.interpreters;

import java.util.List;

import nl.cwi.reo.interpret.listeners.ListenerPR;
import nl.cwi.reo.semantics.SemanticsType;
import nl.cwi.reo.semantics.prautomata.PRAutomaton;

import nl.cwi.reo.util.Monitor;

public class InterpreterPR extends Interpreter<PRAutomaton> {
	
	/**
	 * Constructs a Reo interpreter for Port Automaton semantics.
	 * @param dirs		list of directories of Reo components
	 * @param values	parameter values of main component
	 * @param monitor	message container
	 */
	public InterpreterPR(List<String> dirs, List<String> values, Monitor monitor) {
		super(SemanticsType.PR, new ListenerPR(monitor), dirs, values, monitor);	
	}	
}
