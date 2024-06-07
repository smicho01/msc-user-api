package org.semicorp.msc.userapi.services;


import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class ItemService {


    private final RestTemplate restTemplate;

    @Value("${academi.service.item.url}")
    private String itemServiceUrl;

    public ItemService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Inserts users' college name into `items.college` table in Item Service
     * It does not duplicate college names but returns `HttpStatus.CONFLICT` to indicate
     * that the college entry is already present in the table.
     * @param addUserDTO
     */
    public String insertUsersCollegeIntoDb(AddUserDTO addUserDTO, String token) {
        log.info("Adding user college [{}] into db", addUserDTO.getCollege());
        String collegeId = null;
        // Add college to item schema table
        String itemsEndpointUrl = itemServiceUrl + "/api/v1/college";
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        headers2.setBearerAuth(token.substring(7));
        String payload = "{ \"name\": \"" + addUserDTO.getCollege()  +"\"}";
        HttpEntity<String> request = new HttpEntity<>(payload, headers2);
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(itemsEndpointUrl,
                    HttpMethod.POST, request, Map.class);
            // Intercept collegeId from response
            Map body = exchange.getBody();
            if(body != null) {
                collegeId = (String) body.get("id");
            }
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                log.info("College {} already exists in Item Service database", addUserDTO.getCollege());
                log.error(e.getMessage());
            }
        }
        return collegeId;
    }

}
