package de.jcup.egradle.core.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class NeverCanceledTest {

	@Test
	public void never_canceld_returns_false_for_canceled() {
		assertFalse(CancelStateProvider.NEVER_CANCELED.isCanceled());
	}

}
