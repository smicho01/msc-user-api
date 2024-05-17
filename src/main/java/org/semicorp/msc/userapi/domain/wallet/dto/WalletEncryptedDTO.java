package org.semicorp.msc.userapi.domain.wallet.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WalletEncryptedDTO {

    private String publicKeyEncrypted; // encrypted and encoded to base64 string
    private String privateKeyEncrypted; // encrypted and encoded to base64 string
    private int balance;
}
