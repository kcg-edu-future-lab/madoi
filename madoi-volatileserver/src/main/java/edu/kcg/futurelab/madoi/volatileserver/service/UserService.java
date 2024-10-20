package edu.kcg.futurelab.madoi.volatileserver.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.kcg.futurelab.madoi.volatileserver.ApplicationProperties;

@Service
public class UserService implements UserDetailsService {
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!props.getAdminUser().equals(username)) {
			throw new UsernameNotFoundException("Invalid user ID or password.");
		}
		return new UserDetails() {
			private static final long serialVersionUID = 1L;
			@Override
			public String getUsername() {
				return username;
			}
			@Override
			public String getPassword() {
				return passwordEncoder.encode(props.getAdminPass());
			}
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	// SecurityConfig.passwordEncoderを参照する
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ApplicationProperties props;
}
