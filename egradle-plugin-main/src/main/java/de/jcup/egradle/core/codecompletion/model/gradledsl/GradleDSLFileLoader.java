package de.jcup.egradle.core.codecompletion.model.gradledsl;

import de.jcup.egradle.core.codecompletion.model.Type;

public interface GradleDSLFileLoader {

	Type load(String string);

}
