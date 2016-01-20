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
package com.dickthedeployer.dick.web.dao;

import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author mariusz
 */
public interface JobBuildDao extends JpaRepository<JobBuild, Long> {

    List<JobBuild> findByBuild(Build build);

    Optional<JobBuild> findByBuildAndStageAndName(Build build, String stage, String name);

    List<JobBuild> findByStatusAndWorkerNull(JobBuild.Status status);

    Long countByWorker(Worker worker);

    Optional<JobBuild> findByStatusAndWorker(JobBuild.Status status, Worker worker);

}
