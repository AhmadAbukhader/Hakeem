package com.system.hakeem.Model.UserManagement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "reset_token")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;

    @Column(name = "token")
    private String token ;

    @Column(name = "expiration_time")
    private LocalDateTime expireTime ;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user ;

}
