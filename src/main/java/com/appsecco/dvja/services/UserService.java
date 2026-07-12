package com.appsecco.dvja.services;

import com.appsecco.dvja.models.User;
import com.appsecco.dvja.security.AESUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Transactional
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    private static final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void save(User user) {

        logger.debug("Saving user with login: " + user.getLogin() +
                " id: " + user.getId());


        //Solo generar un nuevo hash cuando la contraseña
        //no sea ya un hash BCrypt.

        if (user.getPassword() != null &&
                !user.getPassword().startsWith("$2a$") &&
                !user.getPassword().startsWith("$2b$") &&
                !user.getPassword().startsWith("$2y$")) {

            user.setPassword(hashEncodePassword(user.getPassword()));
        }

         //Cifrar el correo antes de almacenarlo.

        if (user.getEmail() != null) {
            user.setEmail(AESUtil.encrypt(user.getEmail()));
        }

        if (user.getId() != null) {
            entityManager.merge(user);
        } else {
            entityManager.persist(user);
        }
    }


    // Descifrar el correo al recuperar un usuario

    public User find(int id) {

        User user = entityManager.find(User.class, id);

        if (user != null && user.getEmail() != null) {
            user.setEmail(AESUtil.decrypt(user.getEmail()));
        }

        return user;
    }


     //Verificación usando BCrypt.

    public boolean checkPassword(User user, String password) {

        if (user == null)
            return false;

        if (StringUtils.isEmpty(password))
            return false;

        return encoder.matches(password, user.getPassword());
    }


     //Descifrar los correos de toda la lista.
    public List<User> findAllUsers() {

        Query query = entityManager.createQuery("SELECT u FROM User u");

        List<User> resultList = query.getResultList();

        for (User user : resultList) {

            if (user.getEmail() != null) {
                user.setEmail(AESUtil.decrypt(user.getEmail()));
            }
        }

        return resultList;
    }


     //Buscar por login y devolver el correo descifrado.

    public User findByLogin(String login) {

        Query query = entityManager
                .createQuery("SELECT u FROM User u WHERE u.login = :login")
                .setParameter("login", login)
                .setMaxResults(1);

        List<User> resultList = query.getResultList();

        if (resultList.size() > 0) {

            User user = resultList.get(0);

            if (user.getEmail() != null) {
                user.setEmail(AESUtil.decrypt(user.getEmail()));
            }

            return user;
        }

        return null;
    }

    /*
     * Método vulnerable.
     * Se corregirá en la Parte 3 (SQL Injection).
     */
    public User findByLoginUnsafe(String login) {

        Query query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.login = '" + login + "'"
        );

        List<User> resultList = query.getResultList();

        if (resultList.size() > 0)
            return resultList.get(0);

        return null;
    }

    public boolean resetPasswordByLogin(String login,
                                        String key,
                                        String password,
                                        String passwordConfirmation) {

        if (!StringUtils.equals(password, passwordConfirmation))
            return false;

        /*
         * Esta validación sigue usando MD5.
         * Pertenece a la vulnerabilidad de Broken Access Control
         * y se corregirá en otra fase.
         */
        if (!StringUtils.equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(login.getBytes()), key))
            return false;

        // Ya no se registra la contraseña en los logs
        logger.info("Password reset requested for login: " + login);

        User user = findByLogin(login);

        if (user != null) {

            user.setPassword(password);
            save(user);

            return true;
        }

        logger.info("Failed to find user with login: " + login);

        return false;
    }

    private String hashEncodePassword(String password) {

        return encoder.encode(password);
    }
}