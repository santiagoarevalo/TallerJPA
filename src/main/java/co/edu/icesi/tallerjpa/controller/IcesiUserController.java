package co.edu.icesi.tallerjpa.controller;

import co.edu.icesi.tallerjpa.api.IcesiUserApi;
import co.edu.icesi.tallerjpa.dto.IcesiUserCreateDTO;
import co.edu.icesi.tallerjpa.dto.IcesiUserShowDTO;
import co.edu.icesi.tallerjpa.service.IcesiUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class IcesiUserController implements IcesiUserApi {
    private final IcesiUserService icesiUserService;

    @Override
    public IcesiUserShowDTO createIcesiUser(@Valid IcesiUserCreateDTO userDTO){
        return icesiUserService.save(userDTO);
    }

}
