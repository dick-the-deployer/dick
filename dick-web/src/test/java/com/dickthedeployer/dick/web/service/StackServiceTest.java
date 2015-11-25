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
import com.dickthedeployer.dick.web.dao.ProjectDao;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.domain.Stack;
import com.dickthedeployer.dick.web.model.StackModel;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mariusz
 */
public class StackServiceTest extends ContextTestBase {

    @Autowired
    StackService stackService;

    @Autowired
    ProjectDao projectDao;

    @Test
    public void shouldCreateStack() {
        Project project = new Project();
        project.setProjectName(UUID.randomUUID().toString());
        project.setRepository(UUID.randomUUID().toString());
        projectDao.save(project);
        StackModel model = new StackModel();
        model.setProjectId(project.getId());
        model.setRef("master");

        Stack entity = stackService.createStack(model);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getRef()).isEqualTo("master");
        assertThat(entity.getProject().getId()).isEqualTo(project.getId());
        assertThat(entity.getCreationDate()).isNotNull();

    }
}
