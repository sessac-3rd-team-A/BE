package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String token; // jwt 저장 공간
    private String userId;
    private String password;
    private String nickname;
    private String age;
    private char gender;
}
