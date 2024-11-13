package api.model.country;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CountryCustomizedModel {
    private String name;
    private String code;
    private float gdp;
    @JsonProperty("private")
    private int fieldPrivate;
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
