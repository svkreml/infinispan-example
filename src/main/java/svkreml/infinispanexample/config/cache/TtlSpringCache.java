package svkreml.infinispanexample.config.cache;

import org.infinispan.commons.api.BasicCache;
import org.infinispan.spring.common.provider.SpringCache;
import org.springframework.cache.Cache;

import java.util.concurrent.TimeUnit;

public class TtlSpringCache extends SpringCache {

    private final long ttl;

    public TtlSpringCache(BasicCache nativeCache, boolean reactive, long readTimeout, long writeTimeout, long ttl) {
        super(nativeCache, reactive, readTimeout, writeTimeout);
        this.ttl = ttl;
    }

    /**
     * @see Cache#put(Object, Object)
     */
    @Override
    public void put(final Object key, final Object value) {
        super.put(key, value, ttl, TimeUnit.MILLISECONDS);
    }

}
