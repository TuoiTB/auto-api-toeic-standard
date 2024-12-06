package api.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateUserInput<T> {
    private String id;
    private String firstName;
    private String lastName;
    private String birthday;
    private String phone;
    private String email;
    private String middleName;
    private List<T> addresses;

    public static UpdateUserInput<UpdateAddressesInput> getDefault(){
        UpdateUserInput<UpdateAddressesInput> userUpdatedInput = new UpdateUserInput<UpdateAddressesInput>();
        userUpdatedInput.setId("654066b6-21e1-446f-9f75-53e206a4c481");
        userUpdatedInput.setFirstName("John edit");
        userUpdatedInput.setLastName("Swith edit");
        userUpdatedInput.setMiddleName("Lacie edit");
        userUpdatedInput.setBirthday("20-09-1998");
        userUpdatedInput.setPhone("0952874185");
        return userUpdatedInput;
    }

    public static UpdateUserInput<UpdateAddressesInput> getDefaultWithEmail(){
        UpdateUserInput<UpdateAddressesInput> userUpdatedInput = getDefault();
        userUpdatedInput.setEmail(String.format("auto_api_edit_%s@test.com", System.currentTimeMillis()));
        return userUpdatedInput;
    }

    @Override
    public String toString(){
        return String.format("User email edit: %s", this.email);
    }
}
