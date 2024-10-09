package svkreml.infinispanexample.config.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.CacheType;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.spring.starter.remote.InfinispanRemoteConfigurationProperties;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Configuration
public class CacheConfig {
/*        @Bean
        CacheManager cacheManager() {
            CaffeineCacheManager cacheManager = new CaffeineCacheManager();
            cacheManager.setCacheSpecification(...);
            cacheManager.setAsyncCacheMode(true);
            return cacheManager;
        }*/
    private final RemoteCacheManager sessionCache;
    @Autowired
    private ApplicationContext context;

    @Bean
    @Primary
    public TtlSpringRemoteCacheManager springRemoteCacheManager(RemoteCacheManager remoteCacheManager) {
        InfinispanRemoteConfigurationProperties infinispanProperties = context.getBean(InfinispanRemoteConfigurationProperties.class);
        long readTimeout =  infinispanProperties == null ? 0L : infinispanProperties.getReadTimeout();
        long writeTimeout = infinispanProperties == null ? 0L : infinispanProperties.getWriteTimeout();
        boolean reactive = infinispanProperties == null ? false : infinispanProperties.isReactive();
        return new TtlSpringRemoteCacheManager(remoteCacheManager, reactive, readTimeout, writeTimeout, 5000);
    }

    @PostConstruct
    public void init() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.clustering().cacheMode(CacheMode.DIST_SYNC).cacheType(CacheType.DISTRIBUTION).encoding().key().mediaType(MediaType.APPLICATION_SERIALIZED_OBJECT_TYPE).encoding().value().mediaType(MediaType.APPLICATION_SERIALIZED_OBJECT_TYPE);
        sessionCache.administration().getOrCreateCache("example", builder.build());

        createCachesForAnnotatedMethods(builder);

    }

    private void createCachesForAnnotatedMethods(ConfigurationBuilder builder) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        Set<BeanDefinition> beanDefs = provider.findCandidateComponents("svkreml");
        Set<String> annotatedBeans = new HashSet<>();
        for (BeanDefinition bd : beanDefs) {
            if (bd instanceof GenericBeanDefinition) {
                for (MethodMetadata methodMetadata : ((AnnotatedBeanDefinition) bd).getMetadata().getDeclaredMethods()) {
                    final Map<String, Object> annotationAttributes = methodMetadata.getAnnotationAttributes(Cacheable.class.getName());
                    if (annotationAttributes != null) {
                        final String[] cacheNames = (String[]) annotationAttributes.get("cacheNames");
                        if (cacheNames != null) {
                            Collections.addAll(annotatedBeans, cacheNames);
                        }
                    }
                }
            }
        }
        for (String annotatedBean : annotatedBeans) {
            final RemoteCache<Object, Object> cache = sessionCache.administration().getOrCreateCache(annotatedBean, builder.build());
        }
    }
}
