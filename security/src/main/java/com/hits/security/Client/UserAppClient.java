package com.hits.security.Client;

import com.hits.common.Models.User.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.hits.common.Consts.GET_USER;
import static com.hits.common.Consts.VALIDATE_TOKEN;

@FeignClient(name = "USER-SERVICE")
public interface UserAppClient {
    @GetMapping(VALIDATE_TOKEN)
    Boolean validateToken(@RequestParam("token") String token);

    @GetMapping(GET_USER)
    UserDto getUser(@RequestParam("login") String login);
}
