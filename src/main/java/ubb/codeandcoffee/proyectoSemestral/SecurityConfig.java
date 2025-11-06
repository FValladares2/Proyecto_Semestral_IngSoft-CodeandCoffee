package ubb.codeandcoffee.proyectoSemestral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ubb.codeandcoffee.proyectoSemestral.servicios.Usuario_UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Usuario_UserDetailsService uds;

    @Autowired
    private Exito exito;

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


                        .requestMatchers(
                                "/login",
                                "/completar_registro",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()


                        .requestMatchers(HttpMethod.POST, "/Usuario").permitAll()

                        .requestMatchers(
                                "/admin/**",
                                "/Usuario/**"
                        ).hasRole("ADMINISTRADOR")


                        .requestMatchers("/formularios/**")
                        .hasAnyRole("ADMINISTRADOR", "RECOLECTOR_DE_DATOS")


                        /* aun no implementadas las opciones
                        .requestMatchers("/exportar/**")
                        .hasAnyRole("ADMINISTRADOR", "ANALISTA")

                         */

                        // solo la persona indicada puede ver el menu segun su rol
                        .requestMatchers("/menu/admin").hasRole("ADMINISTRADOR")
                        .requestMatchers("/menu/analista").hasRole("ANALISTA")
                        .requestMatchers("/menu/recolector").hasRole("RECOLECTOR_DE_DATOS")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-post")
                        .successHandler(exito)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                );
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