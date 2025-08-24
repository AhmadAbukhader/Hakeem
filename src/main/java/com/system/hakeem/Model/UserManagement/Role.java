package com.system.hakeem.Model.UserManagement;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "hakeem_schema" , name = "user_role" )
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Type role;

    public Role(Type type) {
        this.role = type;
    }
}

