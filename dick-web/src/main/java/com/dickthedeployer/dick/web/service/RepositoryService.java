/*
 * Copyright 2015 dick the deployer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dickthedeployer.dick.web.service;

import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.exception.CommandExecutionException;
import com.dickthedeployer.dick.web.exception.RepositoryUnavailableException;
import com.google.common.base.Throwables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mariusz
 */
@Slf4j
@Service
public class RepositoryService {

    private final static Map<Repo, Path> REPOS = new ConcurrentHashMap<>();

    @Autowired
    CommandService commandService;

    @PreDestroy
    public void cleanup() {
        REPOS.values().stream()
                .forEach(path -> {
                    try {
                        log.info("Cleaning {}", path);
                        FileUtils.deleteDirectory(path.toFile());
                    } catch (IOException ex) {
                        throw Throwables.propagate(ex);
                    }
                });
    }

    public String getLastMessage(Project project, String sha) {
        Repo repo = new Repo(project.getName(), project.getRepository(), project.getRef());
        REPOS.computeIfAbsent(repo, key -> checkoutRepository(key));
        Path path = REPOS.get(repo);
        synchronized (path) {
            checkoutRevision(path, project.getRef(), sha);
            return commandService.invoke(path, "git", "log", "--pretty=format:%s", "-1");
        }
    }

    public String getLastSha(Project project) {
        Repo repo = new Repo(project.getName(), project.getRepository(), project.getRef());
        REPOS.computeIfAbsent(repo, key -> checkoutRepository(key));
        Path path = REPOS.get(repo);
        synchronized (path) {
            checkoutRevision(path, project.getRef(), "HEAD");
            return commandService.invoke(path, "git", "log", "--pretty=format:%H", "-1");
        }
    }

    public void checkoutRepository(Project project) throws RepositoryUnavailableException {
        Repo repo = new Repo(project.getName(), project.getRepository(), project.getRef());
        if (!REPOS.containsKey(repo)) {
            try {
                Path path = checkoutRepository(repo);
                REPOS.put(repo, path);
            } catch (CommandExecutionException ex) {
                throw new RepositoryUnavailableException();
            }
        }

    }

    public InputStream getFile(Project project, String sha, String filePath) {
        Repo repo = new Repo(project.getName(), project.getRepository(), project.getRef());
        REPOS.computeIfAbsent(repo, key -> checkoutRepository(key));
        Path path = REPOS.get(repo);
        synchronized (path) {
            checkoutRevision(path, project.getRef(), sha);
            Path file = path.resolve(filePath);
            try {
                if (Files.exists(file)) {
                    return Files.newInputStream(file);
                } else {
                    return null;
                }
            } catch (IOException ex) {
                throw Throwables.propagate(ex);
            }
        }
    }

    private synchronized Path checkoutRepository(Repo repository) {
        try {
            Path path = Files.createTempDirectory(getPrefix(repository, repository.getRef()));
            initializeRepository(path, repository);
            return path;
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private String getPrefix(Repo repository, String ref) {
        return repository.getName().replaceAll("/", "-") + ref;
    }

    private void initializeRepository(Path path, Repo repository) {
        commandService.invoke(path, "git", "clone", repository.getRepository(), ".");
    }

    private void checkoutRevision(Path path, String ref, String sha) {
        commandService.invoke(path, "git", "fetch", "origin");
        commandService.invoke(path, "git", "checkout", ref);
        commandService.invoke(path, "git", "checkout", sha);
    }

    @Data
    @AllArgsConstructor
    private class Repo {

        final String name;
        final String repository;
        final String ref;
    }
}
