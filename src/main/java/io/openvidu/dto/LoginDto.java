package io.openvidu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class LoginDto {

    @NotBlank
    @Email
    @Size(min = 4, max = 12)
    private String email;

    @NotBlank
    @Size(min = 4, max = 12)
    private String password;

}
