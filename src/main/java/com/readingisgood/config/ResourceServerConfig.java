package com.readingisgood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.keys.KeyManager;
import org.springframework.security.crypto.keys.StaticKeyGeneratingKeyManager;

@EnableWebSecurity
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
				.oauth2ResourceServer().jwt();
	}

	@Bean
	public KeyManager keyManager() {
		return new StaticKeyGeneratingKeyManager();
	}
}
