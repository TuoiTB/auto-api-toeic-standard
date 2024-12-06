package api.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetUserUpdateResponse<T> extends UpdateUserInput<T>{
    private String createdAt;
    private String updatedAt;
}
