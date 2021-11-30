package org.maxvas.batchnews.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Get resource from internet with retry.
 */
@Service
@Slf4j
public class DocumentServiceInternal {
    @Retryable(value = IOException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public Document getDocumentRetryInternal(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}