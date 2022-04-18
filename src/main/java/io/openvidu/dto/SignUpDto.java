package io.openvidu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class SignUpDto {

    private BCryptPasswordEncoder encoder;

    @NotBlank
    @Size(min = 4, max = 8)
    private String username;

    @Size(min = 4, max = 8)
    private String password;

    @Email
    @NotNull
    private String email;

    public SignUpDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void setEncoder(BCryptPasswordEncoder encoder){
        this.encoder = encoder;
    }

    public void encodePassword(){
        this.password = encoder.encode(password);
    }
}
