package com.hey.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "wallets",
        indexes = {
                @Index(name = "i_owner_id", columnList = "owner_id"),
        }
)
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balance;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "ref_from")
    private String refFrom;

}
