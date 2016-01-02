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
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mariusz
 */
@Service
public class RepositoryService {

    private final static ConcurrentHashMap<Project, Path> REPOS = new ConcurrentHashMap<>();

    @Autowired
    CommandService commandService;

    public InputStream getFile(Project project, String sha, String filePath) {
        checkoutRepository(project);
        Path path = REPOS.get(project);
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

    private synchronized void checkoutRepository(Project project) {
        REPOS.computeIfAbsent(project, (key)
                -> {
                    try {
                        Path path = Files.createTempDirectory(getPrefix(key, project.getRef()));
                        initializeRepository(path, project);
                        return path;
                    } catch (IOException ex) {
                        throw Throwables.propagate(ex);
                    }
                });
    }

    private String getPrefix(Project project, String ref) {
        return project.getName().replaceAll("/", "-") + ref;
    }

    private void initializeRepository(Path path, Project stack) {
        commandService.invoke(path, "git", "init");
        commandService.invoke(path, "git", "clone", stack.getRepository());
        commandService.invoke(path, "git", "remote", "add", "origin", stack.getRepository());
    }

    private void checkoutRevision(Path path, String ref, String sha) {
        commandService.invoke(path, "git", "pull", "origin", ref);
        commandService.invoke(path, "git", "checkout", sha);
    }
}
