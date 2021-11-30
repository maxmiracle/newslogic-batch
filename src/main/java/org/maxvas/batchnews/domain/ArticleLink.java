package org.maxvas.batchnews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Ссылка на статью с датой публикации.
 */
@AllArgsConstructor
@Data
public class ArticleLink {
    private LocalDate date;
    private String link;
}
