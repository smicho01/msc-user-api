package org.semicorp.msc.userapi.domain.message.exchange;

import lombok.*;
import org.semicorp.msc.userapi.domain.message.Message;
import org.semicorp.msc.userapi.domain.user.dto.MessageUserDTO;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class MessageGrouper {

    private MessageUserDTO user;
    private List<Message> messages = new ArrayList<>();
}
