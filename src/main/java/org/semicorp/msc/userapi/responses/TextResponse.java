package org.semicorp.msc.userapi.responses;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class TextResponse implements BasicResponse {
    private String response;
    private int code;
}
