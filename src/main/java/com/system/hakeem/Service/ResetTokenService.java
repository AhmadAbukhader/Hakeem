package com.system.hakeem.Service;

import com.system.hakeem.Model.ResetToken;
import com.system.hakeem.Model.User;
import com.system.hakeem.Repository.ResetTokenRepository;
import com.system.hakeem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetTokenService {

    @Autowired
    private ResetTokenRepository resetTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender ;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void sendMail(String email , String token){
        String linkReset = "http://localhost:4200/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abukhaderahmad818@gmail.com");
        message.setTo(email);
        message.setSubject("reset password");
        message.setSubject("Reset Password");
        message.setText("Reset Password Link " + linkReset);

        System.out.println(linkReset);
        System.out.println(email);

        mailSender.send(message);
    }

    public void initiateResetPassword(String email){
        Optional<User> userOptional = userRepository.findByUsername(email);
        if(userOptional.isEmpty()){
            System.out.println("User not found");
            return;
        }
        User user = userOptional.get();

        resetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = ResetToken.builder()
                .token(token)
                .expireTime(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        System.out.println(resetToken.getExpireTime());

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