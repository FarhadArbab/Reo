package nl.cwi.reo.semantics;

/**
 * Enumerates all implemented semantics for Reo.
 */
public enum SemanticsType {
	
	/**
	 * Symbolic Automata.
	 */
	SBA, 
	
	/**
	 * Port automata.
	 */
	PA, 
	
	/**
	 * Constraint automata with memory.
	 */
	CAM, 
	
	/**
	 * Work automata.
	 */
	WA, 
	
	/**
	 * Seepage automata.
	 */
	SA, 
	
	/**
	 * Atomic components for Lykos compiler by Sung-Shik Jongmans.
	 */
	PR,
	
	/**
	 * Plain semantics.
	 */
	PLAIN;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		switch(this) {
		case PA: return "pa";
		case PR: return "pr";
		case CAM: return "cam";
		case WA: return "wa";
		case SA: return "sa";
		case SBA: return "sba";
		case PLAIN: return "plain";
		default: throw new IllegalArgumentException();
		}
	}

}
