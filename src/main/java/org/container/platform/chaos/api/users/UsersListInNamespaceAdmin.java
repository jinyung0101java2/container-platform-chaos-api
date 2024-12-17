package org.container.platform.chaos.api.users;

import lombok.Data;
import org.container.platform.chaos.api.common.Constants;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;

import static org.container.platform.chaos.api.common.Constants.CHECK_N;
import static org.container.platform.chaos.api.common.Constants.CHECK_Y;

/**
 * User List In Namespace Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.15
 **/
@Data
public class UsersListInNamespaceAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<UserDetail> items;

    @Data
    public static class UserDetail {
        private String isAdmin;
        private String userId;
        private String serviceAccountName;
        private String created;

        private String userType;

        public String getIsAdmin() {

            if (userType.equals(Constants.AUTH_NAMESPACE_ADMIN)) {
                isAdmin = CHECK_Y;
            }
            else if (userType.equals(Constants.AUTH_CLUSTER_ADMIN)) {
                isAdmin = CHECK_Y;
            }
            else {
                isAdmin = CHECK_N;
            }

            return isAdmin;
        }

        public void setIsAdmin(String isAdmin) {
            this.isAdmin = isAdmin;
        }

    }


}

