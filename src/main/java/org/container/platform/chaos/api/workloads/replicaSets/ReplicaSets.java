package org.container.platform.chaos.api.workloads.replicaSets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.chaos.api.common.CommonUtils;
import org.container.platform.chaos.api.common.model.CommonAnnotations;
import org.container.platform.chaos.api.common.model.CommonMetaData;
import org.container.platform.chaos.api.common.model.CommonSpec;
import org.container.platform.chaos.api.common.model.CommonStatus;

import java.util.List;

/**
 * ReplicaSetsList 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-11-19
 */
@Data
public class ReplicaSets {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String uid;
    private String namespace;
    private Object labels;
    private List<CommonAnnotations> annotations;
    private String creationTimestamp;

    private Object selector;
    private String image;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getUid() {
        return metadata.getUid();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public Object getSelector() {
        return spec.getSelector();
    }

    public String getImage() {
        return spec.getTemplate().getSpec().getContainers().get(0).getImage();
    }
}
