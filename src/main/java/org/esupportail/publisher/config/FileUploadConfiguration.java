package org.esupportail.publisher.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.esupportail.publisher.web.ViewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Configuration
@Slf4j
public class FileUploadConfiguration {

    @Autowired
    private Environment env;

    private static final String ENV_UPLOAD_PATH = "app.upload.path";
    private static final String ENV_UPLOAD_PROTECTED_PATH = "app.upload.protectedPath";
    private static final String ENV_UPLOAD_IMAGESIZE = "app.upload.imageMaxSize";
    private static final String ENV_UPLOAD_FILESIZE = "app.upload.maxFileSize";
    private static final String ENV_UPLOAD_FILE_AUTHORIZED_MIME_TYPE = "app.upload.filePathOfAuthorizedMimeTypes";

    private static final long defaultImageMaxSize = 131072;
    private static final long defaultFileMaxSize = 10485760;
    private static final String defaultFileMimeTypes = "MimeTypes.txt";

    @Bean(name = "publicFileUploadHelper")
    public FileUploadHelper publicFileUploadHelper() throws IOException, URISyntaxException {
        FileUploadHelper fuh = new FileUploadHelper();
        final String path = getDefinedUploadPath(ENV_UPLOAD_PATH);
        Assert.notNull(path, "You should define a protected file path");

        fuh.setUploadDirectoryPath(path);
        fuh.setResourceLocation("file:///" + path);
        fuh.setUrlResourceMapping("files/");
        fuh.setUseDefaultPath(false);
        fuh.setFileMaxSize(env.getProperty(ENV_UPLOAD_IMAGESIZE, long.class, defaultImageMaxSize));
        final String mimeTypesFilePath = env.getProperty(ENV_UPLOAD_FILE_AUTHORIZED_MIME_TYPE, defaultFileMimeTypes);
        try {
            List<String> list = Files.readAllLines(Paths.get(new ClassPathResource(mimeTypesFilePath).getURI()), Charsets.UTF_8);
            fuh.setAuthorizedMimeType(list);
        } catch (IOException e) {
            log.error("No file describing authorized MimeTypes is readable !", e);
            throw e;
        }
        log.debug("FileUploadConfiguration : " + fuh.toString());
        return fuh;
        /* Default way
        fuh.setUploadDirectoryPath(new ClassPathResource("classpath:static/").getPath());
        fuh.setResourceLocation("classpath:static/");
        fuh.setUrlResourceMapping("files/");
        fuh.setUseDefaultPath(true);
        fuh.setFileMaxSize(env.getProperty(ENV_UPLOAD_IMAGESIZE, long.class, DefaultImageMaxSize));
        log.debug("FileUploadConfiguration : " + fuh.toString());
        return fuh;*/
    }

    @Bean(name = "protectedFileUploadHelper")
    public FileUploadHelper protectedFileUploadHelper() throws IOException, URISyntaxException {
        FileUploadHelper fuh = new FileUploadHelper();
        final String path = getDefinedUploadPath(ENV_UPLOAD_PROTECTED_PATH);
        Assert.notNull(path, "You should define a protected file path");

        fuh.setUploadDirectoryPath(path);
        fuh.setResourceLocation("file:///" + path);
        fuh.setUrlResourceMapping(ViewController.FILE_VIEW.replaceFirst("/", ""));
        fuh.setUseDefaultPath(false);
        fuh.setFileMaxSize(env.getProperty(ENV_UPLOAD_FILESIZE, long.class, defaultFileMaxSize));
        final String mimeTypesFilePath = env.getProperty(ENV_UPLOAD_FILE_AUTHORIZED_MIME_TYPE, defaultFileMimeTypes);
        try {
            List<String> list = Files.readAllLines(Paths.get(new ClassPathResource(mimeTypesFilePath).getURI()), Charsets.UTF_8);
            fuh.setAuthorizedMimeType(list);
        } catch (IOException e) {
            log.error("No file describing authorized MimeTypes is readable !", e);
            throw e;
        }
        log.debug("FileUploadConfiguration : " + fuh.toString());
        return fuh;
    }

    private String getDefinedUploadPath(final String propertyKey) {
        if (env.containsProperty(propertyKey)) {
            final String path = env.getProperty(propertyKey);
            final File file = new File(path);
            Assert.state(file.exists(), "You defined the property \'" + propertyKey + "\' = '" + path + "' but the path doesn't exist or there isn't read permissions");
            Assert.state(file.isDirectory(), "You defined the property \'\" + propertyKey + \"\' = '" + path + "' but the path isn't a directory");
            Assert.state(file.canWrite(), "You defined the property \'\" + propertyKey + \"\' = '" + path + "' but the path isn't has write access");
            return path;
        }
        return null;
    }
}
