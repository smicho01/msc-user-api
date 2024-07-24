package org.semicorp.msc.userapi.domain.message.dao;

import lombok.*;
import org.semicorp.msc.userapi.domain.message.Message;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRow {

    private String id;
    private String fromId;
    private String toId;
    private String content;
    private LocalDateTime dateCreated;
    private Boolean read;

    public MessageRow(@NonNull final Message message) {
        this.id = message.getId();
        this.fromId = message.getFromId();
        this.toId = message.getToId();
        this.content = message.getContent();
        this.dateCreated = message.getDateCreated();
        this.read = message.getRead();
    }

}
