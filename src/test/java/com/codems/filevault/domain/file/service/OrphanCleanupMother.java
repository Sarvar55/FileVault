package com.codems.filevault.domain.file.service;

import com.codems.filevault.common.config.properties.AppFileProperties;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

final class OrphanCleanupMother {

    static final String ORPHAN_PNG   = "orphan.png";
    static final String ORPHAN_PDF   = "orphan-report.pdf";
    static final String ACTIVE_JPG   = "active-photo.jpg";
    static final String SOFT_DELETED = "soft-deleted-doc.pdf";

    private OrphanCleanupMother() {
    }

    static AppFileProperties defaultProperties() {
        return withGracePeriod(Duration.ofHours(24));
    }

    static AppFileProperties withGracePeriod(Duration gracePeriod) {
        return new AppFileProperties(
                Map.of("pdf", "application/pdf"),
                new AppFileProperties.Storage("./storage/test"),
                new AppFileProperties.Upload(org.springframework.util.unit.DataSize.ofMegabytes(10)),
                new AppFileProperties.Cleanup(true, Duration.ofHours(1), gracePeriod)
        );
    }

    static Set<String> orphanFilenames() {
        return Set.of(ORPHAN_PNG, ORPHAN_PDF);
    }

    static Set<String> activeFilenames() {
        return Set.of(ACTIVE_JPG);
    }

    static Set<String> softDeletedFilenames() {
        return Set.of(SOFT_DELETED);
    }
}
