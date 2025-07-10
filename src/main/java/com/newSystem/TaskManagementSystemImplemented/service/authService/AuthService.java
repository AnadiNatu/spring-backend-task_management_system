package com.newSystem.TaskManagementSystemImplemented.service.authService;


import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.SignUpRequest;
import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.UsersDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import com.newSystem.TaskManagementSystemImplemented.mapper.TaskUserNewMapper;
import com.newSystem.TaskManagementSystemImplemented.repository.UserRepository;
import com.newSystem.TaskManagementSystemImplemented.security.JwtUtils;
import com.newSystem.TaskManagementSystemImplemented.security.UserDetailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskUserNewMapper mapper;

    @PostConstruct
    public void createAdminAccount(){

        Optional<Users> optionalUser = userRepository.findByUserRoles(UserRoles.ADMIN);

        if (optionalUser.isEmpty()){

            Users users = new Users();
            users.setUsername("admin@test.com");
            users.setName("Admin");
            users.setPassword(new BCryptPasswordEncoder().encode("admin"));
            users.setAge(100);
            users.setDepartment("All");
            users.setUserRoles(UserRoles.ADMIN);
            users.setTaskAssignment(false);
            users.setCompleteTask(0);
            userRepository.save(users);

            System.out.println("Admin is created successfully");
        }
        else {
            System.out.println("Admin already created");
        }
    }

    public void sendResetToken(String email){

        Users user = userRepository.findUserByUsername(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        String resetToken = jwtUtil.generateToken(email);

        user.setResetToken(resetToken);
        userRepository.save(user);

        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
        emailService.sendEmail(email ,"Password Reset" , "Click the link to reset your password : " + resetLink);
    }

    public boolean validateResetToken(String token , String email){

        String extractedEmail = jwtUtil.extractUsername(token);

        if (!extractedEmail.equals(email)){
            return false;
        }
        UserDetails userDetails = userDetailService.loadUserByUsername(email);

        return jwtUtil.isTokenValid(token , userDetails);
    }

    public String resetPassword(String email , String token , String newPassword){

        if (!validateResetToken(token, email)){
            throw new IllegalArgumentException("Invalid or expired token");
        }

        Users user = userRepository.findUserByUsername(email).orElseThrow(() -> new UsernameNotFoundException("User not Found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password reset successful";
    }

    public UsersDTO signupEmployee(SignUpRequest signUpRequest){

        Users users = new Users();

        users.setUsername(signUpRequest.getUsername());
        users.setName(signUpRequest.getName());
        users.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        users.setUserRoles(UserRoles.EMPLOYEE);
        users.setAge(signUpRequest.getAge());
        users.setDepartment(signUpRequest.getDepartment());
        users.setTaskAssignment(false);
        users.setCompleteTask(0);

        Users createdUsers = userRepository.save(users);
        return mapper.getUsersDTO(createdUsers);

    }

    public UsersDTO signupEmployer(SignUpRequest signUpRequest){

        Users users = new Users();

        users.setUsername(signUpRequest.getUsername());
        users.setName(signUpRequest.getName());
        users.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        users.setUserRoles(UserRoles.EMPLOYER);
        users.setAge(signUpRequest.getAge());
        users.setDepartment(signUpRequest.getDepartment());
        users.setTaskAssignment(false);
        users.setCompleteTask(0);

        Users createdUsers = userRepository.save(users);
        return mapper.getUsersDTO(createdUsers);
    }

    public boolean hasUserWithUsername(String username){
        return userRepository.findUserByUsername(username).isPresent();

    }
}
