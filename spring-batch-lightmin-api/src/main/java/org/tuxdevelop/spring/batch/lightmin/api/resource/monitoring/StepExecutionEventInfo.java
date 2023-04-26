package org.tuxdevelop.spring.batch.lightmin.api.resource.monitoring;

import lombok.Data;
import org.tuxdevelop.spring.batch.lightmin.api.resource.batch.ExitStatus;

@Data
public class StepExecutionEventInfo {

    private String applicationName;

    private String jobName;
    private String stepName;
    private ExitStatus exitStatus;

    private long readCount;
    private long writeCount;
    private long commitCount;
    private long rollbackCount;
    private long readSkipCount;
    private long processSkipCount;
    private long writeSkipCount;
}
