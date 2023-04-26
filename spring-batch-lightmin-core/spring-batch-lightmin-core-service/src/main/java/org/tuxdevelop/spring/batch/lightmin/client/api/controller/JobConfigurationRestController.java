package org.tuxdevelop.spring.batch.lightmin.client.api.controller;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.tuxdevelop.spring.batch.lightmin.api.resource.admin.JobConfiguration;
import org.tuxdevelop.spring.batch.lightmin.api.resource.admin.JobConfigurations;
import org.tuxdevelop.spring.batch.lightmin.service.ServiceEntry;

import jakarta.validation.Valid;

/**
 * @author Marcel Becker
 * @version 0.1
 */
@Controller
@ResponseBody
@RequestMapping("/")
public class JobConfigurationRestController extends AbstractRestController implements InitializingBean {

    private final ServiceEntry serviceEntry;
    private final JobRegistry jobRegistry;

    public JobConfigurationRestController(final ServiceEntry serviceEntry, final JobRegistry jobRegistry) {
        this.serviceEntry = serviceEntry;
        this.jobRegistry = jobRegistry;
    }


    /**
     * Retrieves the {@link JobConfigurations} for all known Spring Batch Jobs
     *
     * @return all JobConfigurations
     */
    @GetMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATIONS, produces = PRODUCES)
    public ResponseEntity<JobConfigurations> getJobConfigurations() {
        final JobConfigurations jobConfigurations = this.serviceEntry.getJobConfigurations(this.jobRegistry.getJobNames());
        return ResponseEntity.ok(jobConfigurations);
    }


    /**
     * Retrieves the {@link JobConfigurations} of a given Spring Batch Job name.
     *
     * @param jobName the name of the Spring Batch job
     * @return the JobConfigurations of the Spring Batch Job
     */
    @GetMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATIONS_JOB_NAME, produces = PRODUCES)
    public ResponseEntity<JobConfigurations> getJobConfigurationsByJobName(
            @PathVariable("jobname") final String jobName) {
        @Valid final JobConfigurations jobConfigurations = this.serviceEntry.getJobConfigurationsByJobName(jobName);
        return ResponseEntity.ok(jobConfigurations);
    }

    /**
     * Retrieves a {@link JobConfigurations} for a given jobConfigurationId
     *
     * @param jobConfigurationId the id of the jobConfiguration
     * @return the JobConfiguration
     */
    @GetMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATION_JOB_CONFIGURATION_ID, produces = PRODUCES)
    public ResponseEntity<JobConfiguration> getJobConfigurationById(
            @PathVariable("jobconfigurationid") final Long jobConfigurationId) {
        final JobConfiguration jobConfiguration = this.serviceEntry.getJobConfigurationById(jobConfigurationId);
        return ResponseEntity.ok(jobConfiguration);
    }

    /**
     * Adds a new {@link JobConfiguration}
     *
     * @param jobConfiguration the JobConfiguration to add
     * @return HTTP Status Code 201
     */
    @PostMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATIONS, consumes = CONSUMES)
    public ResponseEntity<Void> addJobConfiguration(@Valid @RequestBody final JobConfiguration jobConfiguration) {
        this.serviceEntry.saveJobConfiguration(jobConfiguration);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Updates a given {@link JobConfiguration}
     *
     * @param jobConfiguration the JobConfiguration to update
     * @return HTTP Status Code 200
     */
    @PutMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATIONS, consumes = CONSUMES)
    public ResponseEntity<Void> updateJobConfiguration(@RequestBody final JobConfiguration jobConfiguration) {
        this.serviceEntry.updateJobConfiguration(jobConfiguration);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Deletes a {@link JobConfiguration} for a given id
     *
     * @param jobConfigurationId the id of the JobConfiguration
     * @return HTTP Status Code 200
     */
    @DeleteMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATION_JOB_CONFIGURATION_ID)
    public ResponseEntity<Void> deleteJobConfigurationById(
            @PathVariable("jobconfigurationid") final Long jobConfigurationId) {
        this.serviceEntry.deleteJobConfiguration(jobConfigurationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Starts a {@link JobConfiguration} for a given id
     *
     * @param jobConfigurationId the id of the JobConfiguration
     * @return HTTP Status Code 200
     */
    @GetMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATION_START)
    public ResponseEntity<Void> startJobConfiguration(
            @PathVariable("jobconfigurationid") final Long jobConfigurationId) {
        this.serviceEntry.startJobConfiguration(jobConfigurationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Stops a {@link JobConfiguration} for a given id
     *
     * @param jobConfigurationId the id of the JobConfiguration
     * @return HTTP Status Code 200
     */
    @GetMapping(value = JobConfigurationRestControllerAPI.JOB_CONFIGURATION_STOP)
    public ResponseEntity<Void> stopJobConfiguration(
            @PathVariable("jobconfigurationid") final Long jobConfigurationId) {
        this.serviceEntry.stopJobConfiguration(jobConfigurationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assert this.serviceEntry != null;
        assert this.jobRegistry != null;
    }
}
