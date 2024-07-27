package org.semicorp.msc.userapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.userapi.domain.message.Message;
import org.semicorp.msc.userapi.domain.message.dao.MessageDAO;
import org.semicorp.msc.userapi.domain.message.dao.MessageRow;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final Jdbi jdbi;

    public List<Message> getMessagesForUser(String userId) {
        try {
            return jdbi.onDemand(MessageDAO.class).findAllForUserId(userId);
        } catch (Exception e) {
            log.error("Error while getting messages for user id {}. ERROR: {}", userId, e.getMessage());
        }
        return null;
    }

    public Message saveMessage(Message message) {
        try {
            boolean response = jdbi.onDemand(MessageDAO.class).insert(new MessageRow(message));
            if(response) {
                return message;
            }
            return null;
        } catch (Exception e) {
            log.error("Error while inserting new message. ERROR: {}", e.getMessage());
        }
        return null;
    }

    public Message getMessage(String id) {
        try {
            return jdbi.onDemand(MessageDAO.class).findById(id);
        } catch (Exception e) {
            log.error("Error while inserting new message. ERROR: {}", e.getMessage());
        }
        return null;
    }

    public Message updateMessage(Message message) {
        try {
            boolean response = jdbi.onDemand(MessageDAO.class).update(new MessageRow(message));
            if(response) {
                log.info("Message id {} updated", message.getId());
                return message;
            }
            return null;
        } catch (Exception e) {
            log.error("Error while updating message id {}. ERROR: {}", message.getId(), e.getMessage());
            return null;
        }
    }

    public Boolean deleteMessage(String messageId) {
        try {
            boolean response = jdbi.onDemand(MessageDAO.class).delete(messageId);
            if(response) {
                log.info("Message id {} deleted", messageId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error while updating message id {}. ERROR: {}", messageId, e.getMessage());
            return false;
        }
    }

    public Boolean updateMessageReadAllFromTo(String fromId, String toId, Boolean read) {
        boolean response = jdbi.onDemand(MessageDAO.class).updateReadAllFromTo(fromId, toId, read);

        return response;
    }
}
