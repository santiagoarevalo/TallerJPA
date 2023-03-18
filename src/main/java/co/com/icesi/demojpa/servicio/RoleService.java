package co.com.icesi.demojpa.servicio;

import co.com.icesi.demojpa.dto.RoleCreateDTO;
import co.com.icesi.demojpa.mapper.RoleMapper;
import co.com.icesi.demojpa.model.IcesiRole;
import co.com.icesi.demojpa.repository.RoleRepository;
import co.com.icesi.demojpa.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.userRepository = userRepository;
    }

    public IcesiRole save(RoleCreateDTO role){
        if(roleRepository.findByName(role.getName()).isPresent()){
            throw new RuntimeException("Ya existe un rol con este nombre");
        }

        IcesiRole icesiRole = roleMapper.fromIcesiRoleDTO(role);
        icesiRole.setRoleId(UUID.randomUUID());
        return roleRepository.save(icesiRole);
    }

    public void addUser(IcesiRole role, UUID userid){
        if(userRepository.findById(userid).isEmpty()){
            throw new RuntimeException("No existe un usuario con esta id");
        }
        role.getUser().add(userRepository.findById(userid).get());
    }

    public Optional<IcesiRole> findById(UUID fromString){
        return roleRepository.findById(fromString);
    }
}