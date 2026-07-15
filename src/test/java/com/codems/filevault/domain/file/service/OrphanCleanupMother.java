package com.codems.filevault.domain.file.service;

import com.codems.filevault.common.config.properties.FileCleanupProperties;
import java.time.Duration;
import java.util.Set;

final class OrphanCleanupMother {

    static final String ORPHAN_PNG   = "orphan.png";
    static final String ORPHAN_PDF   = "orphan-report.pdf";
    static final String ACTIVE_JPG   = "active-photo.jpg";
    static final String SOFT_DELETED = "soft-deleted-doc.pdf";

    private OrphanCleanupMother() {
    }

    static FileCleanupProperties defaultProperties() {
        return withGracePeriod(Duration.ofHours(24));
    }

    static FileCleanupProperties withGracePeriod(Duration gracePeriod) {
        FileCleanupProperties properties = new FileCleanupProperties();
        properties.setOrphanGracePeriod(gracePeriod);
        return properties;
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
