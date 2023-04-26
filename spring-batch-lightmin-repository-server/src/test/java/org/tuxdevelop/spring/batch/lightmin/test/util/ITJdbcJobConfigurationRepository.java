package org.tuxdevelop.spring.batch.lightmin.test.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.tuxdevelop.spring.batch.lightmin.domain.JobConfiguration;
import org.tuxdevelop.spring.batch.lightmin.exception.NoSuchJobConfigurationException;
import org.tuxdevelop.spring.batch.lightmin.repository.JdbcJobConfigurationRepository;
import org.tuxdevelop.spring.batch.lightmin.repository.configuration.JdbcJobConfigurationRepositoryConfigurationProperties;

import java.util.Collection;

import static org.junit.Assert.fail;

public class ITJdbcJobConfigurationRepository extends JdbcJobConfigurationRepository implements ITJobConfigurationRepository {

    public ITJdbcJobConfigurationRepository(JdbcTemplate jdbcTemplate, JdbcJobConfigurationRepositoryConfigurationProperties springBatchLightminConfigurationProperties) {
        super(jdbcTemplate, springBatchLightminConfigurationProperties);
    }

    @Override
    public void clean(final String applicationName) {
        final Collection<JobConfiguration> allJobConfigurations = super.getAllJobConfigurations(applicationName);
        for (final JobConfiguration jobConfiguration : allJobConfigurations) {
            try {
                super.delete(jobConfiguration, applicationName);
            } catch (final NoSuchJobConfigurationException e) {
                fail(e.getMessage());
            }
        }
    }
}
