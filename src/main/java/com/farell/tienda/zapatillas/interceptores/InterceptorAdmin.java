package com.farell.tienda.zapatillas.interceptores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InterceptorAdmin implements HandlerInterceptor, InitializingBean {

    private static final String ADMIN_SESSION_ATTRIBUTE = "admin";

    // la contraseña viene de ADMIN_PASSWORD (variable de entorno o secreto.properties);
    // no hay ninguna contraseña escrita en el codigo
    @Value("${tienda.admin.password:}")
    private String passwordAdmin;

    @Override
    public void afterPropertiesSet() {
        if (passwordAdmin == null || passwordAdmin.isBlank()) {
            System.err.println("AVISO: ADMIN_PASSWORD no esta configurada. "
                    + "El acceso a administracion queda bloqueado.");
        }
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());

        boolean contraseñaConfigurada = passwordAdmin != null && !passwordAdmin.isBlank();

        if ("/admin/inicioAdmin".equals(requestPath)
                && "POST".equalsIgnoreCase(request.getMethod())
                && contraseñaConfigurada
                && passwordAdmin.equals(request.getParameter("pass-login-admin"))) {
            request.getSession().setAttribute(ADMIN_SESSION_ATTRIBUTE, "ok");
        }

        if (requestPath.startsWith("/admin/")
                && !"ok".equals(request.getSession().getAttribute(ADMIN_SESSION_ATTRIBUTE))) {
            response.sendRedirect(request.getContextPath() + "/loginAdmin?error");
            return false;
        }

        return true;
    }
}
