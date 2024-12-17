package org.container.platform.chaos.api.users;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.container.platform.chaos.api.users.support.NamespaceRole;
import org.springframework.util.ObjectUtils;
import org.container.platform.chaos.api.common.CommonUtils;


/**
 * User Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/

@Data
public class Users {
    public String resultCode;
    public String resultMessage;
    public Integer httpStatusCode;
    public String detailMessage;

    public long id;
    public String clusterId;
    public String userId;
    public String userAuthId;
    public String password;
    public String passwordConfirm;
    public String email;
    public String clusterName;
    public String clusterProviderType;
    public String clusterApiUrl;
    public String clusterToken;
    public String cpNamespace;
    public String cpAccountTokenName;
    public String serviceAccountName;
    public String saSecret;
    public String saToken;
    public String isActive;
    public String roleSetCode;
    public String description;
    public String userType;
    public String created;
    public String lastModified;

    private String browser;
    private String clientIp;

    // user 생성 시 multi namespaces, roles
    private List<NamespaceRole> selectValues;

    private String cpProviderType;
    private String serviceInstanceId;


    ///secret info
    private String secretName;
    private String secretUid;
    private String secretCreationTimestamp;



    public String getUserId() {
        return CommonUtils.procReplaceNullValue(userId);
    }

    public String getUserAuthId() {
        return CommonUtils.procReplaceNullValue(userAuthId);
    }

    public String getPassword() {
        return CommonUtils.procReplaceNullValue(password);
    }

    public String getPasswordConfirm() {
        return CommonUtils.procReplaceNullValue(passwordConfirm);
    }

    public String getEmail() {
        return CommonUtils.procReplaceNullValue(email);
    }

    public String getClusterName() {
        return CommonUtils.procReplaceNullValue(clusterName);
    }

    public String getClusterApiUrl() {
        return CommonUtils.procReplaceNullValue(clusterApiUrl);
    }

    public String getClusterToken() {
        return CommonUtils.procReplaceNullValue(clusterToken);
    }

    public String getCpNamespace() {
        return CommonUtils.procReplaceNullValue(cpNamespace);
    }

    public String getServiceAccountName() {
        return CommonUtils.procReplaceNullValue(serviceAccountName);
    }

    public String getSaSecret() {
        return CommonUtils.procReplaceNullValue(saSecret);
    }

    public String getSaToken() {
        return CommonUtils.procReplaceNullValue(saToken);
    }

    public String getRoleSetCode() {
        return CommonUtils.procReplaceNullValue(roleSetCode);
    }

    public String getCpAccountTokenName() {
        return CommonUtils.procReplaceNullValue(cpAccountTokenName);
    }

    public String getDescription() {
        return CommonUtils.procReplaceNullValue(description);
    }

    public String getBrowser() {
        return CommonUtils.procReplaceNullValue(browser);
    }

    public String getClientIp() {
        return CommonUtils.procReplaceNullValue(clientIp);
    }

    public String getCpProviderType() {
        return CommonUtils.procReplaceNullValue(cpProviderType);
    }

    public String getServiceInstanceId() {
        return CommonUtils.procReplaceNullValue(serviceInstanceId);
    }

    public String getUserType() {
        return CommonUtils.procReplaceNullValue(userType);
    }


    public String getSecretName() { return CommonUtils.procReplaceNullValue(secretName); }

    public String getSecretUid() { return CommonUtils.procReplaceNullValue(secretUid); }

    public String getSecretCreationTimestamp() { return CommonUtils.procReplaceNullValue(secretCreationTimestamp); }

    public List<NamespaceRole> getSelectValues() {
        return (ObjectUtils.isEmpty(selectValues)) ? new ArrayList<NamespaceRole>() : selectValues;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", clusterId='" + clusterId + '\'' +
                ", userId='" + userId + '\'' +
                ", userAuthId='" + userAuthId + '\'' +
                ", namespace='" + cpNamespace + '\'' +
                ", userType='" + userType + '\'' +
                ", serviceAccountName='" + serviceAccountName + '\'' +
                ", roleSetCode='" + roleSetCode + '\'' +
                ", created='" + created + '\'' +
                '}';
    }

    public Users(String cluterId, String namespace, String userId, String userAuthId, String userType, String roleSetCode, String serviceAccountName, String saSecret,
                 String saToken) {
        this.clusterId = cluterId;
        this.cpNamespace = namespace;
        this.userId = userId;
        this.userAuthId = userAuthId;
        this.userType = userType;
        this.roleSetCode = roleSetCode;
        this.serviceAccountName = serviceAccountName;
        this.saSecret = saSecret;
        this.saToken = saToken;
    }

    public Users(String cluterId, String namespace, String userId, String userAuthId, String userType, String roleSetCode, String serviceAccountName,  String saSecret) {
        this.clusterId = cluterId;
        this.cpNamespace = namespace;
        this.userId = userId;
        this.userAuthId = userAuthId;
        this.userType = userType;
        this.roleSetCode = roleSetCode;
        this.serviceAccountName = serviceAccountName;
        this.saSecret = saSecret;
    }

    public Users() {};
    public Users(String cpNamespace) {
        this.cpNamespace = cpNamespace;
    }
}