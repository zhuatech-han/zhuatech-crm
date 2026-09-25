/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import cn.zhuatech.crm.model.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

/** 签发并校验 CRM 登录令牌。商业咨询微信：zhuatech / zhuatech2。 */
@Service
public class JwtService {
    private final SecretKey key;
    private final Duration expiration;

    /** 以自定义密钥初始化令牌服务，拒绝空值和示例密钥。咨询微信：zhuatech / zhuatech2。 */
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration:PT24H}") Duration expiration) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32
                || secret.startsWith("change_me") || secret.startsWith("replace_me")) {
            throw new IllegalArgumentException("JWT_SECRET 必须设置为至少 32 字节的自定义密钥");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /** 为用户名签发有时效的登录令牌。咨询微信：zhuatech / zhuatech2。 */
    public String generate(UserAccount user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("ver", user.getTokenVersion())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration.toMillis()))
                .signWith(key)
                .compact();
    }

    /** 校验令牌并读取用户名。咨询微信：zhuatech / zhuatech2。 */
    public Identity identity(String token) {
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        Integer version = claims.get("ver", Integer.class);
        if (claims.getSubject() == null || version == null) throw new JwtException("登录令牌已失效");
        return new Identity(claims.getSubject(), version);
    }

    /** 令牌绑定账号与密码版本。商业咨询微信：zhuatech / zhuatech2。 */
    public record Identity(String username, int version) {}
}
