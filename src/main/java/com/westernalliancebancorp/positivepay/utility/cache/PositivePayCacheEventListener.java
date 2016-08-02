package com.westernalliancebancorp.positivepay.utility.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * User: gduggirala
 * Date: 30/6/14
 * Time: 1:12 PM
 */
public class PositivePayCacheEventListener implements CacheEventListener {
    public static CacheEventListener positivePayCacheEventListener = new PositivePayCacheEventListener();

    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        System.out.println("Cache Removed " + cache);
        System.out.println("Element Removed " + element);
    }

    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        removeIfNull(cache, element);
    }

    private void removeIfNull(final Ehcache cache, final Element element) {
        if (element.getObjectValue() == null) {
            cache.remove(element.getObjectKey());
        }
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        System.out.println("Cache Updated " + cache);
        System.out.println("Element Updated " + element);
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
        System.out.println("Cache Expired " + cache);
        System.out.println("Element Expired " + element);
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
        System.out.println("Cache Evicted " + cache);
        System.out.println("Element Evicted " + element);
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
        System.out.println("Cache Removed all " + cache);
    }

    @Override
    public void dispose() {
        System.out.println("Disposed.");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
