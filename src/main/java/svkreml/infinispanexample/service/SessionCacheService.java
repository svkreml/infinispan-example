package svkreml.infinispanexample.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.CacheType;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.springframework.stereotype.Service;
import svkreml.infinispanexample.dto.ExampleDto;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionCacheService {

    private final RemoteCacheManager sessionCache;

    public ExampleDto getExample(Integer i) {

        return examplaCache().get(i);
    }


    public void putExample(Integer i, ExampleDto exampleDto) {
        examplaCache().put(i, exampleDto, 24, TimeUnit.HOURS, 6, TimeUnit.HOURS);
    }

    public void removeExample(Integer i) {
        examplaCache().remove(i);
    }


    private RemoteCache<Integer, ExampleDto> examplaCache() {
        return sessionCache.getCache("example");
    }


    @PostConstruct
    public void init() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.clustering().cacheMode(CacheMode.DIST_SYNC).cacheType(CacheType.DISTRIBUTION).encoding().key().mediaType(MediaType.APPLICATION_SERIALIZED_OBJECT_TYPE).encoding().value().mediaType(MediaType.APPLICATION_SERIALIZED_OBJECT_TYPE);

        sessionCache.administration().getOrCreateCache("token", builder.build());
    }

}
