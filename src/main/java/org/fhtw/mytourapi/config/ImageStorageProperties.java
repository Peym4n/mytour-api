package org.fhtw.mytourapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@ConfigurationProperties(prefix = "storage.images")
public class ImageStorageProperties {

    private Path baseDirectory = Path.of(System.getProperty("java.io.tmpdir"), "mytour-cover-images");
    private long maxSizeBytes = 5L * 1024L * 1024L;
    private List<String> allowedContentTypes = List.of("image/jpeg", "image/png", "image/webp");

    public Path getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public long getMaxSizeBytes() {
        return maxSizeBytes;
    }

    public void setMaxSizeBytes(long maxSizeBytes) {
        this.maxSizeBytes = maxSizeBytes;
    }

    public List<String> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    public void setAllowedContentTypes(List<String> allowedContentTypes) {
        this.allowedContentTypes = allowedContentTypes;
    }
}
