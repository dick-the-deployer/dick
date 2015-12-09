package com.dickthedeployer.dick.web.service.util;

import com.dickthedeployer.dick.web.dao.JobBuildDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionalQueryingService {

    @Autowired
    JobBuildDao jobBuildDao;

    @Transactional
    public String getOutput(Long buildJobId) {
        return jobBuildDao.findOne(buildJobId).getBuildLog().getOutput();
    }
}
