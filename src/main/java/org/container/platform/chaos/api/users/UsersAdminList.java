package org.container.platform.chaos.api.users;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;

@Data
public class UsersAdminList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private CommonItemMetaData itemMetaData;
    private List<UsersAdmin> items;
}
