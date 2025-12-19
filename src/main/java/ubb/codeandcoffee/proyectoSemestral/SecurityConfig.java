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

                        /* si quieren usar postman, agreguen una linea asi : (cambiando el endpoint y metodo, segun
                        requieran)

                        .requestMatchers(HttpMethod.POST, "/Usuario").permitAll()*/

                        //rutas que pueden acceder todos
                        .requestMatchers(

                                "/login",
                                "/login-post",
                                "/completar_registro",
                                "/static/**",
                                "/error/**"

                        ).permitAll()
                        //rutas que pueden acceder todos

                        //rutas del admin
                        .requestMatchers(

                                "/admin/**",
                                "/menu/admin",
                                "/admin/formulario/**",
                                "/criterios/**",
                                "/crear-usuario",

                                //rutas para postman, por si alguien las quiere usar
                                "/Antecedente/**",
                                "/Criterio",
                                "/DatoSolicitado",
                                "/Opcion",
                                "/Seccion",
                                "/SujetoEstudio",
                                "/Usuario",
                                "/UsuarioSujeto"

                        ).hasRole("ADMINISTRADOR")
                        //rutas del admin

                        //rutas del recolector
                        .requestMatchers(

                                "/menu/recolector"

                        ).hasRole("RECOLECTOR_DE_DATOS")
                        //rutas del recolector

                        //rutas del analista
                        .requestMatchers(

                                "/menu/analista"

                        ).hasRole("ANALISTA")
                        //rutas del analista

                        //rutas compartidas admin y recolector
                        .requestMatchers(

                                "/edicion/**",
                                "/formulario/**",
                                "/confirmacion",
                                "/confirmar-guardado",
                                "/ingreso"

                        ).hasAnyRole("ADMINISTRADOR", "RECOLECTOR_DE_DATOS")
                        //rutas compartidas admin y recolector


                        //rutas compartidas admin y analista
                        .requestMatchers(

                                "/exportar/**"


                        ).hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        //rutas compartidas admin y analista

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
    /*

    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // desactiva protección CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // permite todo acceso sin autenticación
            );
        return http.build();
    }*/
}
