package org.tuxdevelop.test.configuration.remote;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tuxdevelop.spring.batch.lightmin.repository.RemoteJobConfigurationRepositoryLocator;
import org.tuxdevelop.spring.batch.lightmin.repository.UrlRemoteJobConfigurationRepositoryLocator;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;
import org.tuxdevelop.spring.batch.lightmin.repository.configuration.JdbcJobConfigurationRepositoryConfigurationProperties;
import org.tuxdevelop.spring.batch.lightmin.repository.configuration.RemoteJobConfigurationRepositoryConfigurationProperties;
import org.tuxdevelop.spring.batch.lightmin.repository.server.configuration.EnableLightminRepositoryServer;
import org.tuxdevelop.spring.batch.lightmin.test.util.ITJdbcJobConfigurationRepository;
import org.tuxdevelop.spring.batch.lightmin.test.util.ITJobConfigurationRepository;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
@EnableLightminJdbcConfigurationRepository
@EnableLightminRepositoryServer
@EnableConfigurationProperties(value = {RemoteJobConfigurationRepositoryConfigurationProperties.class})
public class RemoteIntegrationTestConfiguration {

    @Bean
    public ITJobConfigurationRepository itJobConfigurationRepository(
            DataSource dataSource
    ) {
        return new ITJdbcJobConfigurationRepository(
                new JdbcTemplate(dataSource),
                new JdbcJobConfigurationRepositoryConfigurationProperties()
        );
    }

    @Bean
    public RemoteJobConfigurationRepositoryLocator remoteJobConfigurationRepositoryLocator(
            final RemoteJobConfigurationRepositoryConfigurationProperties properties) {
        return new UrlRemoteJobConfigurationRepositoryLocator(properties);
    }

}
