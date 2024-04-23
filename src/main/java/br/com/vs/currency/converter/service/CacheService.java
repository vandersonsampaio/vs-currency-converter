package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {

    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 * * *")
    public void clearCacheAtMidnight() {
        log.info("Clearing cache at midnight UTC.");
        cacheManager.getCache(CacheConfig.CACHE_NAME).clear();
    }
}
