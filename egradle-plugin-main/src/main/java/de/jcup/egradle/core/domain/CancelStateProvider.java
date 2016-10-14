package de.jcup.egradle.core.domain;

/**
 * A state provider for information about an action is canceled or not
 * @author Albert Tregnaghi
 *
 */
public interface CancelStateProvider {
	public static final NeverCanceled NEVER_CANCELED = new NeverCanceled();
	
	public boolean isCanceled();
	
	public static class NeverCanceled implements CancelStateProvider{

		private NeverCanceled() {
		}
		
		@Override
		public boolean isCanceled() {
			return false;
		}
		
	}
	
}
