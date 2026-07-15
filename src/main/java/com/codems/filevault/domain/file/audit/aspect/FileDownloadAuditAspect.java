package com.codems.filevault.domain.file.audit.aspect;

import com.codems.filevault.domain.file.audit.annotation.DownloadAudited;
import com.codems.filevault.domain.file.audit.service.FileDownloadAuditService;
import com.codems.filevault.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class FileDownloadAuditAspect {

    private final FileDownloadAuditService fileDownloadAuditService;

    @AfterReturning(
            pointcut = "@annotation(downloadAudited) && args(user, fileId)",
            argNames = "downloadAudited,user,fileId"
    )
    public void recordDownload(DownloadAudited downloadAudited, User user, Long fileId) {
        fileDownloadAuditService.record(user, fileId);
    }
}
