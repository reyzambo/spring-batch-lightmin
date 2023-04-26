package org.tuxdevelop.spring.batch.lightmin.api.resource.batch;

import lombok.Data;
import org.tuxdevelop.spring.batch.lightmin.api.resource.common.JobParameters;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Marcel Becker
 * @Since 0.3
 */
@Data
public class JobExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer version;
    private JobParameters jobParameters;
    private JobInstance jobInstance;
    private List<StepExecution> stepExecutions;
    private BatchStatus status;
    private LocalDateTime startTime;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdated;
    private ExitStatus exitStatus;
    private List<Throwable> failureExceptions;
    private String jobConfigurationName;
}
