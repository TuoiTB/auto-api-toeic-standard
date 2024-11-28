package api.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public static UserInput<AddressesInput> getDefaultWithEmail(){
        UserInput<AddressesInput> userInput = getDefault();
        userInput.setEmail(String.format("auto_api_%s@test.com", System.currentTimeMillis()));
        return userInput;
    }

    @Override
    public String toString(){
        return String.format("User email: %s", this.email);
    }
}
