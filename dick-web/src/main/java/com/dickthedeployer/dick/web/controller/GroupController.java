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

import com.dickthedeployer.dick.web.exception.NameTakenException;
import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.model.GroupModel;
import com.dickthedeployer.dick.web.service.GroupService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mariusz
 */
@Slf4j
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    GroupService groupService;

    @RequestMapping(method = RequestMethod.POST)
    public void createGroup(@RequestBody @Valid GroupModel groupModel) throws NameTakenException {
        groupService.createGroup(groupModel);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<GroupModel> getGroups(@RequestParam("page") int page, @RequestParam("size") int size) {
        return groupService.getGroups(page, size);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public GroupModel getGroupByName(@PathVariable("name") String name) throws NotFoundException {
        GroupModel group = groupService.getGroup(name);
        if (group == null) {
            throw new NotFoundException();
        } else {
            return group;
        }
    }

}
