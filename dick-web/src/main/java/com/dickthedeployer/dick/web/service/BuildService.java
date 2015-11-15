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
import com.dickthedeployer.dick.web.dao.StackDao;
import com.dickthedeployer.dick.web.domain.BuildEntity;
import com.dickthedeployer.dick.web.domain.StackEntity;
import com.dickthedeployer.dick.web.model.TriggerModel;
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

    //build url in trigger; commit url in trigger,
    //get .dick.yml for this commit
    //store in db
    //if should deploy -> delegate with dick.yml to deploy service
    public void onTrigger(TriggerModel model) {
        StackEntity stackEntity = stackDao.findByProjectName(model.getProjectName());
        log.info("Found stack {} for project {}", stackEntity, model.getProjectName());
        if (stackEntity != null) {
            buildDao.save(new BuildEntity.Builder()
                    .withBuildUrl(model.getBuildUrl())
                    .withCommitUrl(model.getCommitUrl())
                    .withStackEntity(stackEntity)
                    .build()
            );
        }
    }

    public Page<BuildEntity> getBuilds(int page, int size) {
        return buildDao.findAll(new PageRequest(page, size));
    }
}
