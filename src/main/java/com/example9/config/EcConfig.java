package com.example9.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
class DAdministratorsConfig extends WebSecurityConfigurerAdapter {


	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//				// ログイン前にアクセス可とするファイル群
//				.mvcMatchers("/").permitAll()
//				.mvcMatchers("/searchResult").permitAll()
//				.mvcMatchers("/sortShowList").permitAll()
//				.mvcMatchers("/add_to_cart**").permitAll()
//				.mvcMatchers("/delete_from_cart**").permitAll()
//				.mvcMatchers("/login").permitAll()
//				.mvcMatchers("/confirm**").permitAll()
//				.mvcMatchers("/show_cart_list**").permitAll()
//				.mvcMatchers("/item-detail**").permitAll()
//				.mvcMatchers("/register**").permitAll()
//				.mvcMatchers("/css/**").permitAll()
//				.mvcMatchers("/img_curry/**")
//				.permitAll().mvcMatchers("/js/**").permitAll()
//				// 上記以外のファイルは、ログイン以前のアクセス不可とする
//				.anyRequest().authenticated().and();
//		// LOGIN
//		http.formLogin()
//				.loginProcessingUrl("/login")
//				.loginPage("/")
//				.failureUrl("/failure")
//				.defaultSuccessUrl("/employee/showList", true)
//				.usernameParameter("mailAddress")
//				.passwordParameter("password").and()
//				.logout()
//				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//				.logoutSuccessUrl("/")
////                .deleteCookies("JSESSIONID")
//				.invalidateHttpSession(true).permitAll();
//		// end
		
	}
}