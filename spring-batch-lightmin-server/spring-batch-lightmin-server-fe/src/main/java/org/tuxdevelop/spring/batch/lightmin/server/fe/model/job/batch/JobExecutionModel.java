package org.tuxdevelop.spring.batch.lightmin.server.fe.model.job.batch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.tuxdevelop.spring.batch.lightmin.server.fe.model.common.CommonExecutionModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JobExecutionModel extends CommonExecutionModel {

    private Long instanceId;
    private LocalDateTime createTime;

}
