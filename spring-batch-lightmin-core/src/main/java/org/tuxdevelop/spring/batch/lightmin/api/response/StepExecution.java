package org.tuxdevelop.spring.batch.lightmin.api.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.item.ExecutionContext;

import lombok.Data;

@Data
public class StepExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer version;
    private Long jobExecutionId;
    private String stepName;
    private BatchStatus status;
    private int readCount;
    private int writeCount;
    private int commitCount;
    private int rollbackCount;
    private int readSkipCount;
    private int processSkipCount;
    private int writeSkipCount;
    private Date startTime;
    private Date endTime;
    private Date lastUpdated;
    private ExecutionContext executionContext;
    private ExitStatus exitStatus;
    private boolean terminateOnly;
    private int filterCount;
    private List<Throwable> failureExceptions;
}
