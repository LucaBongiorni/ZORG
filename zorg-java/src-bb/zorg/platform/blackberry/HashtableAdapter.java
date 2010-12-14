/**
 * ZRTP.org is a ZRTP protocol implementation  
 * Copyright (C) 2010 - PrivateWave Italia S.p.A.
 * 
 * This  program  is free software:  you can  redistribute it and/or
 * modify  it  under  the terms  of  the  GNU Affero  General Public
 * License  as  published  by the  Free Software Foundation,  either 
 * version 3 of the License,  or (at your option) any later version.
 * 
 * This program is  distributed in  the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;  without even  the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * Affero General Public License for more details.
 * 
 * You should have received a copy of the  GNU Affero General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please contact PrivateWave Italia S.p.A. at
 * address zorg@privatewave.com or http://www.privatewave.com 
 */
package zorg.platform.blackberry;

import java.util.Enumeration;
import java.util.Hashtable;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import com.privategsm.main.variants.VariantManager;

import zorg.ZRTPCache;
import zorg.ZrtpCacheEntry;
import zorg.platform.PersistentHashtable;

public class HashtableAdapter implements PersistentHashtable {

    private static final long ZRTP_CACHE_KEY = VariantManager.getVariant().getZrtpCacheKey();
    
	private Hashtable cache = null;
	private PersistentObject persistent = null;
	
	public HashtableAdapter() {
        cache = null;
		 persistent = PersistentStore.getPersistentObject(ZRTP_CACHE_KEY);
        try {
            cache = (Hashtable)persistent.getContents();
        } catch (ClassCastException ex) {
            // we'll overwrite the original cache type with new implementation below
        }
        if (cache != null) {
            // Could have a previous version of cache stored (i.e. saved as single String rather than CacheEntry)
            // If so, recreate a new one
            try {
            	ZrtpCacheEntry ce = (ZrtpCacheEntry)cache.get(ZRTPCache.LOCAL_ZID_KEY);
            } catch (Exception ccex) {
                cache.clear();
                cache = null;
            }
        }
        if (cache == null) {
            cache = VariantManager.getVariant().newZtrpHashTable();
            persistent.setContents(cache);
            persistent.commit();
        }

    }

	public ZrtpCacheEntry get(String key) {
	    return (ZrtpCacheEntry) cache.get(key);
    }

	public Enumeration keys() {
	    return cache.keys();
    }

	public void put(String zid, byte[] data, String phoneNumber) {
		ZrtpCacheEntry ce = VariantManager.getVariant().newZrtpCacheEntry(data, phoneNumber);
		cache.put(zid, ce);
		persistent.commit();
    }

	public void remove(String currentZid) {
		cache.remove(currentZid);
		persistent.commit();
    }

	public void reset() {
		PersistentStore.destroyPersistentObject(ZRTP_CACHE_KEY);
    }

}
