package io.openvidu.dto;

import io.openvidu.entity.User;
import io.openvidu.java.client.OpenViduRole;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String picture;
    private OpenViduRole openViduRole;

    public SessionUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.picture = user.getPicture();
        this.openViduRole = user.getOpenViduRole();
    }
}
