package ScrapeAskStreamSets;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.TimeZone;

public class KBArticle {
  private long id;

  private String title = "";
  private String body = "";
  private String version = "";
  private List<String> keywords = new ArrayList<>();

  KBArticle(long id) {
    this.id = id;
  }

  String getTitle() {
    return this.title;
  }

  void setTitle(String title) {
    this.title = title;
  }

  void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }
  List<String> getKeywords() {
    return keywords;
  }

  Long getId() {
    return this.id;
  }

  String getBody() {
    return this.body;
  }

  void setBody(String body) {
    this.body = body;
  }

  String getVersion() {
    return this.version;
  }

  void setVersion(String version) {
    this.version = version;
  }

  String showKeywords() {
    // append the keywords to the bottom of the body.
    StringJoiner sj = new StringJoiner(", ", "Keywords: ", "");
    if (keywords.size() > 0) {
      for (String s : keywords) {
        sj.add(s);
      }
    }
    return "<br></br>" + sj.toString() + "<br></br>";
  }

  String createFooter(String uri) {
     // append the version to the bottom of the body.
    String footer = "";
    if (!StringUtils.isEmpty(version)) {
      footer = version + "\n";
    }

    // appends a date stamp and ZD ticket.
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    footer = "Created: " + sdf.format(new Date()) + "\n" + "Xref: " + id + "\n";
    return footer;
  }

  static String cleanUp(String input) {

    input = input.replace("_", " ");
    input = WordUtils.capitalize(input);
    input = input.replaceAll(" O$", " Origin");
    input = input.replaceAll(" D$", " Destination");
    input = input.replaceAll(" P$", " Processor");
    input = input.replace(" - ", "-");
    input = input.replace("Sdc", "SDC");
    input = input.replace("SCH", "SCH");
    input = input.replace("\\n", "<br>");
    return input;
  }

}
