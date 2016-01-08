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
package com.dickthedeployer.dick.web.hook;

import com.dickthedeployer.dick.web.exception.RepositoryParsingException;
import com.google.common.base.Throwables;

import java.net.URI;
import java.net.URISyntaxException;

public class RepositoryMapper {

    public static String getHost(String repo) throws RepositoryParsingException {
        try {
            if (repo.contains("@") && repo.contains(":")) {
                return repo.substring(repo.indexOf('@') + 1, repo.indexOf(':'));
            } else {
                URI httpUri = getUri(repo);
                return httpUri.getHost();
            }
        } catch (RuntimeException ex) {
            throw new RepositoryParsingException(ex);
        }
    }

    public static String getPath(String repo) throws RepositoryParsingException {
        try {
            if (repo.contains("@") && repo.contains(":")) {
                String suffix = repo.substring(repo.indexOf(':') + 1, repo.length());
                return suffix.substring(0, suffix.indexOf('.'));
            } else {
                URI httpUri = getUri(repo);
                return httpUri.getPath().substring(1, httpUri.getPath().indexOf('.'));
            }
        } catch (RuntimeException ex) {
            throw new RepositoryParsingException(ex);
        }
    }

    private static URI getUri(String repo) {
        try {
            return new URI(repo);
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }
}
