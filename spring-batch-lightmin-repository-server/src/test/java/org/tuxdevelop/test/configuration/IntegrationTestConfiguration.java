package org.tuxdevelop.test.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;
import org.tuxdevelop.spring.batch.lightmin.repository.configuration.JdbcJobConfigurationRepositoryConfigurationProperties;
import org.tuxdevelop.spring.batch.lightmin.repository.server.configuration.EnableLightminRepositoryServer;
import org.tuxdevelop.spring.batch.lightmin.test.util.ITJdbcJobConfigurationRepository;

import javax.sql.DataSource;

@Configuration
@EnableLightminRepositoryServer
@EnableLightminJdbcConfigurationRepository
@PropertySource(value = {"classpath:application.properties"})
public class IntegrationTestConfiguration {

    @Bean
    public ITJdbcJobConfigurationRepository itJobConfigurationRepository(
            DataSource dataSource
    ) {
        return new ITJdbcJobConfigurationRepository(
                new JdbcTemplate(dataSource),
                new JdbcJobConfigurationRepositoryConfigurationProperties()
        );
    }

}
