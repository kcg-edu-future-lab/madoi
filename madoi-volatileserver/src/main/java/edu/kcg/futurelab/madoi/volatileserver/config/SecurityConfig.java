package edu.kcg.futurelab.madoi.volatileserver.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import edu.kcg.futurelab.madoi.volatileserver.ApplicationProperties;
import edu.kcg.futurelab.madoi.volatileserver.config.auth.ApiKeyAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Bean
	static GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		var apiKeys = Arrays.asList(props.getApiKeys()).stream()
				.flatMap(value->Stream.of(value.split(",")))
				.filter(v->!v.isBlank())
				.toList();
		var allowedOrigins = Arrays.asList(props.getAllowedOrigins()).stream()
				.flatMap(value->Stream.of(value.split(",")))
				.filter(v->!v.isBlank())
				.toList();
		System.out.println("allowed origins: " + allowedOrigins);
		System.out.println("apikeys: " + apiKeys);
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
					.requestMatchers(
							AntPathRequestMatcher.antMatcher("/*.html"), // sample codes
							AntPathRequestMatcher.antMatcher("/js/**")
							).permitAll()
					.anyRequest()
							.authenticated())
			.addFilterBefore(
					new ApiKeyAuthenticationFilter(apiKeys.toArray(new String[] {}), contextPath, "/rooms"),
					AnonymousAuthenticationFilter.class)
			;

		return http.build();
	}

	@Value("${server.servlet.context-path:}")
	private String contextPath;
	@Autowired
	private ApplicationProperties props;
}
