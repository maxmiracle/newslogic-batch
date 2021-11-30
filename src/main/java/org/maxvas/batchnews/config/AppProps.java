package org.maxvas.batchnews.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app")
public class AppProps {
    // Dates in ISO format yyyy-mm-dd
    private String startDate;
    private String endDate;

}
