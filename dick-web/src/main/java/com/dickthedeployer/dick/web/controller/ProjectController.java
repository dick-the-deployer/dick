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
package com.dickthedeployer.dick.web.controller;

import com.dickthedeployer.dick.web.exception.NameTakenException;
import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.exception.RepositoryParsingException;
import com.dickthedeployer.dick.web.exception.RepositoryUnavailableException;
import com.dickthedeployer.dick.web.model.BuildModel;
import com.dickthedeployer.dick.web.model.ProjectModel;
import com.dickthedeployer.dick.web.service.BuildService;
import com.dickthedeployer.dick.web.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 *
 * @author mariusz
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Autowired
    BuildService buildService;

    @RequestMapping(method = POST)
    public void createProject(@RequestBody @Valid ProjectModel projectModel) throws NameTakenException, RepositoryUnavailableException, RepositoryParsingException {
        projectService.createProject(projectModel);
    }

    @RequestMapping(method = PUT, value = "{projectId}")
    public void updateProject(@PathVariable("projectId") Long projectId,
                              @RequestBody ProjectModel model) throws RepositoryUnavailableException,
            RepositoryParsingException, NotFoundException {
        projectService.updateProject(projectId, model);
    }

    @RequestMapping(method = DELETE, value = "{projectId}")
    public void deleteProject(@PathVariable("projectId") Long projectId) throws NotFoundException {
        projectService.deleteProject(projectId);
    }

    @RequestMapping(method = PUT, value = "{projectId}/name")
    public void renameProject(@PathVariable("projectId") Long projectId,
                              @RequestBody ProjectModel model) throws NameTakenException, NotFoundException {
        projectService.renameProject(projectId, model);
    }

    @RequestMapping(method = PUT, value = "{projectId}/namespace")
    public void moveProject(@PathVariable("projectId") Long projectId,
                            @RequestBody ProjectModel model) throws NameTakenException, NotFoundException {
        projectService.moveProject(projectId, model);
    }

    @RequestMapping(method = GET)
    public List<ProjectModel> getProjects(@RequestParam("page") int page, @RequestParam("size") int size,
                                          @RequestParam(required = false, name = "name") String name) {
        if (StringUtils.isEmpty(name)) {
            return projectService.getProjects(page, size);
        } else {
            return projectService.getProjectsLikeName(name, page, size);
        }
    }

    @RequestMapping(method = GET, value = "/all")
    public List<ProjectModel> getProjects(@RequestParam("ids") List<Long> ids) {
        return projectService.getProjects(ids);
    }

    @RequestMapping(method = GET, value = "/{namespace}/{name}")
    public ProjectModel getProject(@PathVariable("namespace") String namespace, @PathVariable("name") String name) throws NotFoundException {
        return projectService.getProject(namespace, name);
    }

    @RequestMapping(method = GET, value = "/{namespace}/{name}/builds")
    public List<BuildModel> getBuilds(@PathVariable("namespace") String namespace, @PathVariable("name") String name,
                                      @RequestParam("page") int page, @RequestParam("size") int size) {
        return buildService.getBuilds(namespace, name, page, size);
    }
}
