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

import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.exception.NameTakenException;
import static com.dickthedeployer.dick.web.mapper.ProjectMapper.mapProject;
import com.dickthedeployer.dick.web.model.ProjectModel;
import com.dickthedeployer.dick.web.service.ProjectService;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mariusz
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @RequestMapping(method = POST)
    public void createProject(@RequestBody @Valid ProjectModel projectModel) throws NameTakenException {
        projectService.createProject(projectModel);
    }

    @RequestMapping(method = GET)
    public List<ProjectModel> getProjects(@RequestParam("page") int page, @RequestParam("size") int size,
            @RequestParam(required = false, name = "name") String name) {
        Page<Project> projects;
        if (StringUtils.isEmpty(name)) {
            projects = projectService.getProjects(page, size);
        } else {
            projects = projectService.getProjectsLikeName(name, page, size);
        }

        return projects.getContent().stream()
                .map(project
                        -> mapProject(project)
                ).collect(toList());
    }
}
