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

import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author mariusz
 */
@Service
public class DickYmlService {

    @Autowired
    RepositoryService repositoryService;

    public Dickfile loadDickFile(Build build) throws DickFileMissingException {
        try (InputStream file = repositoryService.getFile(build.getProject(), build.getSha(), ".dick.yml")) {
            if (file == null) {
                throw new DickFileMissingException();
            }
            Yaml yaml = new Yaml(new Constructor(Dickfile.class));
            return (Dickfile) yaml.load(file);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
