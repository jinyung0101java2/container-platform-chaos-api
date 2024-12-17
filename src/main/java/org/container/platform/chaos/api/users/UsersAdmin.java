package org.container.platform.chaos.api.users;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import org.container.platform.chaos.api.common.CommonUtils;
import org.container.platform.chaos.api.common.Constants;

/**
 * User Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.15
 **/

@Data
public class UsersAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    //User Info
    public String userId;
    private String userAuthId;
    public String serviceAccountName;
    public String created;
    public String email;

    // Cluster Info
    public String clusterName;
    public String clusterApiUrl;
    public String clusterToken;
    public String clusterId;


    private CommonItemMetaData itemMetaData;
    private List<UsersDetails> items;

    // Details
    @Data
    public static class UsersDetails {

        private String serviceAccountUid;
        public String isActive;
        public String userId;
        public String cpNamespace;
        public String userType;
        public String roleSetCode;
        public String saSecret;
        public Secrets secrets;

        public String getServiceAccountUid() {
            return CommonUtils.procReplaceNullValue(serviceAccountUid);
        }

        public Secrets getSecrets() {
            return (ObjectUtils.isEmpty(secrets)) ? new UsersAdmin.Secrets(Constants.NULL_REPLACE_TEXT, Constants.NULL_REPLACE_TEXT, Constants.NULL_REPLACE_TEXT) {{
                setSaSecret(Constants.NULL_REPLACE_TEXT);
            }} : secrets;
        }
    }

    // Secrets
    @Data
    @Builder
    public static class Secrets {
        private String saSecret;
        private Object secretLabels;
        private String secretType;

        public Object getSecretLabels() {
            return CommonUtils.procReplaceNullValue(secretLabels);
        }
    }

}
