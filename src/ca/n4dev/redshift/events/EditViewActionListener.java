package ca.n4dev.redshift.events;

import ca.n4dev.redshift.history.UrlUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

public class EditViewActionListener implements TextView.OnEditorActionListener {
	
	private UrlModificationAware urlAware = null;
	
	@Override
	public boolean onEditorAction(TextView textview, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			String url = textview.getText().toString();
			url = UrlUtils.sanitize(url);
			urlAware.urlHasChanged(url);
			return true;
		}
		
		return false;
	}
	
	
	public void setUrlModificationAware(UrlModificationAware urlAware) {
		this.urlAware = urlAware;
	}
}
