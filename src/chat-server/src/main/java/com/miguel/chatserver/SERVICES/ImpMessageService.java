package com.miguel.chatserver.SERVICES;

import com.miguel.chatserver.MAPPERS.IMessagesMapper;
import com.miguel.chatserver.MODELS.Chat;
import com.miguel.chatserver.MODELS.Contact;
import com.miguel.chatserver.MODELS.Message;
import com.miguel.chatserver.REPOSITORIES.IMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ImpMessageService implements IMessageService {

  @Autowired
  private IMessageRepository messageRepository;

  @Autowired
  private IMessagesMapper messagesMapper;

  @Autowired
  private IChatsService chatsService;

  @Override
  public Message sendMessage(Chat chat, String messageText) {
    Message message = Message
      .builder()
      .sender(chat.getUser())
      .dateTime(LocalDateTime.now())
      .messageText(messageText)
      .chat(chat)
      .build();

    return messageRepository.save(message);
  }

  @Override
  public Message sendFirstContactMessage(Contact contact, String messageText) {
    Message savedMessage = null;
    Chat savedChat = chatsService.createChatIfNotExists(contact);
    if (Objects.nonNull(savedChat)) {
      savedMessage = this.sendMessage(savedChat, messageText);
      savedChat.getMessages().add(savedMessage);
      chatsService.saveChat(savedChat);
    }
    return savedMessage;
  }
}
