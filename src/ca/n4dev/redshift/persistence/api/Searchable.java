/*
 * Searchable.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-21
 */ 
package ca.n4dev.redshift.persistence.api;

import ca.n4dev.redshift.utils.PeriodUtils.Period;
import android.database.Cursor;

public interface Searchable {
	public Cursor search(String query, Period period);
}
