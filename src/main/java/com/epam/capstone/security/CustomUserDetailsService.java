package com.epam.capstone.security;

import com.epam.capstone.dao.RoleDao;
import com.epam.capstone.dao.UserDao;
import com.epam.capstone.model.Role;
import com.epam.capstone.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public CustomUserDetailsService(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        log.debug("User: {}", user);
        Role role = roleDao.findById(user.getRoleID());

        String roleName = "ROLE_" + role.getName().toUpperCase();
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(roleName));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}
