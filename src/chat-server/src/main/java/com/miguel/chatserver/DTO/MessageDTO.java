package com.miguel.chatserver.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

  @NotNull(message = "Message's sender required")
  private String senderPhoneNumber;

  private LocalDateTime dateTime;

  @NotBlank(message = "Message text required")
  private String messageText;

}
