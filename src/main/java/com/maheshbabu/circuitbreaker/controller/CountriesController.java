package com.maheshbabu.circuitbreaker.controller;

import com.maheshbabu.circuitbreaker.service.CountriesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CountriesController {

    private final CountriesService countriesService;

    public CountriesController(CountriesService countriesService) {
        this.countriesService = countriesService;
    }

    @GetMapping("/countries")
    public List<Object> getCountries() {
        return countriesService.getCountries();
    }

}
