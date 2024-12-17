package org.container.platform.chaos.api.workloads.pods.support;

import lombok.Data;

import java.util.Map;

/**
 * Container Statuses Item Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
public class ContainerStatusesItem {
    private String name;
    private Map<String, ContainerState> state;
    private String image;
    private Integer restartCount;
}
