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

import com.dickthedeployer.dick.web.dao.BuildDao;
import com.dickthedeployer.dick.web.dao.DeploymentDao;
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.BuildStatus;
import com.dickthedeployer.dick.web.domain.Deployment;
import com.dickthedeployer.dick.web.model.CommandResult;
import com.dickthedeployer.dick.web.model.Dickfile;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author mariusz
 */
@Slf4j
@Service
public class DeploymentService {

    @Autowired
    CommandService commandService;

    @Autowired
    DeploymentDao deploymentDao;

    @Autowired
    BuildDao buildDao;

    @Async
    public void deploy(Build build, Dickfile dickfile) {
        blockingDeploy(build, dickfile);
    }

    public Deployment blockingDeploy(Build build, Dickfile dickfile) {
        Build previous = buildDao.findByStackAndBuildStatus(build.getStack(), BuildStatus.DEPLOYED);
        Deployment deployment = new Deployment();
        deployment.setBuild(build);
        deploymentDao.save(deployment);
        int status = performDeploy(deployment, build, dickfile.getDeploy(), getEnvironment(build));
        if (status == 0) {
            if (previous != null) {
                previous.setBuildStatus(BuildStatus.READY);
                buildDao.save(previous);
            }
            return deployment;
        } else {
            Deployment rollback = new Deployment();
            rollback.setBuild(previous);
            deploymentDao.save(rollback);
            deployment.setRollback(rollback);
            deploymentDao.save(deployment);
            performDeploy(rollback, previous, dickfile.getRollback(), getEnvironment(previous));
            return rollback;
        }
    }

    private Map<String, String> getEnvironment(Build build) {
        Map<String, String> environment = new HashMap<>();
        environment.put("SHA", build.getSha());
        environment.put("SERVER", build.getStack().getServer());
        return environment;
    }

    private int performDeploy(Deployment deployment, Build build, List<String> deploy, Map<String, String> environment) {
        build.setBuildStatus(BuildStatus.IN_PROGRESS);
        buildDao.save(build);

        for (String command : deploy) {
            try {
                Path temp = Files.createTempDirectory("deployment-" + deployment.getId());
                CommandResult result = commandService.invokeWithEnvironment(temp, environment, command.split(" "));
                StringBuilder builder = new StringBuilder(deployment.getDeploymentLog());
                deployment.setDeploymentLog(builder.append(result.getOutput()).toString());
                deploymentDao.save(deployment);
                if (result.getResult() != 0) {
                    build.setBuildStatus(BuildStatus.FAILED);
                    buildDao.save(build);
                    return result.getResult();
                }
            } catch (IOException ex) {
                throw Throwables.propagate(ex);
            }
        }
        build.setBuildStatus(BuildStatus.DEPLOYED);
        buildDao.save(build);
        return 0;
    }

    public Page<Deployment> getDeployments(int page, int size) {
        return deploymentDao.findAll(new PageRequest(page, size));
    }

}
