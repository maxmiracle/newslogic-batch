package org.maxvas.batchnews.config;

import lombok.extern.slf4j.Slf4j;
import org.maxvas.batchnews.domain.ArticleDto;
import org.maxvas.batchnews.entity.Article;
import org.maxvas.batchnews.processor.ArticleProcessor;
import org.maxvas.batchnews.reader.TheGuaridianNewsItemReader;
import org.maxvas.batchnews.service.ListArticlesService;
import org.maxvas.batchnews.service.TheGuardianArticleService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
public class ArticleJobConfig {
    public static final String START_DATE_PARAM = "startDate";
    public static final String END_DATE_PARAM = "endDate";
    public static final String IMPORT_THE_GUARDIAN_ARTICLES_JOB = "importTheGuardianArticlesJob";
    private static final int CHUNK_SIZE = 5;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @StepScope
    @Bean
    public TheGuaridianNewsItemReader itemReader(
            @Value("#{jobParameters['" + START_DATE_PARAM + "']}") String startDateParam,
            @Value("#{jobParameters['" + END_DATE_PARAM + "']}") String endDateParam,
            ListArticlesService listArticlesService,
            TheGuardianArticleService theGuardianArticleService) {
        LocalDate startDate = LocalDate.parse(startDateParam, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(endDateParam, DateTimeFormatter.ISO_LOCAL_DATE);
        return new TheGuaridianNewsItemReader(startDate, endDate, listArticlesService, theGuardianArticleService);
    }

    @StepScope
    @Bean
    public ArticleProcessor itemProcessor() {
        return new ArticleProcessor("TheGuardian");
    }

    @StepScope
    @Bean
    public MongoItemWriter<Article> itemWriter() {
        MongoItemWriter<Article> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("articles");
        return writer;
    }


    @Bean
    public Job importTheGuardianArticlesJob(Step transformArticleStep) {
        return jobBuilderFactory.get(IMPORT_THE_GUARDIAN_ARTICLES_JOB)
                .incrementer(new RunIdIncrementer())
                .flow(transformArticleStep)
                .end()
                .listener(new JobExecutionListener() {
            @Override
            public void beforeJob(@NonNull JobExecution jobExecution) {
                log.info("Старт загрузки статей");
            }

            @Override
            public void afterJob(@NonNull JobExecution jobExecution) {
                log.info("Конец загрузки статей");
            }
        }).build();
    }

    @Bean
    public Step transformArticleStep(TheGuaridianNewsItemReader itemReader, ArticleProcessor itemProcessor, MongoItemWriter<Article> itemWriter) {
        return stepBuilderFactory
                .get("transformArticleStep")
                .<ArticleDto, Article>chunk(CHUNK_SIZE)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter).build();
    }

}
