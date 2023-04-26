package org.tuxdevelop.spring.batch.lightmin.batch.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.*;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.transaction.PlatformTransactionManager;
import org.tuxdevelop.spring.batch.lightmin.exception.SpringBatchLightminConfigurationException;

import javax.sql.DataSource;

@Slf4j
public class BasicSpringBatchLightminBatchConfigurer implements InitializingBean {

    @Getter
    private JobInstanceDao jobInstanceDao;
    @Getter
    private JobExecutionDao jobExecutionDao;
    @Getter
    private StepExecutionDao stepExecutionDao;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private PlatformTransactionManager transactionManager;

    private final TransactionManagerCustomizers transactionManagerCustomizers;

    private String tablePrefix;

    private final DataFieldMaxValueIncrementer incrementer = new AbstractDataFieldMaxValueIncrementer() {
        @Override
        protected long getNextKey() {
            throw new IllegalStateException("JobExplorer is read only.");
        }
    };

    public BasicSpringBatchLightminBatchConfigurer(final TransactionManagerCustomizers transactionManagerCustomizers){
        this.transactionManagerCustomizers = transactionManagerCustomizers;
    }

    public BasicSpringBatchLightminBatchConfigurer(final TransactionManagerCustomizers transactionManagerCustomizers,
                                                   final DataSource dataSource,
                                                   final String tablePrefix){
        this.setDataSource(dataSource);
        this.tablePrefix = tablePrefix;
        this.transactionManagerCustomizers = transactionManagerCustomizers;
    }

    public JobRepository getJobRepository() {
        return this.jobRepository;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public JobLauncher getJobLauncher()  {
        return this.jobLauncher;
    }

    public JobExplorer getJobExplorer()  {
        return this.jobExplorer;
    }

    @Override
    public void afterPropertiesSet() {
        initialize();
    }

    protected void initialize() {
        try {
            this.transactionManager = buildTransactionManager();
            if (this.dataSource != null) {
                this.createJdbcComponents();
            } else {
                throw  new SpringBatchLightminConfigurationException("Datasource is null!");
            }
            this.jobLauncher = this.createJobLauncher();
        } catch (final Exception e) {
            log.error("Error while creating DefaultSpringBatchLightminConfiguration: " + e.getMessage());
            throw new SpringBatchLightminConfigurationException(e, e.getMessage());
        }
    }

    protected void createJdbcComponents() throws Exception {

        // jobExplorer
        final JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
        jobExplorerFactoryBean.setTablePrefix(this.tablePrefix);
        jobExplorerFactoryBean.setDataSource(this.dataSource);
        jobExplorerFactoryBean.setTransactionManager(this.transactionManager);
        jobExplorerFactoryBean.afterPropertiesSet();

        this.jobExplorer = jobExplorerFactoryBean.getObject();

        // jobExecutionDao
        this.jobExecutionDao = this.createJobExecutionDao();
        // jobInstanceDao
        this.jobInstanceDao = this.createJobInstanceDao();
        // stepExecutionDao
        this.stepExecutionDao = this.createStepExecutionDao();
        // jobRepository
        this.jobRepository = this.createJobRepository();
    }

    protected JobLauncher createJobLauncher() throws Exception {
        TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
        taskExecutorJobLauncher.setJobRepository(getJobRepository());
        taskExecutorJobLauncher.afterPropertiesSet();
        return taskExecutorJobLauncher;
    }

    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        PropertyMapper map = PropertyMapper.get();
        map.from(this.dataSource).to(factory::setDataSource);
        map.from(this::determineIsolationLevel).whenNonNull().to(factory::setIsolationLevelForCreate);
        map.from(this.tablePrefix).whenHasText().to(factory::setTablePrefix);
        map.from(this::getTransactionManager).to(factory::setTransactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    /**
     * Determine the isolation level for create* operation of the {@link JobRepository}.
     * @return the isolation level or {@code null} to use the default
     */
    protected String determineIsolationLevel() {
        return null;
    }

    protected PlatformTransactionManager createTransactionManager() {
        final PlatformTransactionManager platformTransactionManager;
        if(this.dataSource != null) {
            platformTransactionManager = new DataSourceTransactionManager(this.dataSource);
        }else{
            platformTransactionManager = new ResourcelessTransactionManager();
        }
        return platformTransactionManager;
    }

    protected JobInstanceDao createJobInstanceDao() throws Exception {
        final JdbcJobInstanceDao dao = new JdbcJobInstanceDao();
        dao.setJdbcTemplate(this.jdbcTemplate);
        dao.setJobInstanceIncrementer(this.incrementer);
        dao.setTablePrefix(this.tablePrefix);
        dao.afterPropertiesSet();
        return dao;
    }

    protected JobExecutionDao createJobExecutionDao() throws Exception {
        final JdbcJobExecutionDao dao = new JdbcJobExecutionDao();
        dao.setJdbcTemplate(this.jdbcTemplate);
        dao.setJobExecutionIncrementer(this.incrementer);
        dao.setTablePrefix(this.tablePrefix);
        dao.afterPropertiesSet();
        return dao;
    }

    protected StepExecutionDao createStepExecutionDao() throws Exception {
        final JdbcStepExecutionDao dao = new JdbcStepExecutionDao();
        dao.setJdbcTemplate(this.jdbcTemplate);
        dao.setStepExecutionIncrementer(this.incrementer);
        dao.setTablePrefix(this.tablePrefix);
        dao.afterPropertiesSet();
        return dao;
    }

    private void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.transactionManager = new DataSourceTransactionManager(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private PlatformTransactionManager buildTransactionManager() {
        PlatformTransactionManager platformTransactionManager = createTransactionManager();
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(platformTransactionManager);
        }
        return platformTransactionManager;
    }
}
