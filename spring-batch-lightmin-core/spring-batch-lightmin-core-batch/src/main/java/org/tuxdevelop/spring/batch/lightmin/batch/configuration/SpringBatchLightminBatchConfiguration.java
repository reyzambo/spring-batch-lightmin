package org.tuxdevelop.spring.batch.lightmin.batch.configuration;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.transaction.PlatformTransactionManager;
import org.tuxdevelop.spring.batch.lightmin.batch.dao.JdbcLightminJobExecutionDao;
import org.tuxdevelop.spring.batch.lightmin.batch.dao.LightminJobExecutionDao;
import org.tuxdevelop.spring.batch.lightmin.exception.SpringBatchLightminConfigurationException;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties(value = {SpringBatchLightminBatchConfigurationProperties.class})
public class SpringBatchLightminBatchConfiguration extends DefaultBatchConfiguration {

    private final SpringBatchLightminBatchConfigurationProperties properties;
    private final DataFieldMaxValueIncrementer incrementer = new AbstractDataFieldMaxValueIncrementer() {
        @Override
        protected long getNextKey() {
            throw new IllegalStateException("JobExplorer is read only.");
        }
    };

    @Autowired
    public SpringBatchLightminBatchConfiguration(final SpringBatchLightminBatchConfigurationProperties properties,
                                                 final ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
    }
    
    @Bean
    @ConditionalOnMissingBean(value = BasicSpringBatchLightminBatchConfigurer.class)
    public BasicSpringBatchLightminBatchConfigurer batchConfigurer(final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        final BasicSpringBatchLightminBatchConfigurer batchConfigurer;
        final BatchRepositoryType batchRepositoryType = SpringBatchLightminBatchConfiguration.this.properties.getRepositoryType();
        if (Objects.requireNonNull(batchRepositoryType) == BatchRepositoryType.JDBC) {
            final DataSource dataSource = SpringBatchLightminBatchConfiguration.this.getDataSource();
            final String tablePrefix = SpringBatchLightminBatchConfiguration.this.properties.getTablePrefix();
            batchConfigurer = new BasicSpringBatchLightminBatchConfigurer(transactionManagerCustomizers.getIfAvailable(), dataSource, tablePrefix);
        } else {
            throw new SpringBatchLightminConfigurationException("Unknown BatchRepositoryType: " + batchRepositoryType);
        }
        return batchConfigurer;
    }

    @Bean
    @ConditionalOnMissingBean(value = {JobRepository.class})
    public JobRepository jobRepository(final BasicSpringBatchLightminBatchConfigurer batchConfigurer) {
        return batchConfigurer.getJobRepository();
    }

    @Bean
    SimpleAsyncTaskExecutor defaultSimpleAsyncTaskExecutor(){
        return new SimpleAsyncTaskExecutor();
    }

    /*
     * TODO: check why it is needed
     */
    @Bean(name = "defaultAsyncJobLauncher")
    public JobLauncher defaultAsyncJobLauncher(
            final JobRepository jobRepository,
            final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor
            ) {
        final TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(simpleAsyncTaskExecutor);
        return jobLauncher;
    }

    @Primary
    @Bean(name = "jobLauncher")
    public JobLauncher jobLauncher(final BasicSpringBatchLightminBatchConfigurer batchConfigurer) {
        return batchConfigurer.getJobLauncher();
    }

    @Bean
    @ConditionalOnMissingBean(value = {JobLauncher.class})
    public JobExplorer jobExplorer(final BasicSpringBatchLightminBatchConfigurer batchConfigurer) {
        return batchConfigurer.getJobExplorer();
    }

    @Bean
    public PlatformTransactionManager transactionManager(final BasicSpringBatchLightminBatchConfigurer batchConfigurer){
        return batchConfigurer.getTransactionManager();
    }

    @Bean
    public LightminJobExecutionDao lightminJobExecutionDao() throws Exception {
        final BatchRepositoryType batchRepositoryType = this.properties.getRepositoryType();
        final LightminJobExecutionDao lightminJobExecutionDao;
        if (Objects.requireNonNull(batchRepositoryType) == BatchRepositoryType.JDBC) {
            lightminJobExecutionDao = this.createLightminJobExecutionDao();
        } else {
            throw new SpringBatchLightminConfigurationException("Unknown BatchRepositoryType: " + batchRepositoryType);
        }
        return lightminJobExecutionDao;
    }

    @Bean
    @ConditionalOnMissingBean(value = {JobOperator.class})
    public JobOperator jobOperator(final JobExplorer jobExplorer,
                                   @Qualifier("jobLauncher") final JobLauncher jobLauncher,
                                   final JobRepository jobRepository,
                                   final JobRegistry jobRegistry) throws Exception {
        final SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.afterPropertiesSet();
        return jobOperator;
    }

    @Override
    @Bean
    @ConditionalOnMissingBean(value = {JobRegistry.class})
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    private LightminJobExecutionDao createLightminJobExecutionDao() throws Exception {
        final DataSource dataSource = this.getDataSource();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final JdbcLightminJobExecutionDao dao = new JdbcLightminJobExecutionDao(dataSource);
        dao.setJdbcTemplate(jdbcTemplate);
        dao.setJobExecutionIncrementer(this.incrementer);
        dao.setTablePrefix(this.properties.getTablePrefix());
        dao.afterPropertiesSet();
        return dao;
    }

    @Override
    protected DataSource getDataSource() {
        return this.applicationContext.getBean(this.properties.getDataSourceName(), DataSource.class);
    }


}
