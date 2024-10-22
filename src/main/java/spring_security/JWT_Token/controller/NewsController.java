package spring_security.JWT_Token.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_security.JWT_Token.dto.Country;
import spring_security.JWT_Token.dto.NewsResponse;
import spring_security.JWT_Token.service.NewsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/top-headlines")
    public ResponseEntity<NewsResponse> getTopHeadlines(@RequestParam String country) {
        NewsResponse newsResponse = newsService.getTopHeadlines(country);
        return ResponseEntity.ok(newsResponse);
    }


    @GetMapping("/all")
    @CrossOrigin(origins = "http://localhost:4200")
    public List<Country> getAllCountries() {
        return newsService.getAllCountries();
    }

    @GetMapping("/search")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Country> searchCountry(@RequestParam String name) {
        Optional<Country> country = newsService.getAllCountries()
                .stream().filter(c -> c.getName().getCommon().equalsIgnoreCase(name)).findFirst();

        return country.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
