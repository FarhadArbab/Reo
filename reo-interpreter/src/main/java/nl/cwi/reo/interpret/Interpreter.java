package nl.cwi.reo.interpret;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import nl.cwi.reo.semantics.Port;
import nl.cwi.reo.semantics.Semantics;
import nl.cwi.reo.semantics.SemanticsType;

public final class Interpreter<T extends Semantics<T>> {
	
	/**
	 * Type of semantics.
	 */
	private final SemanticsType semantics;
	
	/**
	 * Component paths: base directories of component files.
	 */
	private final List<String> dirs;
	
	/**
	 * Constructs a Reo interpreter.
	 * @param dirs		list of directories of Reo components
	 */
	public Interpreter(List<String> dirs, SemanticsType semantics) {
		this.semantics = semantics;
		this.dirs = Collections.unmodifiableList(dirs);	
	}

	/**
	 * Interprets a list of Reo files (the first file is the main file) as a 
	 * list of atomic components.
	 * @param file		non-empty list of Reo file names.
	 * @return list of work automata.
	 */
	public List<T> interpret(List<String> srcfiles) {
		
		List<T> system = new ArrayList<T>();
		
		// Find all available component expressions.
		Stack<ProgramFile> stack = new Stack<ProgramFile>();	
		try {
			List<String> parsed = new ArrayList<String>();
			Queue<String> components = new LinkedList<String>();
			
			for (String file : srcfiles) {
				ProgramFile program = parse(new ANTLRFileStream(file));
				if (program != null) {
					stack.push(program);
					parsed.add(program.getName());
					components.addAll(program.getImports());
				} else {
					System.out.println("ERROR: file " + file + " could not be parsed.");
				}
			}		
			
			while (!components.isEmpty()) {
				String comp = components.poll();
				if (!parsed.contains(comp)) {
					parsed.add(comp);
					ProgramFile program = parseComponent(comp);
					if (program != null) {
						stack.push(program);
						List<String> newComponents = program.getImports();
						newComponents.removeAll(parsed);
						components.addAll(newComponents);
					} else {
						System.out.println("ERROR: Component " + comp + " cannot be found.");
					}

				}
			}
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}		

		// Evaluate these component expressions.
		Map<VariableName, Expression> cexprs = new HashMap<VariableName, Expression>();
		VariableName name = null;		
		try {
			while (!stack.isEmpty()) {
				ProgramFile program = stack.pop();
				name = new VariableName(program.getName());
				Expression cexpr = program.getComponent().evaluate(cexprs);
				System.out.println("Component : " + name + " = " + cexpr + "\n");
				cexprs.put(name, cexpr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Get the instance from the main component.
		List<Instance> instances = null;
		Expression expr = cexprs.get(name);		
		if (expr instanceof ComponentValue) {
			instances = ((ComponentValue)expr).getInstances();
		} else {
			return system;
		}				
		
		if (instances.size() == 0)
			return system;
		
    	@SuppressWarnings("unchecked")
		T unit = (T) instances.get(0).getAtom();
		
		// Split shared ports in every atom in main, and insert a node
		Map<Port, List<Port>> nodes = new HashMap<Port, List<Port>>();
		
		for (Instance inst : instances) {			
			if (inst.getAtom().getType() == semantics) {	
							
				Map<String, String> r = new HashMap<String, String>();

				// For every port of this component, add the current node size as a suffix.
				for (Map.Entry<String, Port> link : inst.entrySet()) {
					Port p = link.getValue();
					
					// Get the current node of this port, or create a new node.
					List<Port> A = nodes.get(p);
					if (A == null) {
						A = new ArrayList<Port>();
						nodes.put(p, A);
					}
					
					// Rename the port by adding a suffix.
					Port portWithSuffix = p.rename(p.getName() + (A.isEmpty() ? "": A.size()));
					
					// Add the renamed port to this node.
					A.add(portWithSuffix);

					nodes.put(link.getValue(), A);
					
					// Register how to rename the ports in the semantics.
					r.put(link.getKey(), portWithSuffix.getName());
				}

		    	@SuppressWarnings("unchecked")
				T X = ((T)inst.getAtom()).rename(r);
		    	system.add(X);
			} else {
				System.out.println("ERROR: not every component is of type " + semantics);
			}
		}
		
		for (Map.Entry<Port, List<Port>> node : nodes.entrySet()) 
			if (node.getValue().size() > 1)
				system.add(unit.getNode(node.getValue()));


		return system;
	}
	
	/**
	 * Locates the source file that contains the definition of a component.
	 * @param component		fully qualified name of the requested component.
	 * @return path string of the file containing this components definition,
	 * or null, if this path is not found.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private ProgramFile parseComponent(String component) throws IOException {
		
		ProgramFile prog = null;
		
		int k = component.lastIndexOf('.') + 1;
		String name = component.substring(k);
		String directory = component.substring(0, k).replace('.', File.separatorChar);
		String cp1 = directory + semantics + File.separator + name + ".treo";
		String cp2 = directory + name + ".treo";
	
	search:
		for (String dir : dirs) {
			
			// Check if this directory contains a .zip file.
			File folder = new File(dir);
			if (folder.exists() && folder.isDirectory()) {
				
				
				FilenameFilter archiveFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".zip");
					}
				};	
				
				File[] files = folder.listFiles(archiveFilter);
				for (File file : files) {			
					if (!file.isDirectory()) {
					    ZipFile zipFile = null;
						try {
						    zipFile = new ZipFile(file.getPath());
						    ZipEntry entry1 = zipFile.getEntry(cp1);
						    ZipEntry entry2 = zipFile.getEntry(cp2);
						    if (entry1 != null) {
						    	InputStream input = zipFile.getInputStream(entry1);
						    	prog = parse(new ANTLRInputStream(input));
								break search;
						    } else if (entry2 != null) {
						    	InputStream input = zipFile.getInputStream(entry2);
						    	prog = parse(new ANTLRInputStream(input));
								break search;
						    }
						} finally {
							try { if (zipFile != null) zipFile.close(); } catch(IOException e) { }
						}
					} 
				}
			}
			
			File f1 = new File(dir + File.separator + cp1);
			if (f1.exists() && !f1.isDirectory()) {
				prog = parse(new ANTLRFileStream(dir + File.separator + cp1));
				break search;
			}

			File f2 = new File(dir + File.separator + cp2);
			if (f2.exists() && !f2.isDirectory()) {
				prog = parse(new ANTLRFileStream(dir + File.separator + cp2));
				break search;
			}
		}
		
		return prog;
	}
	
	/**
	 * Parses a source file using ANTLR4, and walks over the parse 
	 * tree to interpret this source file as a Java object. By default, 
	 * ANTLR4 sends any error found during parsing to System.err.
	 * @param c		input character stream
	 * @return an interpreted source file, or null in case of an error.
	 * @throws IOException 
	 */
	private ProgramFile parse(CharStream c) throws IOException  {
		TreoLexer lexer = new TreoLexer(c); 
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TreoParser parser = new TreoParser(tokens);
		ParseTree tree = parser.file();
		ParseTreeWalker walker = new ParseTreeWalker();
		Listener listener = new Listener();
		walker.walk(listener, tree);
		return listener.getFile();
	}
	
}
