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
import com.dickthedeployer.dick.web.dao.JobBuildDao;
import com.dickthedeployer.dick.web.dao.ProjectDao;
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.exception.BuildAlreadyQueuedException;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.mapper.BuildDetailsMapper;
import com.dickthedeployer.dick.web.mapper.BuildMapper;
import com.dickthedeployer.dick.web.mapper.ProjectMapper;
import com.dickthedeployer.dick.web.model.*;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author mariusz
 */
@Slf4j
@Service
public class BuildService {

    @Autowired
    BuildDao buildDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    JobBuildDao jobBuildDao;

    @Autowired
    DickYmlService dickYmlService;

    @Autowired
    JobBuildService jobBuildService;

    @Transactional
    public void onTrigger(TriggerModel model) throws BuildAlreadyQueuedException {
        Optional<Project> projectOptional = projectDao.findByNamespaceNameAndName(model.getNamespace(), model.getName());
        Project project = projectOptional.get();
        log.info("Found project {} for name {}", project.getId(), model.getName());
        buildProject(project, "HEAD");
    }

    public void onHook(HookModel hookModel) {
        projectDao.findByRepositoryHostAndRepositoryPathAndRef(hookModel.getHost(), hookModel.getPath(), hookModel.getRef()).stream()
                .forEach(project -> {
                    try {
                        buildProject(project, hookModel.getSha());
                    } catch (BuildAlreadyQueuedException ex) {
                        log.info("Cannot queue project {}; already in queue", project.getName());
                    }
                });
    }

    private void buildProject(Project project, String sha) throws BuildAlreadyQueuedException {
        Optional<Build> inQueue = buildDao.findByProjectAndInQueueTrue(project);
        if (inQueue.isPresent()) {
            throw new BuildAlreadyQueuedException();
        }
        Build build = buildDao.save(new Build.Builder()
                .withProject(project)
                .withSha(sha)
                .build()
        );
        try {
            Dickfile dickfile = dickYmlService.loadDickFile(build);
            build.setStages(dickfile.getStageNames());
            build.setStatus(Build.Status.READY);
            buildDao.save(build);
            Stage firstStage = dickfile.getFirstStage();
            jobBuildService.prepareJobs(build, dickfile);
            if (firstStage.isAutorun()) {
                jobBuildService.buildStage(build, dickfile, firstStage);
            }
        } catch (DickFileMissingException ex) {
            log.info("Dickfile is missing", ex);
            build.setStatus(Build.Status.MISSING_DICKFILE);
            buildDao.save(build);
        }
    }

    public void buildStage(Long buildId, String stageName) throws NotFoundException {
        Build build = getAndCheckBuild(buildId);
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

    public BuildModel findLastBuild(Project project) {
        Page<Build> builds = buildDao.findByProject(project, new PageRequest(0, 1, Sort.Direction.DESC, "creationDate"));
        return builds.getContent().isEmpty() ? null : BuildMapper.mapBuild(builds.getContent().get(0));
    }

    public void kill(Long buildId) throws NotFoundException {
        Build build = getAndCheckBuild(buildId);
        jobBuildService.stop(build);
    }


    public BuildDetailsModel getBuild(Long buildId) throws NotFoundException {
        Build build = getAndCheckBuild(buildId);
        ProjectModel projectModel = ProjectMapper.mapProject(build.getProject());
        List<JobBuild> jobBuilds = jobBuildDao.findByBuild(build);
        return BuildDetailsMapper.mapBuildDetails(build, projectModel, jobBuilds);
    }

    private Build getAndCheckBuild(Long buildId) throws NotFoundException {
        Build build = buildDao.findOne(buildId);
        if (build == null) {
            throw new NotFoundException();
        }
        return build;
    }

    public List<BuildModel> getBuilds(String namespace, String name, int page, int size) {
        Optional<Project> projectOptional = projectDao.findByNamespaceNameAndName(namespace, name);
        Page<Build> builds = buildDao.findByProject(projectOptional.get(), new PageRequest(page, size,
                Sort.Direction.DESC, "creationDate"));
        return builds.getContent().stream()
                .map(BuildMapper::mapBuild)
                .collect(toList());
    }

    @Transactional
    public void deleteBuilds(Project project) {
        PageRequest pageable = new PageRequest(0, 20);
        Page<Build> page;
        do {
            page = buildDao.findByProject(project, pageable);
            page.getContent().stream()
                    .forEach(build -> {
                        jobBuildService.deleteJobBuilds(build);
                        buildDao.delete(build);
                    });
        } while (page.hasNext());
    }
}
