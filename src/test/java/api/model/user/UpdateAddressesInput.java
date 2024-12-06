package api.model.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateAddressesInput {
    private String streetNumber;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String state;
    private String zip;
    private String country;
    public static UpdateAddressesInput getDefault(){
        return UpdateAddressesInput.builder()
                .streetNumber("000")
                .street("000 Ho Tung Mau Street update")
                .ward("Phu Dienupdate")
                .district("Bac Tu Liemupdate")
                .city("update")
                .state("update")
                .zip("10000")
                .country("HQ")
                .build();
    }
}
