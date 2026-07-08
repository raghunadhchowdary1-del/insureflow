package com.insureflow.insureflow.service;

import com.insureflow.insureflow.entity.AuthProvider;
import com.insureflow.insureflow.entity.Role;
import com.insureflow.insureflow.entity.User;
import com.insureflow.insureflow.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole(Role.CUSTOMER);
            user.setAuthProvider(AuthProvider.GOOGLE);
            userRepository.save(user);
        }

        return new CustomOidcUser(oidcUser, user);
    }
}