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

import com.dickthedeployer.dick.web.dao.BuildDao;
import com.dickthedeployer.dick.web.dao.ProjectDao;
import com.dickthedeployer.dick.web.dao.StackDao;
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.domain.Stack;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.model.TriggerModel;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 *
 * @author mariusz
 */
@Slf4j
@Service
public class BuildService {

    @Autowired
    BuildDao buildDao;

    @Autowired
    StackDao stackDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    DickYmlService dickYmlService;

    @Autowired
    JobBuildService jobBuildService;

    public void onTrigger(TriggerModel model) {
        Project project = projectDao.findByProjectName(model.getProjectName());
        List<Stack> stacks = stackDao.findByProjectAndRef(project, model.getRef());
        stacks.forEach(stack -> {
            log.info("Found stack {} for project {}", stack, model.getProjectName());
            if (stack != null) {
                Build build = buildDao.save(new Build.Builder()
                        .withBuildUrl(model.getBuildUrl())
                        .withCommitUrl(model.getCommitUrl())
                        .withStack(stack)
                        .withSha(model.getSha())
                        .build()
                );
                try {
                    Dickfile dickfile = dickYmlService.loadDickFile(build);
                    Stage firstStage = dickfile.getFirstStage();
                    if (firstStage.isAutorun()) {
                        jobBuildService.buildStage(build, dickfile, firstStage);
                    }
                } catch (DickFileMissingException ex) {
                    log.info("Dickfile is missing", ex);
                    build.setStatus(Build.Status.MISSING_DICKFILE);
                    buildDao.save(build);
                }
            }
        });
    }

    public void buildStage(Long buildId, String stageName) {
        Build build = buildDao.findOne(buildId);
        try {
            Dickfile dickfile = dickYmlService.loadDickFile(build);
            Stage stage = dickfile.getStage(stageName);
            jobBuildService.buildStage(build, dickfile, stage);
        } catch (DickFileMissingException ex) {
            log.info("Dickfile is missing", ex);
            build.setStatus(Build.Status.MISSING_DICKFILE);
            buildDao.save(build);
        }

    }

    public Page<Build> getBuilds(int page, int size) {
        return buildDao.findAll(new PageRequest(page, size));
    }
}
