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
package com.dickthedeployer.dick.web.service.util;

import com.dickthedeployer.dick.web.dao.WorkerDao;
import com.dickthedeployer.dick.web.domain.Worker;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mariusz
 */
@Service
public class OptymisticLockService {

    @Autowired
    WorkerDao workerDao;

    @Transactional
    public void shouldThrowOptymisticLockException(Long id) throws InterruptedException, ExecutionException, TimeoutException {
        Worker finalWorker = workerDao.findOne(id);

        Executors.newSingleThreadExecutor().submit(() -> {
            Worker theSameWorker = workerDao.findOne(finalWorker.getId());
            theSameWorker.setStatus(Worker.Status.BUSY);
            workerDao.save(theSameWorker);
        }).get(1, TimeUnit.SECONDS);

        finalWorker.setStatus(Worker.Status.DEAD);
        workerDao.save(finalWorker);
    }
}
