package com.maheshbabu.circuitbreaker.controller;

import com.maheshbabu.circuitbreaker.service.CountriesService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CountriesController {


    private final CountriesService countriesService;

    public CountriesController(CountriesService countriesService) {
        this.countriesService = countriesService;
    }

    @GetMapping("/countries")
    @CircuitBreaker(name = "countriesCircuitBreaker", fallbackMethod = "getCountries")
    public List<Object> getCountries() throws Exception {
        return countriesService.getCountries();
    }

    public List<Object> getCountries(Throwable throwable) {
        List<Object> countries = new ArrayList<>();
        countries.add("Country service unavailable!");
        return countries;
    }

}
