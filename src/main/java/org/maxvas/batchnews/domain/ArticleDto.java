package org.maxvas.batchnews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Новостная статья.
 */
@AllArgsConstructor
@Data
public class ArticleDto {
    /**
     * Дата публикации статьи.
     */
    private LocalDate date;

    /**
     * URL статьи.
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
     * Статус загрузки статьи из интернета.
     */
    private boolean isSuccess;

    /**
     * Описание ошибки.
     */
    private String error;
}
