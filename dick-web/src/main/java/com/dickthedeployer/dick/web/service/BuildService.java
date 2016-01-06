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
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.mapper.BuildDetailsMapper;
import com.dickthedeployer.dick.web.mapper.BuildMapper;
import com.dickthedeployer.dick.web.mapper.ProjectMapper;
import com.dickthedeployer.dick.web.model.BuildDetailsModel;
import com.dickthedeployer.dick.web.model.BuildModel;
import com.dickthedeployer.dick.web.model.ProjectModel;
import com.dickthedeployer.dick.web.model.TriggerModel;
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
    public void onTrigger(TriggerModel model) {
        List<Project> projects = projectDao.findByNameAndRef(model.getName(), model.getRef());
        projects.forEach(project -> {
            log.info("Found project {} for name {}", project.getId(), model.getName());
            Build build = buildDao.save(new Build.Builder()
                    .withCommitUrl(model.getCommitUrl())
                    .withProject(project)
                    .withSha(model.getSha())
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

    public BuildModel findLastBuild(Project project) {
        Page<Build> builds = buildDao.findByProject(project, new PageRequest(0, 1, Sort.Direction.DESC, "creationDate"));
        return builds.getContent().isEmpty() ? null : BuildMapper.mapBuild(builds.getContent().get(0));
    }

    public void kill(Long buildId) {
        Build build = buildDao.findOne(buildId);
        jobBuildService.stop(build);
    }


    public BuildDetailsModel getBuild(Long buildId) {
        Build build = buildDao.findOne(buildId);
        ProjectModel projectModel = ProjectMapper.mapProject(build.getProject());
        List<JobBuild> jobBuilds = jobBuildDao.findByBuild(build);
        return BuildDetailsMapper.mapBuildDetails(build, projectModel, jobBuilds);
    }

    public List<BuildModel> getBuilds(String namespace, String name, int page, int size) {
        Project project = projectDao.findByNamespaceNameAndName(namespace, name);
        Page<Build> builds = buildDao.findByProject(project, new PageRequest(page, size,
                Sort.Direction.DESC, "creationDate"));
        return builds.getContent().stream()
                .map(BuildMapper::mapBuild)
                .collect(toList());
    }
}
