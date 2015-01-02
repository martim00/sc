package org.scova.instrumenter;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.SourceValue;


public class SCValue extends SourceValue {
	
	
	private String name = "";

	public SCValue(int size) {
		super(size);
	}

	public SCValue(int size, AbstractInsnNode insn, String name) {
		super(size, insn);
		this.name = name;
	}

	public SCValue(int size, Set<AbstractInsnNode> insns, String name) {
		super(size, insns);
		this.name = name;
	}
	
	public SCValue(int size, String name) {
		super(size);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	

}
