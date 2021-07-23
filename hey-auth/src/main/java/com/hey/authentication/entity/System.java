package com.hey.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "systems")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class System {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_name")
    private String systemName;

    @Column(name = "system_key")
    private String systemKey;

}
