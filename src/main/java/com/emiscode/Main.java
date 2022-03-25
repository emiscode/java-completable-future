package com.emiscode;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger.getGlobal().log(Level.INFO, "Starts");

        // Data - webPageLinks
        List<String> webPageLinks = Arrays.asList(
                "https://www.python.org/",
                "https://www.java.com/pt-BR/",
                "https://nodejs.org/en/",
                "https://www.typescriptlang.org/",
                "https://developer.mozilla.org/pt-BR/docs/Web/JavaScript"
        );

        // Download contents of all the web pages asynchronously
        List<CompletableFuture<String>> pageContentFutures = webPageLinks.stream()
                .map(Main::downloadWebPage)
                .collect(Collectors.toList());

        // Create a combined Future using allOf()
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                pageContentFutures.toArray(new CompletableFuture[0])
        );

        // When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
        CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(v ->
                pageContentFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));

        // Count the number of web pages having the "www" keyword.
        CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(pageContents ->
                pageContents.stream().filter(pageContent -> pageContent.contains("www")).count()
        );

        Logger.getGlobal().log(Level.INFO, "Number of Web Pages having www keyword - {}", countFuture.get());

        Logger.getGlobal().log(Level.INFO, "Ends");
    }

    static CompletableFuture<String> downloadWebPage(String pageLink) {
        return CompletableFuture.supplyAsync(() -> {
            Logger.getGlobal().log(Level.INFO, pageLink);
            return pageLink;
        });
    }
}
