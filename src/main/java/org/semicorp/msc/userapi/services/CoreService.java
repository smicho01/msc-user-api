package org.semicorp.msc.userapi.services;

import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.User;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Slf4j
public class CoreService {

    private final RestTemplate restTemplate;

    @Value("${academi.service.core.url}")
    private String coreServiceUrl;

    public CoreService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public WalletEncryptedDTO generateBlockchainWalletKeys(String token) {
        log.info("Attempt to generate blockchain wallet keys");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.substring(7));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = coreServiceUrl + "/api/v1/wallet/create";
            ResponseEntity<WalletEncryptedDTO> response = restTemplate.exchange(
                            url, HttpMethod.GET, entity, WalletEncryptedDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error while generating wallet keys");
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

}