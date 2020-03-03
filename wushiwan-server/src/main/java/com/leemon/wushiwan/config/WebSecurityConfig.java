package com.leemon.wushiwan.config;

import com.leemon.wushiwan.filter.ExceptionHandlerFilter;
import com.leemon.wushiwan.filter.JwtAuthenticationFilter;
import com.leemon.wushiwan.jwt.JwtAuthenticationProvider;
import com.leemon.wushiwan.jwt.JwtTokenUtil;
import com.leemon.wushiwan.jwt.LoginAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * @description: spring security 配置
 * @author: leemon
 * @create: 2019-03-18 10:39
 **/
@Configuration
@EnableWebSecurity(debug = false)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtProperties jwtProperties;
	private final JwtTokenUtil jwtTokenUtil;

	@Autowired
	public WebSecurityConfig(JwtProperties jwtProperties, JwtTokenUtil jwtTokenUtil) {
		this.jwtProperties = jwtProperties;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Autowired
	public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//		authenticationManagerBuilder
//				// 设置UserDetailsService
//				.userDetailsService(userDetailsService)
//				// 使用BCrypt进行密码的hash
//				.passwordEncoder(passwordEncoder());
	}

	// 装载BCrypt密码编码器
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	protected AuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider();
	}

	@Bean
	protected AuthenticationProvider loginAuthenticationProvider() {
		return new LoginAuthenticationProvider();
	}


	@Override
	public void configure(WebSecurity web) {
		//ignore
		web.ignoring().antMatchers(
				"/*.ico",
				"/**/*.css",
				"/**/*.js",
				"/druid/**",
				"/core-login-auth/no-auth/**",
				"/index/no-auth/**",
				"/core-version/no-auth/**",
				"/qiniu/kodo/upload-success",
				"/notify/**"
		).antMatchers(HttpMethod.OPTIONS);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors().and()
				// 由于使用的是JWT，我们这里不需要csrf
				.csrf().disable()
				// 基于token，所以不需要session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				// 对于获取token的rest api要允许匿名访问
//				.antMatchers("/core-login-auth/no-auth/**").permitAll()
				// 除上面外的所有请求全部需要鉴权认证
				.anyRequest().authenticated()
				.and()
				.addFilterBefore(new JwtAuthenticationFilter(this.jwtProperties, authenticationManagerBean(), jwtTokenUtil), RequestCacheAwareFilter.class)
				.addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
				.authenticationProvider(jwtAuthenticationProvider())
				.authenticationProvider(loginAuthenticationProvider());

	}

	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
		return CorsConfig.createCorsConfig();
	}

}
