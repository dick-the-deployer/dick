package com.dickthedeployer.dick.web.service;

import com.dickthedeployer.dick.web.facade.GithubFacade;
import com.dickthedeployer.dick.web.model.InfoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Slf4j
@Service
public class InfoService {

    @Value("${version:DEV}")
    String version;

    @Value("${git.commit.id.describe:LATEST}")
    String commitShort;

    String publicKey;

    boolean latest;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    GithubFacade githubFacade;

    @PostConstruct
    public void init() {
        Resource resource = resourceLoader.getResource("file:/root/.ssh/id_rsa.pub");
        try (InputStream is = resource.getInputStream()) {
            publicKey = StreamUtils.copyToString(is, Charset.defaultCharset());
        } catch (IOException e) {
            log.info("Could not read public key", e);
        }
        checkIfNewVersionAvailable();
    }

    @Scheduled(fixedDelay = 1800000)
    public void checkIfNewVersionAvailable() {
        try {
            latest = githubFacade.getCommits().get(0).getSha().startsWith(commitShort);
        } catch (Exception ex) {
            log.info("Unable to determine if version is latest", ex);
        }
    }

    public InfoModel getInfo() {
        return InfoModel.builder()
                .version(version)
                .versionUpToDate(latest)
                .commitShort(commitShort)
                .publicKey(publicKey)
                .build();
    }
}
