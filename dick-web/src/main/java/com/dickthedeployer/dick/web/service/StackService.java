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
package com.dickthedeployer.dick.web.service;

import com.dickthedeployer.dick.web.dao.StackDao;
import com.dickthedeployer.dick.web.domain.StackEntity;
import com.dickthedeployer.dick.web.model.StackModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 *
 * @author mariusz
 */
@Service
public class StackService {

    @Autowired
    StackDao stackDao;

    public StackEntity createStack(StackModel model) {
        return stackDao.save(new StackEntity.Builder()
                .withRef(model.getRef())
                .withRepository(model.getRepository())
                .withProjectName(model.getProjectName())
                .withServer(model.getServer())
                .build()
        );
    }

    public Page<StackEntity> getStacks(int page, int size) {
        return stackDao.findAll(new PageRequest(page, size));
    }
}
