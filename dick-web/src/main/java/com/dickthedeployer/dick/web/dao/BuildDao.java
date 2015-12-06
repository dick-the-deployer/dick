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
package com.dickthedeployer.dick.web.dao;

import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuildStatus;
import com.dickthedeployer.dick.web.domain.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author mariusz
 */
public interface BuildDao extends JpaRepository<Build, Long> {

    Build findByStackAndBuildStatus(Stack stack, JobBuildStatus buildStatus);
}
