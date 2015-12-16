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

import com.dickthedeployer.dick.web.dao.ProjectDao;
import com.dickthedeployer.dick.web.domain.EnvironmentVariable;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.model.ProjectModel;
import static java.util.stream.Collectors.toList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 *
 * @author mariusz
 */
@Service
public class ProjectService {

    @Autowired
    ProjectDao projectDao;

    public Project createProject(ProjectModel model) {
        return projectDao.save(new Project.Builder()
                .withRef(model.getRef())
                .withName(model.getName())
                .withRepository(model.getRepository())
                .withEnvironmentVariables(model.getEnvironmentVariables().stream()
                        .map(variable -> new EnvironmentVariable(variable.getKey(), variable.getValue()))
                        .collect(toList())
                ).build()
        );
    }

    public Page<Project> getProjects(int page, int size) {
        return projectDao.findAll(new PageRequest(page, size));
    }
}
