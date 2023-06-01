package com.maheshbabu.circuitbreaker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CountriesService {

    private final RestTemplate restTemplate;

    public CountriesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Object> getCountries() throws Exception {
        Object[] countries = null;
        try {
            countries = restTemplate.getForObject("https://restcountries.com/v3.1/all", Object[].class);
        } catch (Exception e) {
            throw new Exception("Failed to fetch countries from the API");
        }
        return Arrays.stream(countries).toList().subList(1, 10);
    }
}
