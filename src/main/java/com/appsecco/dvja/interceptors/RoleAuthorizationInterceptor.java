package com.appsecco.dvja.interceptors;

import com.appsecco.dvja.Constant;
import com.appsecco.dvja.models.User;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Map;

public class RoleAuthorizationInterceptor implements Interceptor {

    private String allowedRoles;

    public void setAllowedRoles(String allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() {
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Map<String, Object> session =
                actionInvocation.getInvocationContext().getSession();

        User user = (User) session.get(Constant.SESSION_USER_HANDLE);

        if (user == null) {
            return "login";
        }

        if (user.getRole() == null || allowedRoles == null) {
            return "accessDenied";
        }

        String[] roles = allowedRoles.split(",");

        for (String role : roles) {
            if (role.trim().equalsIgnoreCase(user.getRole())) {
                return actionInvocation.invoke();
            }
        }

        return "accessDenied";
    }
}