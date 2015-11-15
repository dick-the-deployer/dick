/*
 * Copyright 2015 Pivotal Software, Inc..
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
import com.dickthedeployer.dick.web.model.TriggerModel;
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

    //build url in trigger; commit url in trigger,
    //get .dick.yml for this commit
    //store in db
    //if should deploy -> delegate with dick.yml to deploy service
    public void onTrigger(TriggerModel model) {
        Project project = projectDao.findByProjectName(model.getProjectName());
        List<Stack> stacks = stackDao.findByProjectAndRef(project, model.getRef());
        stacks.forEach(stack -> {
            log.info("Found stack {} for project {}", stack, model.getProjectName());
            if (stack != null) {
                buildDao.save(new Build.Builder()
                        .withBuildUrl(model.getBuildUrl())
                        .withCommitUrl(model.getCommitUrl())
                        .withStack(stack)
                        .build()
                );
            }
        });
    }

    public Page<Build> getBuilds(int page, int size) {
        return buildDao.findAll(new PageRequest(page, size));
    }
}
