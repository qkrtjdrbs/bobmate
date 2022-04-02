package io.openvidu.entity;

import io.openvidu.java.client.OpenViduRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String password;
    private String email;
    private String picture;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private OpenViduRole openViduRole;

    public User(String username, String email, String picture, Role role) {
        this.username = username;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.openViduRole = OpenViduRole.PUBLISHER;
    }

    public User update(String username, String picture) {
        this.username = username;
        this.picture = picture;
        return this;
    }

    public void changePassword(String password){
        this.password = password;
    }
}
