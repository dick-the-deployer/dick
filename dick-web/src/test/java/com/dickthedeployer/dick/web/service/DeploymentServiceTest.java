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
package com.dickthedeployer.dick.web.service;

import com.dickthedeployer.dick.web.ContextTestBase;
import com.dickthedeployer.dick.web.dao.BuildDao;
import com.dickthedeployer.dick.web.dao.ProjectDao;
import com.dickthedeployer.dick.web.dao.StackDao;
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.Deployment;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.domain.Stack;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.model.Dickfile;
import static java.util.Arrays.asList;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mariusz
 */
public class DeploymentServiceTest extends ContextTestBase {

    @Autowired
    DeploymentService deploymentService;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    StackDao stackDao;

    @Autowired
    BuildDao buildDao;

    @Test
    public void shouldPerformDeployment() throws DickFileMissingException {
        final Project project = new Project.Builder()
                .withProjectName(UUID.randomUUID().toString())
                .withRepository(UUID.randomUUID().toString())
                .build();
        projectDao.save(project);

        final Stack stack = new Stack.Builder()
                .withRef("master")
                .withServer("not localhost")
                .withProject(project).build();
        stackDao.save(stack);

        Build build = new Build.Builder()
                .withSha("somesha")
                .withStack(stack).build();
        buildDao.save(build);

        Dickfile dickfile = new Dickfile();
        if (isWindows()) {
            dickfile.setDeploy(asList("cmd.exe /c echo %SHA%", "cmd.exe /c echo %SERVER%"));
        } else {
            dickfile.setDeploy(asList("echo $SHA", "echo $SERVER"));
        }
        Deployment deployment = deploymentService.blockingDeploy(build, dickfile);

        assertThat(deployment).isNotNull();
        assertThat(deployment.getDeploymentLog()).contains("not localhost", "somesha");
    }

    public static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().contains("win"));
    }
}
