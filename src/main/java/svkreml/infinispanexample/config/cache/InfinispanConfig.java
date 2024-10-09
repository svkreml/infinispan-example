package svkreml.infinispanexample.config.cache;

import lombok.Getter;
import lombok.Setter;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.ServerConfigurationBuilder;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.spring.starter.remote.InfinispanRemoteConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties(prefix = "infinispan")
@Setter
@Getter
public class InfinispanConfig {

    private String username;
    private String password;
    private List<String> hosts;

    @Bean
    public InfinispanRemoteConfigurer infinispanRemoteConfigurer() {
        final ServerConfigurationBuilder serverConfigurationBuilder = new ConfigurationBuilder()
                .marshaller(new JavaSerializationMarshaller())
                .addJavaSerialAllowList("java.util.List", "java.util.ArrayList", "svkreml.infinispanexample.dto.*")
                .security()
                .authentication()
                .username(username)
                .password(password)
                .enable()
                .addServer();
        for (String host : hosts) {
            final String[] split = host.split(":");
            serverConfigurationBuilder
                    .host(split[0])
                    .port(Integer.parseInt(split[1]));
        }
        return serverConfigurationBuilder::build;
    }
}
