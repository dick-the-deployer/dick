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
package com.dickthedeployer.dick.web.hook.gitlabci;

import com.dickthedeployer.dick.web.exception.BuildAlreadyQueuedException;
import com.dickthedeployer.dick.web.model.HookModel;
import com.dickthedeployer.dick.web.service.BuildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mariusz
 */
@Slf4j
@Service
public class GitlabCiService {

    @Autowired
    BuildService buildService;

    public void onTrigger(GitlabCiTrigger trigger) throws BuildAlreadyQueuedException {
        log.info("Received gitlab-ci trigger for {} with status {}", trigger.getProject_name(), trigger.getBuild_status());
        if (trigger.getBuild_status().equals("success")) {
            HookModel model = new HookModel();
            model.setPath(trigger.getProjectName());
            model.setRef(trigger.getRef());
            model.setSha(trigger.getSha());
            model.setHost(trigger.getHost());
            buildService.onHook(model);
        }
    }

}
