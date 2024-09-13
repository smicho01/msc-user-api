package org.semicorp.msc.userapi.domain.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.message.exchange.MessageGrouper;
import org.semicorp.msc.userapi.domain.message.exchange.UserPair;
import org.semicorp.msc.userapi.domain.user.User;
import org.semicorp.msc.userapi.domain.user.UserMapper;
import org.semicorp.msc.userapi.domain.user.dto.MessageUserDTO;
import org.semicorp.msc.userapi.services.MessageService;
import org.semicorp.msc.userapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/message")
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, MessageGrouper>> getAllUserMessages(@PathVariable(value = "userId") String userId) {
        log.info("Get all messages for user id: {}", userId);
        try {
            List<Message> messagesForUser = messageService.getMessagesForUser(userId);
            Map<String, MessageGrouper> groupedMessages = getStringMessageGrouperMap(userId, messagesForUser);
            return new ResponseEntity<>(groupedMessages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> saveMessage(@RequestBody Message message) {
        log.info("Saving message between {} and {}", message.getFromId(), message.getToId());
        message.setId(UUID.randomUUID().toString());
        try {
            Message savedMessage = messageService.saveMessage(message);
            if(savedMessage != null) {
                return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMessage(@PathVariable String messageId) {
        log.info("Deleting message between id {}", messageId);
        try {
            boolean response = messageService.deleteMessage(messageId);
            if (response) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> updateMessage(@PathVariable String messageId, @RequestBody Message messageDetails) {
        log.info("Updating message between {} and {}", messageDetails.getFromId(), messageDetails.getToId());
        try {
            Message messageData = messageService.getMessage(messageId);
            if (messageData != null) {
                if (messageDetails.getFromId() != null) {
                    messageData.setFromId(messageDetails.getFromId());
                }
                if (messageDetails.getToId() != null) {
                    messageData.setToId(messageDetails.getToId());
                }
                if (messageDetails.getContent() != null) {
                    messageData.setContent(messageDetails.getContent());
                }
                if (messageDetails.getDateCreated() != null) {
                    messageData.setDateCreated(messageDetails.getDateCreated());
                }
                if (messageDetails.getRead() != null) {
                    messageData.setRead(messageDetails.getRead());
                }

                Message savedMessage = messageService.updateMessage(messageData);
                log.info("Message updated");
                return new ResponseEntity<>(savedMessage, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/updatereadall/{fromId}/{toId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateMessageRead(@PathVariable(name = "fromId") String fromId,
                                                     @PathVariable(name = "toId") String toId) {
        log.info("Updating all messages to read from id {} to id {}", fromId, toId);
        Boolean response = messageService.updateMessageReadAllFromTo(fromId, toId, true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
     *     Method is grouping messages between two users, so that they can be later nicely displayed in UI
     */
    private Map<String, MessageGrouper> getStringMessageGrouperMap(String userId, List<Message> messagesForUser) {
        Map<String, MessageGrouper> groupedMessages = new HashMap<>();
        Map<String, List<Message>> messagesWithUser = new HashMap<>();
        Set<String> withUserIdsSet = new HashSet<>(); // Store IDs of user that already has been pulled from the DB to save resources and lower API and DB req.

        for (Message msg : messagesForUser) {
            UserPair userPair = new UserPair(msg.getFromId(), msg.getToId());
            String withUserId = userPair.getUser1().equals(userId) ? userPair.getUser2() : userPair.getUser1(); // select Id of the other user than userId ("conversation with")
            User withUser = null;
            MessageUserDTO messageUserDTO = null;
            if (!withUserIdsSet.contains(withUserId)) {
                withUserIdsSet.add(withUserId);
                withUser = userService.getUserByField("id", withUserId);
                messageUserDTO = UserMapper.userToMessageUser(withUser);
                log.info("Has message[s] with user id: {}", withUserId);
            }
            messagesWithUser.computeIfAbsent(withUserId, k -> new ArrayList<>()).add(msg);
            groupedMessages.computeIfAbsent(withUserId, k -> new MessageGrouper()).getMessages().add(msg);
            if (withUser != null) {
                groupedMessages.computeIfAbsent(withUserId, k -> new MessageGrouper()).setUser(messageUserDTO);
            }

        }
        return groupedMessages;
    }

}
