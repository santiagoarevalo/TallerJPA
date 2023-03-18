package co.com.icesi.TallerJPA.mapper;

import co.com.icesi.TallerJPA.dto.IcesiRoleDTO;
import co.com.icesi.TallerJPA.model.IcesiRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IcesiRoleMapper {
    IcesiRole fromRoleDTO(IcesiRoleDTO roleDTO);
    IcesiRoleDTO fromIcesiRole(IcesiRole role);
}