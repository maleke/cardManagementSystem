package com.digipay.cardmanagement.web.rest;

import com.digipay.cardmanagement.dto.CreateCardDto;
import com.digipay.cardmanagement.dto.UserDto;
import com.digipay.cardmanagement.exceptions.ServiceException;
import com.digipay.cardmanagement.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
  private final org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @ApiOperation(value = "(create user) Add user in system. in this service you can initialize a card to user" +
          "or only create a user and later relate cards to this user")
  @PostMapping
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto)
      throws ServiceException {
    logger.debug("REST request to save user : {}", userDto);
    UserDto result = userService.createUser(userDto);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "(add card) Add card to existing user by user id")
  @PostMapping("/cards")
  public ResponseEntity<UserDto> addCardToUser(@Valid @RequestBody CreateCardDto createCardDto)
      throws ServiceException {
    logger.debug(
        "Request to save card : {} for userId {}", createCardDto, createCardDto.getUserId());
    UserDto result = userService.addCardToUser(createCardDto);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
