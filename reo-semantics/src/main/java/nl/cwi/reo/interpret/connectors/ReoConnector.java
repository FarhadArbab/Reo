package nl.cwi.reo.interpret.connectors;

import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.cwi.reo.interpret.Expression;
import nl.cwi.reo.interpret.ports.Port;
import nl.cwi.reo.semantics.Semantics;

/**
 * A SubComponent is a part of a Connector.
 * 
 * A SubComponent is an immutable object.
 * 
 * @param <T>
 *            type of semantics objects
 * @see ReoConnectorComposite
 */
public interface ReoConnector<T extends Semantics<T>> extends Expression<ReoConnector<T>> {

	/**
	 * Checks if this connector is empty.
	 * 
	 * @return true if this connector is a composite Reo consisting of an empty
	 *         list of subconnectors, and false otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Gets the links from internal ports to external ports.
	 * 
	 * @return map assigning an internal port to an external port.
	 */
	public Map<Port, Port> getLinks();

	/**
	 * Relabels the set of links of this connector by renaming all link targets
	 * names according to renaming map, and hiding all ports that are not
	 * renamed.
	 * 
	 * @param r
	 *            renaming map
	 * @return a copy of this block with reconnected links
	 */
	public ReoConnector<T> rename(Map<Port, Port> r);

	/**
	 * Inserts, if necessary, a merger and/or replicator at every node in this
	 * instance list. This method uses an instance of a semantics object to
	 * create nodes of the correct type.
	 * 
	 * @param mergers
	 *            insert mergers
	 * @param replicators
	 *            insert replicators
	 * @param nodeFactory
	 *            instance of semantics object
	 */
	public ReoConnector<T> insertNodes(boolean mergers, boolean replicators, T nodeFactory);

	/**
	 * Flattens the nested block structure of this connector, and erases any
	 * used-defined composition operator. This operation is particularly useful
	 * if this connector uses only a single associative product operator.
	 * 
	 * @return connector wherein every sub-connector is atomic.
	 */
	public ReoConnector<T> flatten();

	/**
	 * Integrates the links of this connector by renaming the interfaces of the
	 * semantic objects in this connector.
	 * 
	 * @return connector wherein every atomic component has links that map port
	 *         x to port x.
	 */
	public ReoConnector<T> integrate();

	/**
	 * Partition this connector into a list of ReoConnector with independent interfaces.
	 *  
	 * @return List of ReoConnector<T> where interfaces do not intersect.
	 * 
	 */
	public List<ReoConnector<T>> partition();

	
	/**
	 * Gets the interface of this connector.
	 * 
	 * @return set of ports in the interface of this connector
	 */
	public Set<Port> getInterface();

	/**
	 * Gets all atomic sub-connectors in this connector.
	 * 
	 * @return all atomic sub-connectors in this connector
	 */
	public List<ReoConnectorAtom<T>> getAtoms();

}
