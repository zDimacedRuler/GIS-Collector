package com.disarm.surakshit.collectgis.SoftTfidf;

import com.disarm.surakshit.collectgis.api.StringWrapper;
import com.disarm.surakshit.collectgis.api.StringWrapperIterator;
import java.util.Iterator;

/** A simple StringWrapperIterator implementation. 
 */

public class BasicStringWrapperIterator implements StringWrapperIterator {
	private Iterator myIterator;
	public BasicStringWrapperIterator(Iterator i) { myIterator=i; }
	public boolean hasNext() { return myIterator.hasNext(); }
	public Object next() { return myIterator.next(); }
	public StringWrapper nextStringWrapper() { return (StringWrapper)next(); }
	public void remove() { myIterator.remove(); }
}
