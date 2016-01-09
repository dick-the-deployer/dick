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

import com.dickthedeployer.dick.web.dao.GroupDao;
import com.dickthedeployer.dick.web.dao.NamespaceDao;
import com.dickthedeployer.dick.web.domain.Group;
import com.dickthedeployer.dick.web.domain.Namespace;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.exception.NameTakenException;
import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.mapper.GroupMapper;
import com.dickthedeployer.dick.web.mapper.ProjectMapper;
import com.dickthedeployer.dick.web.model.GroupModel;
import com.dickthedeployer.dick.web.model.ProjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author mariusz
 */
@Service
public class GroupService {

    @Autowired
    GroupDao groupDao;

    @Autowired
    NamespaceDao namespaceDao;

    @Autowired
    BuildService buildService;

    @Autowired
    ProjectService projectService;

    public void createGroup(GroupModel groupModel) throws NameTakenException {
        validateIfNameAvailable(groupModel);

        Group group = new Group.Builder()
                .withDescription(groupModel.getDescription())
                .withNamespace(
                        new Namespace.Builder()
                        .withName(groupModel.getName())
                        .build()
                ).build();

        groupDao.save(group);
    }

    private void validateIfNameAvailable(GroupModel groupModel) throws NameTakenException {
        Optional<Namespace> namespace = namespaceDao.findByName(groupModel.getName());
        if (namespace.isPresent()) {
            throw new NameTakenException();
        }
    }

    public List<GroupModel> getGroups(int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.DESC, "creationDate");
        return groupDao.findAll(pageRequest).getContent().stream()
                .map(GroupMapper::mapGroup)
                .collect(toList());
    }

    public GroupModel getGroup(String name) throws NotFoundException {
        Optional<Group> groupOptional = groupDao.findByNamespaceName(name);
        Group group = groupOptional.orElseThrow(NotFoundException::new);
        List<ProjectModel> projectModels = group.getNamespace().getProjects().stream()
                .map((Project project) -> {
                    ProjectModel model = ProjectMapper.mapProject(project);
                    model.setLastBuild(buildService.findLastBuild(project));
                    return model;
                }).collect(toList());
        GroupModel model = GroupMapper.mapGroupShallow(group);
        Collections.reverse(projectModels);
        model.setProjects(projectModels);
        return model;
    }

    @Transactional
    public void updateGroup(Long groupId, GroupModel groupModel) throws NameTakenException {
        Group group = groupDao.findOne(groupId);
        if (!groupModel.getName().equals(group.getNamespace().getName())) {
            validateIfNameAvailable(groupModel);
            group.getNamespace().setName(groupModel.getName());
        }
        group.setDescription(groupModel.getDescription());
        groupDao.save(group);
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = groupDao.findOne(groupId);
        group.getNamespace().getProjects().stream()
                .forEach(projectService::deleteProject);
        groupDao.delete(group);
    }
}
