package com.haotchen.server.config.security;


import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class JwtTokenUtil {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    public static String jwtTokenHeader = "Authorization";
    public static String jwtTokenSecret = "yeb-secret";
    public static String jwtTokenHead = "Bearer";
    public static long jwtTokenExpiration = 604800;


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.expiration}")
    private Long expiration;

    public JwtTokenUtil() {
        if (Objects.nonNull(secret)) {
            jwtTokenSecret = secret;
        }
        if (Objects.nonNull(tokenHeader)) {
            jwtTokenHeader = tokenHeader;
        }
        if (Objects.nonNull(tokenHead)) {
            jwtTokenHead = tokenHead;
        }
        if (Objects.nonNull(expiration)) {
            jwtTokenExpiration = expiration;
        }
    }


    /**
     * 生成token
     *
     * @param userDetails
     * @return String
     */

    public String getToken(UserDetails userDetails) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        maps.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(maps);
    }

    /**
     * 根据载荷生成token
     *
     * @param maps
     * @return String
     */
    private String generateToken(Map<String, Object> maps) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenExpiration))
                .setClaims(maps)
                .signWith(SignatureAlgorithm.HS512, jwtTokenSecret);
        return jwtBuilder.compact();
    }

    public String getUserName(String token) {
        return (String) Jwts.parser().setSigningKey(jwtTokenSecret)
                .parseClaimsJws(token).getBody().get(CLAIM_KEY_USERNAME);
    }

    /**
     * 解析token
     *
     * @param token
     * @return Jws<Claims>
     */
    public Jws<Claims> paresToken(String token) {
        return Jwts.parser().setSigningKey(jwtTokenSecret).parseClaimsJws(token);
    }


    /**
     * 校验token 是否正常
     *
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {

        try {
            String userName = getUserName(token);
            if (Objects.nonNull(userName) || Objects.nonNull(userDetails.getUsername())) {
                return userName.equals(userDetails.getUsername());
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        return false;
    }


    /**
     * 判断token是否过期
     *
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser().setSigningKey(jwtTokenSecret)
                .parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 是否可以刷新token
     *
     * @param token
     * @return
     */
    public Boolean canRefreshToken(String token) {
        return isTokenExpired(token);
    }

    /**
     * 刷新token
     * @param token
     * @return
     */
    public String refreshToken(String token) throws Exception {
        if (canRefreshToken(token)) {
            Claims body = Jwts.parser().setSigningKey(jwtTokenSecret).parseClaimsJws(token)
                    .getBody();
            String username = (String) body.get(CLAIM_KEY_USERNAME);
            User user = new User(username, "", null);
            String newToken = getToken(user);
            return newToken;
        } else {
            throw new Exception("token还未过期不能被刷新");
        }
    }

}
