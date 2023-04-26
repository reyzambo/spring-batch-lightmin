package org.tuxdevelop.test.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;

@Configuration
@EnableLightminJdbcConfigurationRepository
@Import(value = {ITJobConfiguration.class})
public class ITEmbeddedConfiguration {


}
