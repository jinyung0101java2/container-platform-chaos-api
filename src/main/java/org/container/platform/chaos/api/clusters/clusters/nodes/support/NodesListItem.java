package org.container.platform.chaos.api.clusters.clusters.nodes.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.chaos.api.common.CommonUtils;
import org.container.platform.chaos.api.common.Constants;
import org.container.platform.chaos.api.common.model.CommonCondition;
import org.container.platform.chaos.api.common.model.CommonMetaData;
import org.container.platform.chaos.api.common.model.CommonStatus;
import org.container.platform.chaos.api.metrics.custom.Quantity;

import java.util.List;
import java.util.Map;


@Data
public class NodesListItem {
    private String name;
    private Object labels;
    private String ready;
    private String creationTimestamp;
    private Map<String, Quantity> usage;

    private String clusterId;
    private String clusterName;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return name = metadata.getName();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getReady() {
        List<CommonCondition> conditions = status.getConditions();
        for (CommonCondition c : conditions) {
            if (c.getType().equals(Constants.STRING_CONDITION_READY)) {
                ready = c.getStatus();
            }
        }

        return ready;
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }
}
