package com.icesi.TallerJPA.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcesiRole {

    @Id
    private UUID roleId;
    private String description;
    private String name;

    @OneToMany(mappedBy = "icesiRole")
    private List<IcesiUser> icesiUserList;
}
