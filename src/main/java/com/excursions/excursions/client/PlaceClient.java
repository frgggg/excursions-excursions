package com.excursions.excursions.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "PlacesClient", url = "${excursions-places.url}")
public interface PlaceClient {

    @GetMapping(value = "${excursions-places.api-check}", produces = "application/json")
    @Headers("Content-Type: application/json")
    @ResponseBody
    List<Long> check(@RequestParam(name = "places-ids-for-check") List<Long> placesIds);
}
