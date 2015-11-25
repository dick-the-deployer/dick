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
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.BuildStatus;
import com.dickthedeployer.dick.web.domain.DeployStatus;
import com.dickthedeployer.dick.web.domain.Deployment;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.domain.Stack;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.EnvironmentVariable;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import com.dickthedeployer.dick.web.model.dickfile.Pipeline;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import static java.util.Arrays.asList;
import java.util.List;
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

    @Test
    public void shouldPerformDeployment() throws DickFileMissingException {
        Build build = prepareBuild();
        Stage firstStage = new Stage("first", true);
        Dickfile dickfile = prepareDickfile(firstStage);

        build = deploymentService.blockingDeploy(build, dickfile, firstStage);
        List<Deployment> deployments = deploymentDao.findByBuild(build);

        assertThat(build).isNotNull();
        assertThat(build.getBuildStatus()).isEqualTo(BuildStatus.DEPLOYED);
        assertThat(deployments).asList().hasSize(2).extracting("deployStatus").containsOnly(DeployStatus.DEPLOYED);
    }

    private Dickfile prepareDickfile(Stage firstStage) {
        Dickfile dickfile = new Dickfile();
        Pipeline pipeline = new Pipeline();
        pipeline.setStages(asList(
                firstStage,
                new Stage("second", true)
        ));
        dickfile.setPipeline(pipeline);
        Job first = new Job();
        first.setEnvironmentVariables(asList(
                new EnvironmentVariable("FOOKEY", "foo")
        ));
        first.setName("first job");
        first.setStage("first");
        first.setDeploy(getOsSpecificGenericCommand());
        Job second = new Job();
        second.setEnvironmentVariables(asList(
                new EnvironmentVariable("FOOKEY", "foo")
        ));
        second.setName("second job");
        second.setStage("second");
        second.setDeploy(getOsSpecificEnvironemntCommand());
        dickfile.setJobs(asList(first, second));
        return dickfile;
    }

    private List<String> getOsSpecificGenericCommand() {
        if (isWindows()) {
            return asList("cmd.exe /c echo %SHA%", "cmd.exe /c echo %SERVER%");
        } else {
            return asList("echo $SHA", "echo $SERVER");
        }
    }

    private List<String> getOsSpecificEnvironemntCommand() {
        if (isWindows()) {
            return asList("cmd.exe /c echo %FOOKEY%");
        } else {
            return asList("echo FOOKEY");
        }
    }

    private Build prepareBuild() {
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
        return build;
    }

    public static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().contains("win"));
    }
}
