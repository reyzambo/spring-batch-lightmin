package org.tuxdevelop.spring.batch.lightmin.api.resource.batch;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Marcel Becker
 * @Since 0.3
 */
@Data
public class StepExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer version;
    private Long jobExecutionId;
    private String stepName;
    private BatchStatus status;
    private long readCount;
    private long writeCount;
    private long commitCount;
    private long rollbackCount;
    private long readSkipCount;
    private long processSkipCount;
    private long writeSkipCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdated;
    private ExitStatus exitStatus;
    private boolean terminateOnly;
    private long filterCount;
    private List<Throwable> failureExceptions;
}
