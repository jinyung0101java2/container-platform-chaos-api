package org.container.platform.chaos.api.clusters.clusters;

import lombok.Data;
import org.container.platform.chaos.api.common.Constants;

/**
 * Clusters Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class Clusters {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String userAuthId;
    private String namespace;

    private String clusterId;
    private String clusterApiUrl;
    private String name;
    private String clusterToken;
    private String clusterType;
    private String description;
    private String kubernetesVersion;
    private String created;
    private String sshKey;
    private String lastModified;
    private Boolean isActive = false;
    private String status = Constants.ClusterStatus.DISABLED.getInitial();
}