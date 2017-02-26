package nl.cwi.reo.compile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import nl.cwi.reo.interpret.semantics.FlatAssembly;
import nl.cwi.reo.portautomata.PortAutomaton;

public class JavaCompiler {
	
	public void compile(FlatAssembly<PortAutomaton> program) {
		
		STGroup group = new STGroupFile("resources/Java.stg", '$', '$');
        ST component = group.getInstanceOf("component");

        component.add("name", "MyComponent");
        
        component.add("originalfile", "MyReoApp");
        
        List<Map<String, String>> ports = new ArrayList<Map<String, String>>();
        Map<String, String> port = new HashMap<String, String>();
        port.put("name", "a");
        port.put("type", "String");
        port.put("inpt", "");
        ports.add(port);
        Map<String, String> port2 = new HashMap<String, String>();
        port2.put("name", "b");
        port2.put("type", "String");
        ports.add(port2);
        
        component.add("ports", ports);
        
        System.out.println(component.render());
		//outputClass(name, st.render());		
	}
	
//	/**
//	 * Produces java source file containing the game graph;
//	 * @param name			name of the java class
//	 * @param code			implementation of the java class
//	 * @return <code>true</code> if the file is successfully written.
//	 */
//	private static boolean outputClass(String name, String code) {
//		try {
//			FileWriter out = new FileWriter(name + ".java");
//			out.write(code);
//			out.close();
//		} catch (IOException e) {
//			return false;
//		}
//		return true;
//	}
}
