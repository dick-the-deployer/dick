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

import com.dickthedeployer.dick.web.ContextTestBase;
import com.dickthedeployer.dick.web.domain.Namespace;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.model.ProjectModel;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mariusz
 */
public class ProjectServiceTest extends ContextTestBase {

    @Autowired
    ProjectService projectService;

    @Test
    public void shouldCreateStack() {
        namespaceDao.save(new Namespace.Builder()
                .withName("test-namespace")
                .build()
        );
        ProjectModel model = new ProjectModel();
        model.setName(UUID.randomUUID().toString());
        model.setRepository(UUID.randomUUID().toString());
        model.setRef("master");
        model.setNamespace("test-namespace");

        Project entity = projectService.createProject(model);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getRef()).isEqualTo("master");
        assertThat(entity.getCreationDate()).isNotNull();

    }
}
