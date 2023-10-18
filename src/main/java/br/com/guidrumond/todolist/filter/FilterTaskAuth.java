package br.com.guidrumond.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.guidrumond.todolist.user.IUserRespository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component /*
            * É estritamente necessário para o spring boot fazer o gerenciamento de rotas
            * dentro da aplicação.
            * O Component é considerado o mais genérico do gerenciamento do spring.
            */
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRespository userRespository;

    // Método para interceptar a request antes de passar para a ROTA.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                var servletPath = request.getServletPath();
                if(servletPath.startsWith("/tasks/")){
                    var authorization = request.getHeader("Authorization");               
                    var authEnconded = authorization.substring("Basic".length()).trim();
                    byte[] authDecoded = Base64.getDecoder().decode(authEnconded);
                    var authString = new String(authDecoded);

                    
                    String[] credentials = authString.split(":");
                    String username = credentials[0];
                    String password = credentials[1];

                    var user = this.userRespository.findByUsername(username);

                    if(user == null){
                        response.sendError(401);
                    }else{
                        var passwordVerify  = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                        if(passwordVerify.verified){
                            request.setAttribute("idUser",user.getId());
                            filterChain.doFilter(request, response);
                        }else{
                            response.sendError(401);
                        }
                    }
                }
                else{
                    filterChain.doFilter(request, response);
                }
            }
}

// public class FilterTaskAuth implements Filter {

// Metodo mais nativo
// @Override
// public void doFilter(ServletRequest request, ServletResponse response,
// FilterChain chain)
// throws IOException, ServletException {
// chain.doFilter(request, response);
// }

// }
