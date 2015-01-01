package instrumenter.core;

import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class SCAnalyzer extends Analyzer<SCValue> {

	public SCAnalyzer(Interpreter<SCValue> interpreter) {
		super(interpreter);
	}

    @Override
    protected Frame<SCValue> newFrame(final int nLocals, final int nStack) {
        return new SCFrame(nLocals, nStack);
    }

    /**
     * Constructs a new frame that is identical to the given frame.
     * 
     * @param src
     *            a frame.
     * @return the created frame.
     */
    @Override
    protected Frame<SCValue> newFrame(final Frame<? extends SCValue> src) {
        return new SCFrame(src);
    }
}
