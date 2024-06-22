package edu.kcg.futurelab.madoi.volatileserver.config.auth;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	public AuthTokenAuthenticationFilter(String[] authTokens, String contextPath, String... paths){
		this.authTokens = authTokens;
		this.contextPath = contextPath;
		this.paths = paths;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			var r = (HttpServletRequest)request;
			var reqPath = r.getRequestURI().substring(contextPath.length());
			var target = false;
			for(var p : paths){
				if(reqPath.startsWith(p)){
					target = true;
					break;
				}
			}
			var keyHeader = r.getHeader("X-Auth-Token");
			var keyParam = r.getParameter("authToken");
			String foundToken = null;
			if(authTokens.length == 0) {
				var auth = new AuthTokenAuthentication("NO_AUTH_TOKEN_DEFINITIONS", AuthorityUtils.NO_AUTHORITIES);
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else if(target){
				for(var k : new String[]{keyHeader, keyParam}){
					if(k != null) {
						for(var authToken : authTokens){
							if(authToken.equals(k)){
								foundToken = authToken;
							}
						}
					}
				}
				if(foundToken != null){
					var auth = new AuthTokenAuthentication(foundToken, AuthorityUtils.NO_AUTHORITIES);
					SecurityContextHolder.getContext().setAuthentication(auth);
				} else{
					throw new BadCredentialsException("Invalid Auth Token");
				}
			}
			chain.doFilter(request, response);
		} catch (RuntimeException | Error e) {
			throw e;
		} catch (Exception e) {
			var r = (HttpServletResponse) response;
			r.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			r.setContentType(MediaType.APPLICATION_JSON_VALUE);
			try(var w = r.getWriter()){
				w.print(e.getMessage());
			}
		}
	}

	private String contextPath;
	private String[] authTokens;
	private String[] paths;
}
