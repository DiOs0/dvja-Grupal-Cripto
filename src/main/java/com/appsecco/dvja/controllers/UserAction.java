package com.appsecco.dvja.controllers;

import com.appsecco.dvja.models.User;
import com.appsecco.dvja.services.UserService;
import org.apache.commons.lang.StringUtils;

public class UserAction extends BaseController {

    private UserService userService;
    private User user;
    private String login;
    private String password;
    private String passwordConfirmation;
    private String email;
    private int userId;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String edit() {

        User sessionUser = sessionGetUser();

        if (sessionUser == null) {
            return LOGIN;
        }

        // Al abrir la página, cargar al usuario desde el servicio
        // para obtener el correo descifrado.
        if ("GET".equalsIgnoreCase(getServletRequest().getMethod())) {

            user = userService.find(sessionUser.getId());

            if (user == null) {
                addActionError("No se pudo encontrar el usuario");
                return INPUT;
            }

            setUserId(user.getId());
            setEmail(user.getEmail());

            return INPUT;
        }

        // En POST se utiliza el ID del usuario autenticado.
        setUserId(sessionUser.getId());

        boolean passwordProvided =
                !StringUtils.isEmpty(getPassword()) ||
                        !StringUtils.isEmpty(getPasswordConfirmation());

        if (passwordProvided) {

            if (StringUtils.isEmpty(getPassword()) ||
                    StringUtils.isEmpty(getPasswordConfirmation())) {

                addFieldError(
                        "password",
                        "Debe ingresar y confirmar la nueva contraseña"
                );

                return INPUT;
            }

            if (!getPassword().equals(getPasswordConfirmation())) {

                addFieldError(
                        "password",
                        "La contraseña no coincide con la confirmación"
                );

                return INPUT;
            }
        }

        user = userService.find(getUserId());

        if (user == null) {
            addActionError(
                    "No se pudo encontrar el usuario con id: " + getUserId()
            );

            return INPUT;
        }

        if (!StringUtils.isEmpty(getEmail())) {
            user.setEmail(getEmail());
        }

        if (passwordProvided) {
            user.setPassword(getPassword());
        }

        userService.save(user);

        return SUCCESS;
    }

    public String search() {
        if(StringUtils.isEmpty(getLogin()))
            return INPUT;

        try {
            user = userService.findByLoginUnsafe(getLogin());
            if(user == null) {
                addFieldError("login", "User not found by login: " + getLogin());
                return INPUT;
            }
        }
        catch(Exception e) {
            addActionError("Error Occurred: " + e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }

}
