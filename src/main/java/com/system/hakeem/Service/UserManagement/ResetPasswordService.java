package com.system.hakeem.Service.UserManagement;

import com.system.hakeem.Model.UserManagement.ResetCode;
import com.system.hakeem.Model.UserManagement.ResetToken;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.UserManagement.ResetCodeRepository;
import com.system.hakeem.Repository.UserManagement.ResetTokenRepository;
import com.system.hakeem.Repository.UserManagement.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final ResetTokenRepository resetTokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender ;
    private final PasswordEncoder passwordEncoder;
    private final ResetCodeRepository resetCodeRepository;

    public void sendMail(String email , String token){
        String linkReset = "http://localhost:4200/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abukhaderahmad818@gmail.com");
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Reset Password Link " + linkReset);

        System.out.println(linkReset);
        System.out.println(email);

        mailSender.send(message);
    }

    public void sendCode (String email){
        Optional<User> userOptional = userRepository.findByUsername(email);
        if(userOptional.isEmpty()){
            System.out.println("User not found");
            return;
        }
        User user = userOptional.get();

        resetTokenRepository.deleteByUser(user);

        int code = new Random().nextInt(900000);
        ResetCode resetCode = ResetCode
                .builder()
                .code(code)
                .expireTime(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        resetCodeRepository.save(resetCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abukhaderahmad818@gmail.com");
        message.setTo(email);
        message.setSubject("reset password");
        message.setText("this is you verification code " + code);

        mailSender.send(message);

    }

    @Transactional
    public void initiateResetPassword(String email , int code ){
        Optional<ResetCode> codeOptional = resetCodeRepository.findByCode(code);
        if(codeOptional.isEmpty()){
            System.out.println("invalid code");
        }

        ResetCode resetCode = codeOptional.get();

        if (resetCode.getExpireTime().isBefore(LocalDateTime.now())){
            resetCodeRepository.delete(resetCode);
            throw new RuntimeException("Reset token expired");
        }

        User user = resetCode.getUser();
        resetCodeRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = ResetToken.builder()
                .token(token)
                .expireTime(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        resetTokenRepository.save(resetToken);

        sendMail(email, token);
    }

    public void resetPassword(String newPassword, String token){
        Optional<ResetToken> tokenOptional = resetTokenRepository.findByToken(token);
        if(tokenOptional.isEmpty()){
            System.out.println("invalid token");
        }

        ResetToken resetToken = tokenOptional.get();

        System.out.println(resetToken.getExpireTime());
        System.out.println(LocalDateTime.now());


        if (resetToken.getExpireTime().isBefore(LocalDateTime.now())){
            resetTokenRepository.delete(resetToken);
            throw new RuntimeException("Reset token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }

}