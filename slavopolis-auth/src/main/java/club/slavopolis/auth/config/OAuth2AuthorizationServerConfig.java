package club.slavopolis.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * OAuth2 授权服务器配置
 * 
 * <p>
 * 配置OAuth2授权服务器的核心组件：
 * - 客户端注册信息
 * - JWT令牌配置
 * - 授权服务器端点
 * - 令牌设置
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/1/20
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuth2AuthorizationServerConfig {

    /**
     * OAuth2授权服务器安全过滤器链
     * 
     * @param http HttpSecurity配置
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(oidc -> oidc
                        .clientRegistrationEndpoint(clientRegistration -> clientRegistration
                                .authenticationProviders(authenticationProviders -> {
                                    // 自定义客户端注册认证提供者
                                    log.debug("配置客户端注册认证提供者");
                                })
                        )
                );
        
        http
                // 异常处理 - 重定向到登录页面
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                // OAuth2资源服务器配置
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                );
        
        return http.build();
    }

    /**
     * 默认安全过滤器链
     * 
     * @param http HttpSecurity配置
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 公开端点
                        .requestMatchers("/login", "/error", "/actuator/**").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 表单登录配置
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                );
        
        return http.build();
    }

    /**
     * 注册客户端仓库
     * 
     * @return RegisteredClientRepository
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        // Web客户端
        RegisteredClient webClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("slavopolis-web")
                .clientSecret("{noop}web-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:3000/login/oauth2/code/slavopolis")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope("read")
                .scope("write")
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(2))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .build())
                .build();

        // 移动端客户端
        RegisteredClient mobileClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("slavopolis-mobile")
                .clientSecret("{noop}mobile-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope("read")
                .scope("write")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(2))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(webClient, mobileClient);
    }

    /**
     * 密码编码器
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 用户详细信息服务（临时实现，后续会替换为数据库实现）
     * 
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * JWT解码器
     * 
     * @return JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource());
    }

    /**
     * JWK源配置
     * 
     * @return JWKSource
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 授权服务器设置
     * 
     * @return AuthorizationServerSettings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000/auth")
                .build();
    }

    /**
     * 生成RSA密钥对
     * 
     * @return KeyPair
     */
    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("无法生成RSA密钥对", e);
        }
    }
} 