package api.model.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddressesInput {
    private String streetNumber;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String state;
    private String zip;
    private String country;
    public static AddressesInput getDefault(){
        return AddressesInput.builder()
                .streetNumber("136")
                .street("136 Ho Tung Mau Street")
                .ward("Phu Dien")
                .district("Bac Tu Liem")
                .city("Ha Noi")
                .state("Ha Noi")
                .zip("10000")
                .country("VN")
                .build();
    }
}
