package org.semicorp.msc.userapi.domain.message;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Message {

    private String id;
    private String fromId;
    private String toId;
    private String content;
    private LocalDateTime dateCreated;
    private Boolean read;

}
