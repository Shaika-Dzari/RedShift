/**
 * 
 */
package ca.n4dev.redshift.events;

/**
 * @author rguillemette
 *
 */
public interface UrlModificationAware {
	public void urlHasChanged(String url);
	public void pageReceived(String url, String title);
}
