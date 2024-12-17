package org.container.platform.chaos.api.chaos.model;

import lombok.Data;

@Data
public class ChaosResource {
    private long resourceId;
    private long chaosId;
    private String resourceName;
    private String type;
    private Integer choice;
    private String generateName;

    private String chaosName;
    private String namespaces;
}
