package com.epam.capstone.config.init;

import com.epam.capstone.repository.dao.RoleDao;
import com.epam.capstone.repository.dao.UserDao;
import com.epam.capstone.model.Role;
import com.epam.capstone.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Order(1)
@Slf4j
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final RoleDao roleDao;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleDao roleDao,
                           UserDao userDao,
                           PasswordEncoder passwordEncoder) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null || !initialized.compareAndSet(false, true)) {
            return;
        }

        Role adminRole;
        try {
            adminRole = roleDao.findByName("ADMIN");
        } catch (RuntimeException e) {
            adminRole = Role.builder().name("ADMIN").build();
            roleDao.save(adminRole);
            log.info("Created role ADMIN");
        }

        try {
            userDao.findByUsername("admin");
        } catch (RuntimeException e) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            admin.setRoleId(adminRole.getRoleId());
            admin.setEmail("admin@epam.com");
            admin.setFullName("Admin");
            userDao.save(admin);
        }
    }
}
