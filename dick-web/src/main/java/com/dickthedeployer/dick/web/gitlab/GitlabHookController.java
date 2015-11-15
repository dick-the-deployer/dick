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
package com.dickthedeployer.dick.web.gitlab;

import com.dickthedeployer.dick.web.model.TriggerModel;
import com.dickthedeployer.dick.web.service.BuildService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mariusz
 */
@Slf4j
@RestController
public class GitlabHookController {
    
    @Autowired
    GitlabService gitlabService;
    
    @RequestMapping(method = POST, value = "/hooks/gitlab")
    public void receiveHook(@RequestBody GitlabTrigger trigger) {
        gitlabService.onTrigger(trigger);
    }
    
}
