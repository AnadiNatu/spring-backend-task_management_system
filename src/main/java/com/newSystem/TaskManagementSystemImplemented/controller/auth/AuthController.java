
package com.newSystem.TaskManagementSystemImplemented.controller.auth;

import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.LoginRequest;
import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.LoginResponse;
import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.SignUpRequest;
import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.UsersDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import com.newSystem.TaskManagementSystemImplemented.repository.UserRepository;
import com.newSystem.TaskManagementSystemImplemented.security.JwtUtils;
import com.newSystem.TaskManagementSystemImplemented.security.UserDetailService;
import com.newSystem.TaskManagementSystemImplemented.service.authService.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup/employer")
    public ResponseEntity<?> signupEmployer(@RequestBody SignUpRequest request){
        if (authService.hasUserWithUsername(request.getUsername())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(" ⚠️ User already exists with this email " + request);
        }

        UsersDTO usersDTO = authService.signupEmployer(request);

        if (usersDTO == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usersDTO);
    }

    @PostMapping("/signup/employee")
    public ResponseEntity<?> signupEmployee(@RequestBody SignUpRequest request){
        if (authService.hasUserWithUsername(request.getUsername())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("⚠️ User Already exists with this email " + request);
        }

        UsersDTO usersDTO = authService.signupEmployee(request);

        if (usersDTO == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usersDTO);
    }

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername() , request.getPassword()));
        }catch(BadCredentialsException ex){
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(" Incorrect username or password");
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());
        Optional<Users> users = userRepository.findUserByUsername(request.getUsername());

        String jwtToken = jwtUtil.generateToken(users.get().getUsername());

        LoginResponse response = new LoginResponse();
        if (users.isPresent()){
            response.setId(users.get().getId());
            response.setJwt(jwtToken);
            response.setUserRoles(users.get().getUserRoles());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/password/reset-request")
    public ResponseEntity<?> sendResetLink(@RequestParam String email){

        try {
            authService.sendResetToken(email);
            return ResponseEntity.ok("Reset link is sent successfully to: " + email);
        }catch (UsernameNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" Wrong " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" Failed to send reset link . " + ex.getMessage());
        }
    }

    @GetMapping("/password/validate-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token , @RequestParam String email){
        boolean isValid = authService.validateResetToken(token, email);

        if (isValid){
            return ResponseEntity.ok(" Token is valid");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(" Invalid or expired token");
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestParam String email , @RequestParam String token , @RequestParam String newPassword){
      try {
          String message = authService.resetPassword(email, token, newPassword);
          return ResponseEntity.ok("Correct" + message);
      }catch (IllegalArgumentException ex){
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong" + ex.getMessage());
      }catch (Exception ex){
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" Failed to reset password" + ex.getMessage());
      }
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<UsersDTO>> getAllUser(){
        List<UsersDTO> userList = authService.getAllTheUser();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userList);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UsersDTO> getUserById(@PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.getUserById(id));
    }

    @PostMapping("/upload-profile-image/{id}")
    public ResponseEntity<String> uploadProfileImage(@PathVariable("id") Long userId,
                                                     @RequestParam("file") MultipartFile file) {
        try {
            String message = authService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-profile-image/{id}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable("id") Long userId) {
        byte[] imageData = authService.getUserProfileImage(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // or detect based on content
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

}
