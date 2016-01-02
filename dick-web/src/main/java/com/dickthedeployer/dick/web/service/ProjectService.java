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

import com.dickthedeployer.dick.web.dao.NamespaceDao;
import com.dickthedeployer.dick.web.dao.ProjectDao;
import com.dickthedeployer.dick.web.domain.EnvironmentVariable;
import com.dickthedeployer.dick.web.domain.Namespace;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.exception.NameTakenException;
import com.dickthedeployer.dick.web.mapper.ProjectMapper;
import com.dickthedeployer.dick.web.model.ProjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author mariusz
 */
@Service
public class ProjectService {

    @Autowired
    ProjectDao projectDao;

    @Autowired
    NamespaceDao namespaceDao;

    @Autowired
    BuildService buildService;

    public void createProject(ProjectModel model) throws NameTakenException {
        validateIfNameAvailable(model);
        Namespace namespace = namespaceDao.findByName(model.getNamespace());
        projectDao.save(new Project.Builder()
                .withRef(model.getRef())
                .withName(model.getName())
                .withNamespace(namespace)
                .withDescription(model.getDescription())
                .withRepository(model.getRepository())
                .withEnvironmentVariables(model.getEnvironmentVariables().stream()
                        .map(variable -> new EnvironmentVariable(variable.getKey(), variable.getValue()))
                        .collect(toList())
                ).build()
        );
    }

    private void validateIfNameAvailable(ProjectModel model) throws NameTakenException {
        Project project = projectDao.findByName(model.getName());
        if (project != null) {
            throw new NameTakenException();
        }
    }

    public List<ProjectModel> getProjects(List<Long> ids) {
        return projectDao.findByIdIn(ids, new Sort(Sort.Direction.DESC, "creationDate")).stream()
                .map((Project project) -> {
                    ProjectModel model = ProjectMapper.mapProject(project);
                    model.setLastBuild(buildService.findLastBuild(project));
                    return model;
                }).collect(toList());
    }

    public List<ProjectModel> getProjectsLikeName(String name, int page, int size) {
        return projectDao.findByNameContaining(name, new PageRequest(page, size, Sort.Direction.DESC, "creationDate")).getContent().stream()
                .map((Project project) -> {
                    ProjectModel model = ProjectMapper.mapProject(project);
                    model.setLastBuild(buildService.findLastBuild(project));
                    return model;
                }).collect(toList());
    }

    public List<ProjectModel> getProjects(int page, int size) {
        return projectDao.findAll(new PageRequest(page, size, Sort.Direction.DESC, "creationDate")).getContent().stream()
                .map((Project project) -> {
                    ProjectModel model = ProjectMapper.mapProject(project);
                    model.setLastBuild(buildService.findLastBuild(project));
                    return model;
                }).collect(toList());
    }
}
