package org.maxvas.batchnews.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.maxvas.batchnews.domain.ArticleDto;
import org.maxvas.batchnews.domain.ArticleLink;
import org.maxvas.batchnews.service.ListArticlesService;
import org.maxvas.batchnews.service.TheGuardianArticleService;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class TheGuaridianNewsItemReader implements ItemReader<ArticleDto> {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final TheGuardianArticleService theGuardianArticleService;
    private final ListArticlesService listArticlesService;
    private List<ArticleLink> links;
    private Iterator<ArticleLink> linkIterator;

    public TheGuaridianNewsItemReader(LocalDate startDate, LocalDate endDate, ListArticlesService listArticlesService, TheGuardianArticleService theGuardianArticleService) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.listArticlesService = listArticlesService;
        this.theGuardianArticleService = theGuardianArticleService;
    }

    /**
     * Initialize list of links to download for range of dates.
     *
     * @param stepExecution
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("Listing links started {} - {}", startDate, endDate);
        links = listArticlesService.getLinksByRange(startDate, endDate);
        linkIterator = links.listIterator();
        log.info("Listing finished. Encountered: {} ", links.size());
    }

    @Override
    public ArticleDto read() throws Exception {
        if (!linkIterator.hasNext()) {
            return null;
        }
        ArticleLink link = linkIterator.next();
        try {
            return theGuardianArticleService.getArticleData(link);
        } catch (Exception ex) {
            log.error("Read error occured. Link {}\n Error: {}", link, ExceptionUtils.getStackTrace(ex));
            return new ArticleDto(link.getDate(), link.getLink(), null, null, false, ExceptionUtils.getStackTrace(ex));
        }
    }
}
