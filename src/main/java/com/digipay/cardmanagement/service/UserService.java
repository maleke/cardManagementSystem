package com.digipay.cardmanagement.service;

import com.digipay.cardmanagement.dto.CreateCardDto;
import com.digipay.cardmanagement.dto.UserDto;
import com.digipay.cardmanagement.entity.Card;
import com.digipay.cardmanagement.entity.User;
import com.digipay.cardmanagement.exceptions.ServiceException;
import com.digipay.cardmanagement.exceptions.error.ErrorCode;
import com.digipay.cardmanagement.exceptions.error.FieldErrorDTO;
import com.digipay.cardmanagement.mapper.CardMapper;
import com.digipay.cardmanagement.mapper.UserMapper;
import com.digipay.cardmanagement.repository.UserRepository;
import com.digipay.cardmanagement.web.rest.UserController;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public UserService(UserMapper userMapper, UserRepository userRepository, CardMapper cardMapper) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
    }

    public UserDto createUser(UserDto userDto) throws ServiceException {
        logger.info(" Request to save user base on {}", userDto);
        User user = userMapper.userDtoToUser(userDto);
        setParent(user.getCards(), user);
        return userMapper.userToUserDto(user);
    }

    private void setParent(List<Card> cards, User user) throws ServiceException {
        if (cards == null)
            return;
        for (Card card : cards)
            card.setUser(user);
        user.setCards(cards);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(new FieldErrorDTO().setErrorDescription("This name is already exist")
                    .setErrorCode(String.valueOf(ErrorCode.DUPLICATE_DATA.getCode())));
        }
    }

    public UserDto addCardToUser(CreateCardDto createCardDto) throws ServiceException {
        Optional<User> foundedUser = userRepository.findById(createCardDto.getUserId());
        List<Card> cardList = new ArrayList<>();
        if(foundedUser.isPresent()){
            User user = foundedUser.get();
            cardList.addAll(user.getCards());
            cardList.addAll(cardMapper.cardDtosToCards(createCardDto.getCardDtos()));
            user.setCards(cardList);
            return userMapper.userToUserDto(userRepository.save(user));
        }
        throw new ServiceException(new FieldErrorDTO().setErrorDescription(ErrorCode.USER_NOT_EXIST.getMessage())
                .setErrorCode(String.valueOf(ErrorCode.USER_NOT_EXIST.getCode())));
    }
}
