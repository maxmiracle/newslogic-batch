package org.maxvas.batchnews.shell;

import lombok.RequiredArgsConstructor;
import org.maxvas.batchnews.config.AppProps;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import static org.maxvas.batchnews.config.ArticleJobConfig.*;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final AppProps appProps;
    private final Job importTheGuardianArticlesJob;

    private final JobLauncher jobLauncher;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    @ShellMethod(value = "startImportArticles", key = "sa")
    public void startImportArticles() throws Exception {
        JobExecution execution = jobLauncher.run(importTheGuardianArticlesJob, new JobParametersBuilder().addString(START_DATE_PARAM, appProps.getStartDate()).addString(END_DATE_PARAM, appProps.getEndDate()).toJobParameters());
        System.out.println(execution);
    }

    @ShellMethod(value = "startImportArticlesWithJobOperator", key = "sa-jo")
    public void startMigrationJobWithJobOperator() throws Exception {
        Long executionId = jobOperator.start(IMPORT_THE_GUARDIAN_ARTICLES_JOB, START_DATE_PARAM + "=" + appProps.getStartDate() + "\n" + END_DATE_PARAM + "=" + appProps.getEndDate());
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "showInfo", key = "i")
    public void showInfo() {
        System.out.println(jobExplorer.getJobNames());
        System.out.println(jobExplorer.getLastJobInstance(IMPORT_THE_GUARDIAN_ARTICLES_JOB));
    }
}
