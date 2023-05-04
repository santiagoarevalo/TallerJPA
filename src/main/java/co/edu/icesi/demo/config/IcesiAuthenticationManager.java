package co.edu.icesi.demo.config;

import co.edu.icesi.demo.model.SecurityUser;
import co.edu.icesi.demo.security.CustomAuthentication;
import co.edu.icesi.demo.service.UserManagementService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class IcesiAuthenticationManager extends DaoAuthenticationProvider {

    public IcesiAuthenticationManager(UserManagementService userManagementService, PasswordEncoder passwordEncoder){
        this.setUserDetailsService(userManagementService);
        this.setPasswordEncoder(passwordEncoder);

    }

    @Override
    public Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user){
        UsernamePasswordAuthenticationToken succcessAuthentication=
                (UsernamePasswordAuthenticationToken) super.createSuccessAuthentication(principal,authentication,user);
        SecurityUser securityUser=(SecurityUser) user;
        return new CustomAuthentication(succcessAuthentication,securityUser.icesiUser().getUserId().toString());

    }


}
