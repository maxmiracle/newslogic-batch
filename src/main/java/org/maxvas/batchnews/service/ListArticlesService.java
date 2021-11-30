package org.maxvas.batchnews.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.maxvas.batchnews.domain.ArticleLink;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ListArticlesService {

    static final String theGuardianSitUrlBase = "https://theguardian.com/world/russia";
    static final String allPostfix = "all";
    static final DateTimeFormatter urlPartFormatter = DateTimeFormatter.ofPattern("yyyy/LLL/dd").withLocale(Locale.ENGLISH);
    private final DocumentService documentService;

    /**
     * 3 november 2020 should be formatted like "2020/nov/03"
     *
     * @param date date to request
     * @return formatted part of URL
     */
    public static String getUrlDate(LocalDate date) {
        return urlPartFormatter.format(date).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Get link like https://theguardian.com/world/russia/2020/nov/03/all
     *
     * @param date date to request
     * @return url string
     */
    public static String getUrlAllByDate(LocalDate date) {
        return String.join("/", theGuardianSitUrlBase, getUrlDate(date), allPostfix);
    }

    /**
     * Get date from link url
     *
     * @param link url, содержащая дату в пути.
     * @return
     */
    public static LocalDate getDateFromLink(String link) {
        String regexp = "^.*/([^/]*)/([^/]*)/([^/]*)/.*$";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(link);
        if (!m.matches()) {
            throw new RuntimeException("Cant extract date from link");
        }
        String year = m.group(1);
        String mon = m.group(2);
        String dayOfMonth = m.group(3);
        LocalDate date = LocalDate.parse(String.join("/", year, mon.substring(0, 1).toUpperCase() + mon.substring(1), dayOfMonth), urlPartFormatter);
        return date;
    }

    /**
     * Get all links for the dedicated date
     *
     * @param date
     * @return
     */
    public List<String> getLinksByDate(LocalDate date) {
        String url = getUrlAllByDate(date);
        Document doc = documentService.getDocument(url);
        Elements newLinks = doc.select("a.u-faux-block-link__overlay");
        return newLinks.stream().map(link -> link.absUrl("href")).collect(Collectors.toList());
    }

    /**
     * Get all links for a range of dates.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return List of article links to download.
     */
    public List<ArticleLink> getLinksByRange(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate)
                .flatMap(date -> getLinksByDate(date).stream())
                .distinct()
                .map(
                        link -> {
                            try{
                                return new ArticleLink(getDateFromLink(link), link);
                            }
                            catch (Exception ex) {
                                log.warn("Get date from link error. Skip link: {},\n Error: {}", link, ExceptionUtils.getStackTrace(ex));
                                return null;
                            }
                        })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing((ArticleLink a) -> a.getDate())
                        .thenComparing(a -> a.getLink())).collect(Collectors.toList());
    }


}
