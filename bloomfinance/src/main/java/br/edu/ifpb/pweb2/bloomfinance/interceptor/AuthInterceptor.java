package br.edu.ifpb.pweb2.bloomfinance.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        String contextPath = request.getContextPath();
        String path = request.getRequestURI();
        String relativePath = path.substring(contextPath.length());

        // Libera acesso à página de login e recursos estáticos
        if (relativePath.startsWith("/auth") ||
            relativePath.startsWith("/css") ||
            relativePath.startsWith("/js") ||
            relativePath.startsWith("/img") ||
            relativePath.equals("/")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            Correntista usuario = (Correntista) session.getAttribute("usuario");

            if (usuario != null) {
                // Proteção de páginas administrativas
                boolean requerAdmin = relativePath.startsWith("/correntistas") || relativePath.startsWith("/categorias");

                if (requerAdmin && !usuario.isAdmin()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                    return false;
                }

                return true;
            }
        }

        // Redireciona para login se não estiver logado
        response.sendRedirect(contextPath + "/auth");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }
}

