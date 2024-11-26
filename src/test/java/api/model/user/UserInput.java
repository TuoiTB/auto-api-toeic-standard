package api.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInput {
    private String id;
    private String firstName;
    private String lastName;
    private String birthday;
    private String phone;
    private String email;
    private String middleName;
    private List<AddressesInput> addresses;
}
