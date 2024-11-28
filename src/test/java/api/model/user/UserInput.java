package api.model.user;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserInput<T> {
    private String id;
    private String firstName;
    private String lastName;
    private String birthday;
    private String phone;
    private String email;
    private String middleName;
    private List<T> addresses;

    public static UserInput<AddressesInput> getDefault(){
        UserInput<AddressesInput> userInput = new UserInput<AddressesInput>();
        userInput.setId("155966c5-21e1-446f-9f75-53e206a4c496");
        userInput.setFirstName("John");
        userInput.setLastName("Dow");
        userInput.setMiddleName("Smith");
        userInput.setBirthday("20-09-1999");
        userInput.setPhone("0965187439");
        return userInput;
    }
}
