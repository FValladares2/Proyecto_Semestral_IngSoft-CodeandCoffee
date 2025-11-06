package ubb.codeandcoffee.proyectoSemestral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ubb.codeandcoffee.proyectoSemestral.repositorios.UsuarioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.UsuarioService;
import ubb.codeandcoffee.proyectoSemestral.servicios.Usuario_UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Usuario_UserDetailsService uds;

    @Bean
    public PasswordEncoder passwordEnconder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider prov = new DaoAuthenticationProvider();
        prov.setUserDetailsService(uds);
        prov.setPasswordEncoder(passwordEnconder());
        return prov;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/Usuario/**").hasRole("ADMINISTRADOR")
                        // habria que agregar las rutas a las que solo pueden acceder las demas personas, pero ni idea
                        // .requestMatchers(no se que iria aqui xd).hasRole("ANALISTA")
                        // .requestMatchers(no se que iria aqui xd).hasRole("RECOLECTOR_DE_DATOS")

                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }

    /* lo anterior que hizo la May
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // desactiva protección CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // permite todo acceso sin autenticación
            );
        return http.build();
    } */
}