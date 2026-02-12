package com.nbu.CSCB532.model.logistics;

import com.nbu.CSCB532.model.auth.User;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

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

    @ManyToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "default_address_id")
    private Address defaultAddress;
}

