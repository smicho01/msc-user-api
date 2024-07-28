package org.semicorp.msc.userapi.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WalletBalanceResponse {
    private String publicKey;
    private Integer balance;
}