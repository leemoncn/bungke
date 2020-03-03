package com.leemon.wushiwan.jwt;

import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.enums.base.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-18 16:45
 **/
@Slf4j
@Component
public class JwtTokenUtil implements Serializable {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	private static final long serialVersionUID = -3301605591108950415L;
	private Clock clock = DefaultClock.INSTANCE;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String JWT_WHITE_LIST_NAMESPACE = "JWT_WHITE_LIST_";

	public String getUserIdFromToken(String token) {
		String sub = getClaimFromToken(token, Claims::getSubject);
		return sub;
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		try {
			return Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			throw new LogicException(ErrorCode.LOGIN_FAILED);
		}
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(clock.now());
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public String generateToken(SysUser sysUser, String cacheKey) {
		//传递的数据，不包含userid
		Map<String, Object> claims = new HashMap<>(2);
		return doGenerateToken(claims, cacheKey, false);
	}

	public String generateNeverExpireToken(String cacheKey) {
		Map<String, Object> claims = new HashMap<>(2);
		return doGenerateToken(claims, cacheKey, true);
	}

	private String doGenerateToken(Map<String, Object> claims, String subject, boolean neverExpire) {
		final Date createdDate = clock.now();
		Date expirationDate = calculateExpirationDate(createdDate);
		if (neverExpire) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(2099, Calendar.JANUARY, 1);
			expirationDate = calendar.getTime();
		}

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)//userid
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	public Boolean canTokenBeRefreshed(String token) {
		return !isTokenExpired(token) || ignoreTokenExpiration(token);
	}

	public String refreshToken(String token) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);

		final Claims claims = getAllClaimsFromToken(token);
		claims.setIssuedAt(createdDate);
		claims.setExpiration(expirationDate);

		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	public Boolean validateToken(String token) {
//		JwtUser user = (JwtUser) userDetails;
//		final String username = getUsernameFromToken(token);
//		final Date created = getIssuedAtDateFromToken(token);
//		final Date expiration = getExpirationDateFromToken(token);
		return !isTokenExpired(token);
	}

	private Date calculateExpirationDate(Date createdDate) {
		return new Date(createdDate.getTime() + expiration * 1000);
	}

	/**
	 * 每次登录后，签发一个token，将这个token保存在redis中，作为白名单使用，这样其他之前登录过的token（还未过期的）就不允许再使用，保证同时只有一个token有效
	 *
	 * @param token
	 */
	public void addTokenToWhiteList(String token) {
		Date expirationDate = getExpirationDateFromToken(token);
		final Date currentDate = clock.now();
		long mills = expirationDate.getTime() - currentDate.getTime();
		if (mills > 0) {
			redisTemplate.opsForValue().set(getRedisWhiteListKey(token), token, mills, TimeUnit.MILLISECONDS);
		}
	}

	private String getRedisWhiteListKey(String token) {
		String uid = getUserIdFromToken(token);
		return JWT_WHITE_LIST_NAMESPACE + uid;
	}

	private String getRedisWhiteListKeyByUserId(String userId) {
		return JWT_WHITE_LIST_NAMESPACE + userId;
	}

	public boolean isTokenInWhiteList(String token) {
		String key = getRedisWhiteListKey(token);
		Boolean hasKey = redisTemplate.hasKey(key);
		if (hasKey == null || !hasKey) {
			return false;
		}
		Object valueToken = redisTemplate.opsForValue().get(key);
		if (valueToken == null) {
			return false;
		}
		return valueToken.equals(token);
	}

	public void removeWhiteList(Integer userId) {
		String key = getRedisWhiteListKeyByUserId(userId + "");
		Boolean hasKey = redisTemplate.hasKey(key);
		if (hasKey == null || !hasKey) {
			return;
		}
		Boolean result = redisTemplate.delete(key);
		if (result == null || !result) {
			throw new LogicException(ErrorCode.SYS_ERROR, "移除redis中的白名单失败");
		}
	}
}
