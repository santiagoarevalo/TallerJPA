package co.com.icesi.TallerJPA.model;


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
    private UUID userID;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String password;

    private String email;

    @OneToMany(mappedBy = "user")
    private List<IcesiAccount> accounts;

    @ManyToOne
    @JoinColumn(name = "icesi_role_role_id")
    private IcesiRole role;

}