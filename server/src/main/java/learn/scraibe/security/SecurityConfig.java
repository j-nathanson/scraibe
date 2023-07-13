package learn.scraibe.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication
public class SecurityConfig {

    private final JwtConverter jwtConverter;

    public SecurityConfig(JwtConverter jwtConverter) {
        this.jwtConverter = jwtConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {

        http.csrf().disable();

        http.cors();

        http.authorizeRequests()
//                .antMatchers("/**").permitAll()//TODO uncomment if you want to use backend without authorization, delete before final presentation
                .antMatchers(HttpMethod.POST,
                        "/security/authenticate-username", "/security/authenticate-email", "/security/create-account").permitAll()
                .antMatchers(HttpMethod.POST, "/security/refresh-token").authenticated()
                .antMatchers(HttpMethod.GET,"/users", "/get-email","/get-username").hasAuthority("admin")
                .antMatchers(HttpMethod.PUT,"/users/edit/{id}").authenticated()
                .antMatchers(HttpMethod.DELETE,"/users/delete/{id}").hasAuthority("admin")
                .antMatchers(HttpMethod.GET, "/courses","/courses/{id}","/courses/from-user/{id}").authenticated()
                .antMatchers(HttpMethod.POST, "/courses").authenticated()
                .antMatchers(HttpMethod.PUT,"/courses/{id}").authenticated()
                .antMatchers(HttpMethod.DELETE,"/courses/{id}").authenticated()
                .antMatchers(HttpMethod.GET, "/notes", "/notes/{id}", "/notes/from-course/{id}").authenticated()
                .antMatchers(HttpMethod.POST, "/notes").authenticated()
                .antMatchers(HttpMethod.PUT, "/notes/{id}").authenticated()
                .antMatchers(HttpMethod.DELETE, "/notes/{id}").authenticated()
                .antMatchers(HttpMethod.POST,"/generate-completion").permitAll()
                .and()
                .addFilter(new JwtRequestFilter(authenticationManager(authConfig), jwtConverter))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
