package ScrapeAskStreamSets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class StopWords {
  Set<String> stopwords = new HashSet<>(700);

  StopWords() throws IOException {
    try (InputStream is = StopWords.class.getResourceAsStream("/stopwords")) {
      String word;
      Scanner sc = new Scanner(is).useDelimiter("\n");
      while (sc.hasNext()) {
        word = sc.next();
        System.out.println("word: '" + word + "'");
        stopwords.add(word.toLowerCase().trim());
      }
    }
    System.out.println(stopwords.size() + " StopWords");
  }

  boolean check(String word) {
    return stopwords.contains(word.trim());
  }
}
