package spring_security.JWT_Token.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring_security.JWT_Token.dto.Country;
import spring_security.JWT_Token.dto.NewsResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class NewsService {

    private final String BASE_URL = "https://newsapi.org/v2/top-headlines";

    private final String BASE_URL_DATA = "https://restcountries.com/v3.1/all";

    @Value("${news.api.key}")
    private String apiKey; // Use application.properties to store the key

    public NewsResponse getTopHeadlines(String country) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?country=%s&apiKey=%s", BASE_URL, country, apiKey);
        return restTemplate.getForObject(url, NewsResponse.class);
    }


    public List<Country> getAllCountries() {
        RestTemplate restTemplate = new RestTemplate();
        Country[] countries = restTemplate.getForObject(BASE_URL_DATA, Country[].class);

        if (countries == null) {
            return Collections.emptyList(); // Handle null response
        }

        return Arrays.asList(countries);
    }

}
