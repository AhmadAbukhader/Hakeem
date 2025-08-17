package com.system.hakeem.Service.UserManagement;

import com.system.hakeem.Dto.UserManagement.password.ResetPasswordRequest;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.UserManagement.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender ;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, VerificationCodeEntry> codes = new ConcurrentHashMap<>();

    public static class VerificationCodeEntry {
        String code;
        LocalDateTime expiresAt ;
        VerificationCodeEntry(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    public void sendMail(String email , String code){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abukhaderahmad818@gmail.com");
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("this is your verification code: " + code);

        System.out.println(email);

        mailSender.send(message);
    }

    public String generateCode(String email){
        String code = UUID.randomUUID().toString().substring(0,6);
        codes.put(email, new VerificationCodeEntry(code, LocalDateTime.now().plusMinutes(10)));
        sendMail(email, code);
        return code;
    }

    public boolean validateCode(String email, String code) {
        VerificationCodeEntry entry = codes.get(email);
        if (entry == null) return false;
        if (entry.expiresAt.isBefore(LocalDateTime.now())) {
            codes.remove(email);
            return false;
        }
        boolean valid = entry.code.equals(code);
        if (valid) codes.remove(email); // remove after successful verification
        return valid;
    }

    public void resetPassword(ResetPasswordRequest request){
        Optional<User> userOptional = userRepository.findByUsername(request.getEmail());
        if (userOptional.isEmpty()) {
            throw new  BadCredentialsException("user not found") ;
        }
        if(!validateCode(request.getEmail(), request.getCode())){
            throw new  BadCredentialsException("code isn't valid") ;
        }
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}