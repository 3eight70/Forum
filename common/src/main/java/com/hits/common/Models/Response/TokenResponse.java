<<<<<<<< HEAD:user-app/src/main/java/com/hits/user/Models/Dto/Response/TokenResponse.java
package com.hits.user.Models.Dto.Response;
========
package com.hits.common.Models.Response;
>>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d:common/src/main/java/com/hits/common/Models/Response/TokenResponse.java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String token;
}
