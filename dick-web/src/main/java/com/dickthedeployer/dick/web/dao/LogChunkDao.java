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

import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.LogChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author mariusz
 */
public interface LogChunkDao extends JpaRepository<LogChunk, Long> {

    List<LogChunk> findByJobBuild(JobBuild jobBuild);

    List<LogChunk> findByJobBuildAndCreationDateAfter(JobBuild jobBuild, Date date);
}
