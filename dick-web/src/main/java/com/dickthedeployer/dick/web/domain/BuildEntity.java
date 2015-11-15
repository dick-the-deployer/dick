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
package com.dickthedeployer.dick.web.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author mariusz
 */
@Data
@Entity
public class BuildEntity {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private StackEntity stackEntity;
    
    private String buildUrl;
    
    private String commitUrl;

    public static class Builder {
        
        private final BuildEntity item;

        public Builder() {
            this.item = new BuildEntity();
        }

        public Builder withId(final Long id) {
            this.item.id = id;
            return this;
        }

        public Builder withStackEntity(final StackEntity stackEntity) {
            this.item.stackEntity = stackEntity;
            return this;
        }

        public Builder withBuildUrl(final String buildUrl) {
            this.item.buildUrl = buildUrl;
            return this;
        }

        public Builder withCommitUrl(final String commitUrl) {
            this.item.commitUrl = commitUrl;
            return this;
        }

        public BuildEntity build() {
            return this.item;
        }
    }
    
    
}
