package com.zsx.cstfilemanage.infrastructure.persistence.redis;

import com.zsx.cstfilemanage.common.util.JsonUtil;
import com.zsx.cstfilemanage.domain.model.entity.UploadSession;
import com.zsx.cstfilemanage.domain.repository.UploadSessionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UploadSessionRedisRepository
        implements UploadSessionRepository {

    private static final String PREFIX = "upload:session:";

    private final RedisTemplate<String, String> redisTemplate;

    public UploadSessionRedisRepository(
            RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(UploadSession session) {
        redisTemplate.opsForValue().set(
                PREFIX + session.getUploadId(),
                JsonUtil.toJson(session),
                15,
                TimeUnit.MINUTES
        );
    }

    @Override
    public UploadSession get(String uploadId) {
        String json =
                redisTemplate.opsForValue().get(PREFIX + uploadId);
        return json == null ? null :
                JsonUtil.fromJson(json, UploadSession.class);
    }
}
