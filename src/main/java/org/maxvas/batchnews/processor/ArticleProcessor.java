package org.maxvas.batchnews.processor;

import org.maxvas.batchnews.domain.ArticleDto;
import org.maxvas.batchnews.entity.Article;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;


public class ArticleProcessor implements ItemProcessor<ArticleDto, Article> {

    private final String dataSource;
    private LocalDate processingDate;

    public ArticleProcessor(String dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeStep
    public void init() {
        processingDate = LocalDate.now();
    }

    @Override
    public Article process(ArticleDto articleDto) throws Exception {
        if (!articleDto.isSuccess()) {
            return null;
        }
        return new Article(articleDto.getDate(), articleDto.getLink(), articleDto.getTitle(), articleDto.getText(), processingDate, dataSource);
    }
}
