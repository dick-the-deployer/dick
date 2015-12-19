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

import com.dickthedeployer.dick.web.domain.Group;
import static com.dickthedeployer.dick.web.mapper.ProjectMapper.mapProject;
import com.dickthedeployer.dick.web.model.GroupModel;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author mariusz
 */
public class GroupMapper {

    public static GroupModel mapGroup(Group group) {
        return GroupModel.builder()
                .creationDate(group.getCreationDate())
                .description(group.getDescription())
                .name(group.getNamespace().getName())
                .projects(group.getNamespace().getProjects().stream()
                        .map(project
                                -> mapProject(project)
                        ).collect(toList())
                ).build();
    }
}
