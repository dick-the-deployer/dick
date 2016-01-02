/*
 * Copyright dick the deployer.
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
package com.dickthedeployer.dick.web.mapper;

import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.model.ProjectModel;
import com.dickthedeployer.dick.web.model.dickfile.EnvironmentVariable;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author mariusz
 */
public class ProjectMapper {

    public static ProjectModel mapProject(Project project) {
        return ProjectModel.builder()
                .creationDate(project.getCreationDate())
                .description(project.getDescription())
                .environmentVariables(
                        project.getEnvironmentVariables().stream()
                        .map(envVariable -> new EnvironmentVariable(envVariable.getVariableKey(), envVariable.getVariableValue()))
                        .collect(toList())
                ).name(project.getName())
                .namespace(project.getNamespace().getName())
                .ref(project.getRef())
                .repository(project.getRepository())
                .id(project.getId())
                .build();
    }
}
