package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Custom Security Configuration
 * <p>
 * Code largely based on example provided by Morgan English.
 *
 * @author <a href="https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/-/tree/master/src/main/java/security/example">Morgan English</a>
 */
@Configuration
@EnableWebSecurity
@ComponentScan("com.baeldung.security")
public class SecurityConfiguration {

    @Autowired
    private UserService userService;

    private static final String LOGIN_URL = "/login";
    private static final String H2_URL = "/h2/**";


    /**
     * Create an Authentication Manager with our {@link TabAuthenticationProvider}
     * @param http http security configuration object from Spring
     * @param authProvider authentication provider for TAB {@link TabAuthenticationProvider}
     * @return a new authentication manager
     * @throws Exception if the AuthenticationManager can not be built
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, TabAuthenticationProvider authProvider) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider); //this.authenticationProvider(http)

        return authenticationManagerBuilder.build();

    }


    /**
     * @param http {@link HttpSecurity} configuration object from Spring
     * @return Returns a new {@link SecurityFilterChain}
     * @throws Exception if the {@link SecurityFilterChain} cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Allow h2 console through security. Note: Spring 6 broke the nicer way to do this
        // See https://github.com/spring-projects/spring-security/issues/12546

        http.authorizeHttpRequests(auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher(H2_URL)).permitAll())
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher(H2_URL)))

                .authorizeHttpRequests()

                // Allow following URLs to anyone (permitAll)
                .requestMatchers("/", "/resetPassword", "/resetPassEmail", "/register", LOGIN_URL, "/css/**", H2_URL, "/js/**", "/activate/**", "/locationSuggest")
                .permitAll()
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/css/**"),
                        AntPathRequestMatcher.antMatcher("/webjars/**"),
                        AntPathRequestMatcher.antMatcher("/resetPassword/**")
                )
                .permitAll()

                // Only allow admins to reach the "/admin" page
                .requestMatchers("/admin")
                // note we do not need the "ROLE_" prefix as we are calling "hasRole()"
                .hasRole("ADMIN")

                // Only allow admins to reach the h2 passthrough
                .requestMatchers(AntPathRequestMatcher.antMatcher(H2_URL))
                .hasRole("ADMIN")


                // Any other request requires authentication
                .anyRequest()
                .authenticated()
                .and()

                // Define logging in, a POST "/login" endpoint now exists under the hood, after logic redirect to user page
                .formLogin().loginPage(LOGIN_URL)
                .failureHandler(new TabFailureHandler())
                .loginProcessingUrl(LOGIN_URL)
                .defaultSuccessUrl("/home")

                .and()
                // Define logging out, a POST "/logout" endpoint now exists under the hood, redirect to "/login", invalidate session and remove cookie
                .logout().logoutUrl("/logout").logoutSuccessUrl(LOGIN_URL).invalidateHttpSession(true).deleteCookies("JSESSIONID")

                .and()
                // Allow changing of password at endpoint '/update-password'
                .passwordManagement(management -> management.changePasswordPage("/update-password"))
                .requestCache().disable();

        return http.build();
    }

}
