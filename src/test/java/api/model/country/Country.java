package api.model.country;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Country {
    private String name;
    private String code;
    /*public Country() {
    }  thay thế bằng annotation của lombok: @NoArgsConstructor */


   /* public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }* @AllArgsConstructor giúp generate ra constructor/
    */



    /*    public String getName() {
        return name;
    }

    public Country setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Country setCode(String code) {
        this.code = code;
        return this;
    }*/
}
