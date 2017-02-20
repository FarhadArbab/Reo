package nl.cwi.reo.semantics.prautomata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.cwi.reo.interpret.Scope;
import nl.cwi.reo.interpret.connectors.Semantics;
import nl.cwi.reo.interpret.connectors.SemanticsType;
import nl.cwi.reo.interpret.ports.Port;
import nl.cwi.reo.interpret.ports.PortType;
import nl.cwi.reo.interpret.values.IntegerValue;
import nl.cwi.reo.interpret.values.StringValue;
import nl.cwi.reo.interpret.values.Value;
import nl.cwi.reo.util.Monitor;

public class PRAutomaton implements Semantics<PRAutomaton> {
		
	private String name;
	private String variable;
	private Integer value; 
	private List<Port> port;
	

	public PRAutomaton(String name, String variable, Integer value, List<Port> port){
		this.name=name;
		this.value=value;
		this.variable=variable;
		this.port=port;
		
	}
	
	public SemanticsType getType() {
		return SemanticsType.PA;
	}
		
	public String getName(){
		return name;
	}

	
	public Port getDest(){
		return port.get(1);
	}

	public String toString(){
		StringBuilder str = new StringBuilder();
	//	str.append(name + "["+ variable + "]("+getInterface()+")");
		str.append(name + "("+getInterface()+")");
		 
		return str.toString();
	}
	
	public SortedSet<Port> getInterface() {
		
		return new TreeSet<Port>(port);
	}

	public PRAutomaton getNode(SortedSet<Port> node) {
		
		List<Port> P = new ArrayList<Port>();

		int counterI=0;
		int counterO=0;
		for (Port p : node) {
			switch (p.getType()) {
			case IN:
				counterI++;
				P.add(new Port(p.getName(),PortType.OUT,p.getPrioType(),p.getTypeTag(), false));
				break;
			case OUT: 
				counterO++;
				P.add(new Port(p.getName(),PortType.IN,p.getPrioType(),p.getTypeTag(), false));
				break;
			default:
				break;
			}
		}

		if(counterI>counterO)
			return new PRAutomaton("Replicator",null,null,P);
		else
			return new PRAutomaton("Merger",null,null,P);
	}


	@Override
	public PRAutomaton rename(Map<Port, Port> links) {
		
		List<Port> P = new ArrayList<Port>();
		
		for (Port a : this.port) {
			Port b = links.get(a);
			if (b == null) b = a;
			P.add(b);//P.add(new Port(b.getName()));
		}
		
		return new PRAutomaton(name,variable,value,P);
	}

	@Override
	public PRAutomaton evaluate(Scope s, Monitor m) {
		Value v = s.get(variable);
		Integer newValue = null;
		
		if (v instanceof IntegerValue) {
			newValue = ((IntegerValue)v).getValue();
		} else if (v instanceof StringValue) {
			try {
				newValue =Integer.parseInt(((StringValue)v).getValue());
			}
			catch(NumberFormatException e){
				
			}
		}
		
		return new PRAutomaton(name,variable,newValue,port);
	}

	@Override
	public PRAutomaton compose(List<PRAutomaton> automata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PRAutomaton restrict(Collection<? extends Port> intface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PRAutomaton getNode(Set<Port> node) {
		// TODO Auto-generated method stub
		return null;
	}

}
