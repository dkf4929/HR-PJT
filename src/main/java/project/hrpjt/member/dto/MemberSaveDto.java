package project.hrpjt.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSaveDto {
    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;

    @NotEmpty
    private String memberName;

    @NotEmpty
    private String role;

    private String kakaoMail;
}
