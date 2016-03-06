package com.dickthedeployer.dick.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentVariable {

    private String name;
    private String value;
    private boolean secure;
}
