package com.dickthedeployer.dick.web.service;

import com.dickthedeployer.dick.web.ContextTestBase;
import com.dickthedeployer.dick.web.model.InfoModel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class InfoServiceTest extends ContextTestBase {

    @Autowired
    InfoService infoService;

    @Test
    public void shouldReturnInfo() {
        InfoModel info = infoService.getInfo();

        assertThat(info).isNotNull();
    }

}
