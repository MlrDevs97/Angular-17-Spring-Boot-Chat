package com.miguel.chatserver.SERVICES;

import com.miguel.chatserver.DTO.AuthLoginRequest;
import com.miguel.chatserver.DTO.AuthRegisterRequest;
import com.miguel.chatserver.DTO.AuthRegisterResponse;
import com.miguel.chatserver.EXCEPTIONS.ExceptionObjectAlreadyExists;
import com.miguel.chatserver.EXCEPTIONS.ExceptionObjectNotFound;
import com.miguel.chatserver.MODELS.User;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import com.miguel.chatserver.MODELS.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImpAuthenticationService implements IAuthenticationService{

  @Value("${application.mailing.frontend.activation-url}")
  private String activationUrl;

  @Autowired
  private IUserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private IJWTService jwtService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public AuthRegisterResponse register(AuthRegisterRequest request) {
    String phoneNumber = request.getPhoneNumber();
    User userByPhoneNumber = this.userService.findByPhoneNumber(phoneNumber);
    if (Objects.nonNull(userByPhoneNumber)) {
      throw new ExceptionObjectAlreadyExists("Phone Number Already In Use");
    }

    String email = request.getEmail();
    User userByEmail = this.userService.findByEmail(email);
    if (Objects.nonNull(userByEmail)) {
      throw new ExceptionObjectAlreadyExists("Phone Number Already In Use");
    }

    User user = this.userService.createUserFromRegisterRequest(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    try {
      this.userService.registerUser(user);
      return AuthRegisterResponse.builder()
        .success(true)
        .message("User registered successfully")
        .phoneNumber(user.getPhoneNumber())
        .build();
    } catch (Exception ex) {
      throw ex;
    }
  }

  @Override
  public Token login(AuthLoginRequest request) {

    Authentication auth = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getPhoneNumber(),
        request.getPassword()
      )
    );
    if (Objects.isNull(auth)) {
      throw new ExceptionObjectNotFound("Authentication failed. Bad credentials.");
    }

    Map<String, Object> claims = new HashMap<String, Object>();
    User user = ((User) auth.getPrincipal());
    String jwt = jwtService.generateToken(claims, user);

    return Token
      .builder()
      .token(jwt)
      .createdAt(jwtService.getTokenIssuedAt(jwt))
      .expiresAt(jwtService.getTokenExpiration(jwt))
      .user(user)
      .build();
  }
}
