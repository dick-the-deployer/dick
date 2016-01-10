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
package com.dickthedeployer.dick.web.controller;

import com.dickthedeployer.dick.web.exception.BuildAlreadyQueuedException;
import com.dickthedeployer.dick.web.model.HookModel;
import com.dickthedeployer.dick.web.service.BuildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 *
 * @author mariusz
 */
@Slf4j
@RestController
public class HookController {

    @Autowired
    BuildService buildService;

    @RequestMapping(method = POST, value = "/api/hooks")
    public void receiveHook(@RequestBody @Valid HookModel hook) throws BuildAlreadyQueuedException {
        buildService.onHook(hook);
    }
}
