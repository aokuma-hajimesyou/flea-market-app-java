// Spring の設定クラスであることを示す
package com.example.flea_market_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	// セキュリティの主要設定（エンドポイント保護 / 認証 / ログアウト / CSRF 例外）
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 認可ルールの設定
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(
								"/login",
								"/css/**",
								"/js/**",
								"/images/**",
								"/items/**")
						.permitAll()
						.requestMatchers("/orders/stripe-webhook").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				// フォームログイン設定
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/items", false)
						.permitAll())
				// ログアウト設定
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				// CSRF（Stripe Webhook のみ除外）
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/orders/stripe-webhook"));

		return http.build();
	}

	// DB からユーザをロードして Spring Security の UserDetails に変換
	//	@Bean
	//	public UserDetailsService userDetailsService(UserRepository userRepository) {
	//		return email -> userRepository.findByEmail(email)
	//				.map(user -> org.springframework.security.core.userdetails.User.builder()
	//						.username(user.getEmail())
	//						.password(user.getPassword())
	//						.roles(user.getRole())
	//						.disabled(!user.isEnabled())
	//						.build())
	//				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		//		return new BCryptPasswordEncoder();
	}
}
