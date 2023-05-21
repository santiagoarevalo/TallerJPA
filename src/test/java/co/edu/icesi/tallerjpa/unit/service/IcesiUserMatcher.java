package co.edu.icesi.tallerjpa.unit.service;

import co.edu.icesi.tallerjpa.model.IcesiUser;
import lombok.AllArgsConstructor;
import org.mockito.ArgumentMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@AllArgsConstructor
public class IcesiUserMatcher implements ArgumentMatcher<IcesiUser> {
    private IcesiUser icesiUserLeft;
    @Override
    public boolean matches(IcesiUser icesiUserRight) {
        return icesiUserRight.getUserId() != null &&
                Objects.equals(icesiUserRight.getFirstName(), icesiUserLeft.getFirstName()) &&
                Objects.equals(icesiUserRight.getLastName(), icesiUserLeft.getLastName()) &&
                Objects.equals(icesiUserRight.getEmail(), icesiUserLeft.getEmail()) &&
                Objects.equals(icesiUserRight.getPhoneNumber(), icesiUserLeft.getPhoneNumber()) &&
                icesiUserRight.getPassword() != null &&
                Objects.equals(icesiUserRight.getIcesiRole().getName(), icesiUserLeft.getIcesiRole().getName()) &&
                Objects.equals(icesiUserRight.getIcesiRole().getDescription(), icesiUserLeft.getIcesiRole().getDescription());
    }
}