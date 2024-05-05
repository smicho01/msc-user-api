package org.semicorp.msc.userapi.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomResponse {
    private String response;
    private int code;
}
