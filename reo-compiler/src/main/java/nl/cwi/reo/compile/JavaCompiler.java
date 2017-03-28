package nl.cwi.reo.compile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import nl.cwi.reo.compile.components.ActiveAutomaton;
import nl.cwi.reo.compile.components.Definition;
import nl.cwi.reo.compile.components.Instance;
import nl.cwi.reo.compile.components.ReoTemplate;
import nl.cwi.reo.compile.components.TransitionRule;
import nl.cwi.reo.interpret.ReoProgram;
import nl.cwi.reo.interpret.connectors.ReoConnector;
import nl.cwi.reo.interpret.connectors.ReoConnectorAtom;
import nl.cwi.reo.interpret.connectors.ReoConnectorComposite;
import nl.cwi.reo.interpret.ports.Port;
import nl.cwi.reo.pr.autom.AutomatonFactory.AutomatonSet;
import nl.cwi.reo.semantics.AutomatonSemantics;
import nl.cwi.reo.semantics.symbolicautomata.Formula;

public class JavaCompiler {

	public static <T extends AutomatonSemantics<T>> ReoTemplate compile(ReoProgram<T> program, String packagename,
			T nodeFactory) {

		if (program == null)
			return null;

		ReoConnector<T> connector = program.getConnector().flatten().insertNodes(true, true, nodeFactory).integrate();

		List<Port> ports = new ArrayList<Port>();
		List<Instance> instances = new ArrayList<Instance>();
		List<Definition> definitions = new ArrayList<Definition>();

		int c = 0;
		int i = 0;
		for (ReoConnectorAtom<T> atom : connector.getAtoms()) {
			List<Port> atom_ports = new ArrayList<Port>(atom.getInterface());
			ports.addAll(atom_ports);
			Map<Integer, Set<TransitionRule>> out = new HashMap<Integer, Set<TransitionRule>>();
			Integer initial = 0;
			Definition defn = new ActiveAutomaton("Component" + c++, atom_ports, out, initial);
			instances.add(new Instance("Instance" + i++, defn, atom_ports));
		}

		List<ReoConnectorComposite<T>> partitionMedium = new ArrayList<ReoConnectorComposite<T>>();
		List<ReoConnector<T>> partitionBig = new ArrayList<ReoConnector<T>>();
		List<List<ReoConnectorAtom<T>>> partition = new ArrayList<List<ReoConnectorAtom<T>>>();

		/*
		 * Partitioning
		 */

		for (ReoConnectorAtom<T> atoms : connector.getAtoms()) {
			if (atoms.getSourceCode().getFile() == null) {
				partition = partitionConnector(partition, atoms);
			} else {
				partitionBig.add(atoms);
			}
		}

		for (List<ReoConnectorAtom<T>> listAtoms : partition)
			partitionMedium.add(new ReoConnectorComposite("", listAtoms));

		for (ReoConnectorComposite<T> composite : partitionMedium) {
			partitionBig.add(getProduct(composite));
		}

		return new ReoTemplate(program.getFile(), packagename, program.getName(), ports, instances, definitions);
	}

	public static <T extends AutomatonSemantics<T>> ReoConnectorComposite<T> getProduct(
			ReoConnectorComposite<T> reoConnector) {
		ReoConnectorAtom<T> productAutomaton;
		Queue<ReoConnectorAtom<T>> atoms = new LinkedList<ReoConnectorAtom<T>>(reoConnector.getAtoms());
		productAutomaton = atoms.poll();
		while (!atoms.isEmpty()) {
			productAutomaton = new ReoConnectorAtom<T>(
					productAutomaton.getSemantics().compose(Arrays.asList(atoms.poll().getSemantics())));
		}
		return new ReoConnectorComposite<T>("", Arrays.asList(productAutomaton));
	}

	public static <T extends AutomatonSemantics<T>> List<List<ReoConnectorAtom<T>>> partitionConnector(
			List<List<ReoConnectorAtom<T>>> reoConnectors, ReoConnectorAtom<T> reoConnector) {

		List<List<ReoConnectorAtom<T>>> listConnector = new ArrayList<List<ReoConnectorAtom<T>>>();
		boolean contain = false;
		listConnector.add(Arrays.asList(reoConnector));
		for (List<ReoConnectorAtom<T>> connector : reoConnectors) {
			for (ReoConnectorAtom<T> atom : connector) {
				if (reoConnector.getInterface().remove(atom.getInterface())) {
					contain = true;
					break;
				}
			}
			if (contain)
				listConnector.get(0).addAll(connector);
			else
				listConnector.add(connector);
			contain = false;
		}

		return listConnector;
	}

	public static List<Set<Port>> partitionPort(List<Set<Port>> setPorts, Set<Port> s) {
		List<Set<Port>> set = new ArrayList<Set<Port>>();
		boolean contain = false;
		set.add(s);
		for (Set<Port> setPort : setPorts) {
			for (Port p : s) {
				if (setPort.contains(p)) {
					contain = true;
					break;
				}
			}
			if (contain)
				set.get(0).addAll(setPort);
			else
				set.add(setPort);
			contain = false;
		}
		return set;
	}

	// public static List<T> partition(ReoConnector<T> connector){
	// return new ArrayList<ReoConnector<T>>();
	// }

	/**
	 * Computes a transition from a formula that consists of a conjunction of
	 * literals.
	 * 
	 * @param f
	 *            conjunction of literals
	 * @return a transition if the formula is a conjunction of literals, or null
	 *         otherwise.
	 */
	public static TransitionRule commandify(Formula f) {
		return null;
	}

}
