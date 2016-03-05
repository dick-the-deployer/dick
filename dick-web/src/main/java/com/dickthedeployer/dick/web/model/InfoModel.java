package com.dickthedeployer.dick.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoModel {

    String publicKey;
    String version;
    String commitShort;
    boolean versionUpToDate;
}
