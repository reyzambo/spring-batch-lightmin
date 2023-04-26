package org.tuxdevelop.spring.batch.lightmin.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.tuxdevelop.spring.batch.lightmin.validation.annotation.PathExists;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JobListenerConfiguration extends AbstractConfiguration {

    private JobListenerType jobListenerType;
    @PathExists
    private String sourceFolder;
    private String filePattern;
    private Long pollerPeriod;
    private String beanName;
    private ListenerStatus listenerStatus;
    private TaskExecutorType taskExecutorType;

    public void validate() {
        if (this.jobListenerType == null) {
            this.throwExceptionAndLogError("jobListenerType must not be null");
        }
        if (this.pollerPeriod == null) {
            this.throwExceptionAndLogError("pollerPeriod must not be null");
        }
        if (JobListenerType.LOCAL_FOLDER_LISTENER.equals(this.jobListenerType)) {
            this.validateLocalFolderListener();
        }
    }

    private void validateLocalFolderListener() {
        if (StringUtils.isEmpty(this.sourceFolder)) {
            this.throwExceptionAndLogError("sourceFolder must not be null or empty");
        }
        if (StringUtils.isEmpty(this.filePattern)) {
            this.throwExceptionAndLogError("filePattern must not be null or empty");
        }
    }
}
