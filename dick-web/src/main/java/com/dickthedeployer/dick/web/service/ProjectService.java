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
import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.exception.RepositoryParsingException;
import com.dickthedeployer.dick.web.exception.RepositoryUnavailableException;
import com.dickthedeployer.dick.web.hook.RepositoryMapper;
import com.dickthedeployer.dick.web.mapper.ProjectMapper;
import com.dickthedeployer.dick.web.model.ProjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    RepositoryService repositoryService;

    public void createProject(ProjectModel model) throws NameTakenException, RepositoryUnavailableException, RepositoryParsingException {
        validateIfNameAvailable(model.getNamespace(), model.getName());
        Namespace namespace = namespaceDao.findByName(model.getNamespace()).get();
        Project project = new Project.Builder()
                .withRef(model.getRef())
                .withName(model.getName())
                .withNamespace(namespace)
                .withDescription(model.getDescription())
                .withRepository(model.getRepository())
                .withRepositoryHost(RepositoryMapper.getHost(model.getRepository()))
                .withRepositoryPath(RepositoryMapper.getPath(model.getRepository()))
                .withEnvironmentVariables(getEnvironment(model))
                .build();
        validateIfRepositoryAvailable(project);
        projectDao.save(project);
    }

    private List<EnvironmentVariable> getEnvironment(ProjectModel model) {
        return model.getEnvironmentVariables().stream()
                .map(variable -> new EnvironmentVariable(variable.getKey(), variable.getValue()))
                .collect(toList());
    }

    private void validateIfRepositoryAvailable(Project project) throws RepositoryUnavailableException {
        repositoryService.checkoutRepository(project);
    }

    private void validateIfNameAvailable(String namespace, String name) throws NameTakenException {
        Optional<Project> project = projectDao.findByNamespaceNameAndName(namespace, name);
        if (project.isPresent()) {
            throw new NameTakenException();
        }
    }

    public List<ProjectModel> getProjects(List<Long> ids) {
        return projectDao.findByIdIn(ids, new Sort(Sort.Direction.DESC, "creationDate")).stream()
                .map(this::mapProject).collect(toList());
    }

    public List<ProjectModel> getProjectsLikeName(String name, int page, int size) {
        return projectDao.findByNameContaining(name, new PageRequest(page, size, Sort.Direction.DESC, "creationDate")).getContent().stream()
                .map(this::mapProject).collect(toList());
    }

    public List<ProjectModel> getProjects(int page, int size) {
        return projectDao.findAll(new PageRequest(page, size, Sort.Direction.DESC, "creationDate")).getContent().stream()
                .map(this::mapProject).collect(toList());
    }

    public ProjectModel getProject(String namespaceName, String name) throws NotFoundException {
        Optional<Project> project = projectDao.findByNamespaceNameAndName(namespaceName, name);
        return mapProject(project.orElseThrow(NotFoundException::new));
    }

    private ProjectModel mapProject(Project project) {
        ProjectModel model = ProjectMapper.mapProject(project);
        model.setLastBuild(buildService.findLastBuild(project));
        return model;
    }

    public void updateProject(Long projectId, ProjectModel model) throws RepositoryParsingException, RepositoryUnavailableException, NotFoundException {
        Project project = getAndCheckProject(projectId);
        project.setRepository(model.getRepository());
        project.setRef(model.getRef());
        project.setDescription(model.getDescription());
        project.setRepositoryHost(RepositoryMapper.getHost(model.getRepository()));
        project.setRepositoryPath(RepositoryMapper.getPath(model.getRepository()));
        project.setEnvironmentVariables(getEnvironment(model));

        validateIfRepositoryAvailable(project);
        projectDao.save(project);
    }

    private Project getAndCheckProject(Long projectId) throws NotFoundException {
        Project project = projectDao.findOne(projectId);
        if (project == null) {
            throw new NotFoundException();
        }
        return project;
    }

    public void renameProject(Long projectId, ProjectModel model) throws NameTakenException, NotFoundException {
        Project project = getAndCheckProject(projectId);
        validateIfNameAvailable(project.getNamespace().getName(), model.getName());
        project.setName(model.getName());
        projectDao.save(project);
    }

    public void moveProject(Long projectId, ProjectModel model) throws NameTakenException, NotFoundException {
        Project project = getAndCheckProject(projectId);
        validateIfNameAvailable(model.getNamespace(), project.getName());
        Namespace namespace = namespaceDao.findByName(model.getNamespace()).get();
        project.setNamespace(namespace);
        projectDao.save(project);
    }

    @Transactional
    public void deleteProject(Long projectId) throws NotFoundException {
        Project project = getAndCheckProject(projectId);
        deleteProject(project);
    }

    @Transactional
    public void deleteProject(Project project) {
        buildService.deleteBuilds(project);
        projectDao.delete(project);
    }
}
