package api.data;

public class GetCountriesData {
    public static final String ALL_COUNTRIES = """
[{"name":"Viet Nam","code":"VN"},{"name":"USA","code":"US"},{"name":"Canada","code":"CA"},{"name":"UK","code":"GB"},{"name":"France","code":"FR"},{"name":"Japan","code":"JP"},{"name":"India","code":"IN"},{"name":"China","code":"CN"},{"name":"Brazil","code":"BR"}]
""";
    public static final String ALL_COUNTRIES_WITH_GDP = """
             [
                 {
                     "name": "Viet Nam",
                     "code": "VN",
                     "gdp": 223.9
                 },
                 {
                     "name": "USA",
                     "code": "US",
                     "gdp": 21427.5
                 },
                 {
                     "name": "Canada",
                     "code": "CA",
                     "gdp": 1930
                 },
                 {
                     "name": "UK",
                     "code": "GB",
                     "gdp": 2827
                 },
                 {
                     "name": "France",
                     "code": "FR",
                     "gdp": 2718
                 },
                 {
                     "name": "Japan",
                     "code": "JP",
                     "gdp": 5081
                 },
                 {
                     "name": "India",
                     "code": "IN",
                     "gdp": 2875
                 },
                 {
                     "name": "China",
                     "code": "CN",
                     "gdp": 14342.9
                 },
                 {
                     "name": "Brazil",
                     "code": "BR",
                     "gdp": 1868
                 }
            ]
            """;
    public static final String COUNTRY_BY_CODE = """
            {
                "name": "Viet Nam",
                "code": "VN"
            }
            """;
}