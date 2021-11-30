package org.maxvas.batchnews.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Статья.
 */
@AllArgsConstructor
@Data
public class Article {
    /**
     * Дата публикации.
     */
    private LocalDate date;

    /**
     * Ссылка на статью.
     */
    private String link;

    /**
     * Заголовок статьи.
     */
    private String title;

    /**
     * Текст статьи.
     */
    private String text;

    /**
     * Дата импорта из интернета.
     */
    private LocalDate importDate;

    /**
     * Название источника информации.
     */
    private String dataSource;
}
