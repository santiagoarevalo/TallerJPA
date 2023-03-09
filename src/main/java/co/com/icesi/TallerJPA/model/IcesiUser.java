package co.com.icesi.TallerJPA.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IcesiUser {
    @Id
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;

    //Un usuario tiene multiples cuentas
    @OneToMany(mappedBy = "user")
    private List<IcesiAccount> accounts;

    //Los usuarios tienen un rol
    @NotNull
    @ManyToOne
    @JoinColumn(name = "icesi_role_role_id")
    private IcesiRole role;



}