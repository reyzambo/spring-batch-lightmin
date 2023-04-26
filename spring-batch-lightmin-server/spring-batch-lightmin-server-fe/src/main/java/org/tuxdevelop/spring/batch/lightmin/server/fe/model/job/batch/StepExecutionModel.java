package org.tuxdevelop.spring.batch.lightmin.server.fe.model.job.batch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.tuxdevelop.spring.batch.lightmin.server.fe.model.common.CommonExecutionModel;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StepExecutionModel extends CommonExecutionModel {

    private String stepName;
    private long readCount;
    private long writeCount;
    private long commitCount;
    private long rollbackCount;
    private long readSkipCount;
    private long processSkipCount;
    private long writeSkipCount;
}
