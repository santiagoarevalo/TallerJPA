package co.com.icesi.tallerjpa.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    private UUID roleId;
    private String name;
    private String description;
    @OneToMany(mappedBy = "role")
    private List<IcesiUser> users;

}
