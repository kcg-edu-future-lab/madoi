package edu.kcg.futurelab.madoi.volatileserver.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import edu.kcg.futurelab.madoi.volatileserver.ApplicationProperties;
import edu.kcg.futurelab.madoi.volatileserver.config.auth.AuthTokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	static GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		var authTokens = Arrays.asList(props.getAuthTokens()).stream()
				.flatMap(value->Stream.of(value.split(",")))
				.filter(v->!v.isBlank())
				.toList();
		var allowedOrigins = Arrays.asList(props.getAllowedOrigins()).stream()
				.flatMap(value->Stream.of(value.split(",")))
				.filter(v->!v.isBlank())
				.toList();
		http.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(request -> {
				var c = new CorsConfiguration();
				c.setAllowedOriginPatterns(allowedOrigins);
				c.setAllowedMethods(List.of("*"));
				c.setAllowedHeaders(List.of("*"));
				return c;
			}))
			.headers(headers -> headers.frameOptions(
					frame -> frame.sameOrigin()))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
					.requestMatchers(
							"/", "/*.html", "/lib/**", "/js/**"
							).permitAll()
					.anyRequest()
							.authenticated())
			.addFilterBefore(
					new AuthTokenAuthenticationFilter(authTokens.toArray(new String[] {}), contextPath, "/rooms"),
					AnonymousAuthenticationFilter.class)
			.formLogin(Customizer.withDefaults())
			.logout(Customizer.withDefaults())
			;

		return http.build();
	}

	@Value("${server.servlet.context-path:}")
	private String contextPath;
	@Autowired
	private ApplicationProperties props;
}
