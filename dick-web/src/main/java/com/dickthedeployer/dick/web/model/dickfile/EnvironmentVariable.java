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
package com.dickthedeployer.dick.web.model.dickfile;

import lombok.Data;

/**
 * @author mariusz
 */
@Data
public class EnvironmentVariable {

    private String key;
    private String value;
    private boolean secure;

    public EnvironmentVariable() {
    }

    public EnvironmentVariable(String key, String value, boolean secure) {
        this.key = key;
        this.value = value;
        this.secure = secure;
    }

}
