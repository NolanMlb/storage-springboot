package com.nextu.storage.controllers;

import com.nextu.storage.dto.UserCreateDTO;
import com.nextu.storage.dto.UserGetDTO;
import com.nextu.storage.dto.WelcomeEmailDTO;
import com.nextu.storage.entities.User;
import com.nextu.storage.exceptions.UserContentException;
import com.nextu.storage.payloads.LoginRequest;
import com.nextu.storage.payloads.RefreshTokenRequest;
import com.nextu.storage.payloads.response.JwtResponse;
import com.nextu.storage.services.UserDetailsImpl;
import com.nextu.storage.services.UserService;
import com.nextu.storage.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/",produces = { "application/json", "application/xml" })
    public ResponseEntity<UserGetDTO> create(@RequestBody UserCreateDTO userCreateDTO) throws UserContentException {
        if (userService.checkIfUserAlreadyExist(userCreateDTO.getLogin())){
            throw new UserContentException("User with login "+userCreateDTO.getLogin()+" already exist");
        }
        User user = new User();
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setLogin(userCreateDTO.getLogin());
        user.setPassword(encoder.encode(userCreateDTO.getPassword()));
        // Call Symfony API
        String symfonyUrl = "http://storageapp.com/mailer/mail/welcome";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            logger.info("Sending request to Symfony: " + symfonyUrl);
            // Create a JSON object with the parameters
            WelcomeEmailDTO welcomeEmailDTO = new WelcomeEmailDTO(userCreateDTO.getLogin(), userCreateDTO.getFirstName());
            String jsonParams = objectMapper.writeValueAsString(welcomeEmailDTO);
            logger.info("Parameters" + jsonParams);
            // Send the request
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonParams, headers);
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(symfonyUrl, requestEntity, Object.class);
            logger.info("Response from Symfony: " + responseEntity);
            // Get the response
            Object response = responseEntity.getBody();
            System.out.println(response);
        } catch (Exception e) {
            logger.error("Error while sending email: " + e.getMessage(), e);
        }
        return ResponseEntity.ok(userService.create(user));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        // Get refresh token from request
        String refreshToken = request.getRefreshToken();
        if (!jwtUtils.validateRefreshJwtToken(refreshToken)) {
            return ResponseEntity.badRequest().body("Refresh token is incorrect!");
        }
        String username = jwtUtils.getUserNameFromRefreshToken(refreshToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        // Create a authentication object
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Generate a new jwt token
        String newJwtToken = jwtUtils.generateJwtToken(authentication);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // return the token
        return ResponseEntity.ok(new JwtResponse(newJwtToken,
                refreshToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFirstName(),
                roles));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshJwt = jwtUtils.generateRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                refreshJwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFirstName(),
                roles));
    }

    @PostMapping(value = "/logout",produces = { "application/json", "application/xml" })
    public ResponseEntity<?> disconnectUser(HttpServletRequest request){
        request.removeAttribute("Authorization");
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("User disconnected");
    }

    @PatchMapping(value = "/{id}",produces = { "application/json", "application/xml" })
    public ResponseEntity<UserGetDTO> update(@PathVariable String id, @RequestBody UserCreateDTO userCreateDTO){
        User user = new User();
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(userCreateDTO.getPassword());
        return ResponseEntity.ok(userService.create(user));
    }
    @GetMapping(value = "/{id}",produces = { "application/json", "application/xml" })
    public ResponseEntity<UserGetDTO> find(@PathVariable String id){
        UserGetDTO userGetDTO = userService.findById(id);
        if(userGetDTO!=null){
            return ResponseEntity.ok(userGetDTO);
        }
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
