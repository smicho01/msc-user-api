package org.semicorp.msc.userapi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CoreServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CoreService coreService;

    @BeforeEach
    public void setUp() {
        coreService = new CoreService(restTemplate);
    }

    @Test
    void testGenerateBlockchainWalletKeys_Success() {
        String token = "Bearer token123";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token123");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        WalletEncryptedDTO mockResponse = new WalletEncryptedDTO("pubKey", "privKey", 10);
        ResponseEntity<WalletEncryptedDTO> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                eq(entity),
                eq(WalletEncryptedDTO.class)))
                .thenReturn(responseEntity);

        WalletEncryptedDTO result = coreService.generateBlockchainWalletKeys(token);

        assertNotNull(result);
        assertEquals("pubKey", result.getPublicKeyEncrypted());
        assertEquals("privKey", result.getPrivateKeyEncrypted());
        assertEquals(10, result.getBalance());
    }

    @Test
    void testGenerateBlockchainWalletKeys_Failure() {
        String token = "Bearer token123";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token123");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                eq(entity),
                eq(WalletEncryptedDTO.class)))
                .thenThrow(new RuntimeException("Mock exception"));

        WalletEncryptedDTO result = coreService.generateBlockchainWalletKeys(token);

        assertNull(result);
    }
}
