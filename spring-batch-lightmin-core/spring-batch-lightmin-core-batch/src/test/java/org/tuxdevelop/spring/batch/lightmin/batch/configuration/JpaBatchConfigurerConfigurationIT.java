package org.tuxdevelop.spring.batch.lightmin.batch.configuration;

import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.tuxdevelop.test.configuration.ITJpaConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ITJpaConfiguration.class})
public class JpaBatchConfigurerConfigurationIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testBatchConfigurer() {
        final BasicSpringBatchLightminBatchConfigurer batchConfigurer = this.applicationContext.getBean(BasicSpringBatchLightminBatchConfigurer.class);
        BDDAssertions.then(batchConfigurer instanceof JpaSpringBatchLightminBatchConfigurer).isTrue();
    }

}
