package com.nbu.CSCB532.model.logistics;

import com.nbu.CSCB532.model.auth.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    private Long id; // same as User.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String phone;

    @Embedded
    private Address defaultAddress;
}

