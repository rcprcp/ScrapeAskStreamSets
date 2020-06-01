package ScrapeAskStreamSets;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.zendesk.client.v2.model.hc.Section;

import java.io.IOException;
import java.util.Arrays;

public class ScrapeAskStreamSets {

  private static final String ZENDESK_EMAIL = "ZENDESK_EMAIL";
  private static final String ZENDESK_TOKEN = "ZENDESK_TOKEN";

  public static void main(String... args) throws IOException {

     StopWords stopwords = new StopWords();
    // Trivial stopwords test:
        System.out.println("blarf " + stopwords.check("blarf"));
        System.out.println("keep " + stopwords.check("keep"));
        System.exit(3);

    ZendeskKB zdkb = new ZendeskKB(System.getenv(ZENDESK_EMAIL), System.getenv(ZENDESK_TOKEN));
    //   sample: "https://ask.streamsets.com/questions/scope:all/sort:activity-desc/page:1/"
    String url = "https://ask.streamsets.com/questions/scope:all/sort:activity-desc/page:";
    for (int page = 1; page < 110; ++page) {
      String thisUrl = url + page + "/";
      Document doc = Jsoup.connect(thisUrl).get();

      Elements questions = doc.select(".short-summary");
      for (Element el : questions) {
        String part = el.attr("id").replace("-", "/");
        String urlw = "https://ask.streamsets.com/" + part;
        Document d1 = Jsoup.connect(urlw).get();

        // this picks up questions and answers.
        Elements content = d1.select(".js-editable-content");
        // if there is a question and an answer the count will be 3.
        // otherwise, this is a question with no answer.
        if (content.size() < 3) {
          break;
        }

        String title = "";
        StringBuilder body = new StringBuilder();
        //first item - put the url in the body
        String link = String.format("<a href=\"%s\">%s</a><p>", urlw, urlw);
        body.append(link);
        // process the question and the answer(s).
        int state = 1;
        for (Element co : content) {
          if (state == 1) {
            title = StringEscapeUtils.escapeHtml4(co.text());
            state = 2;
            continue;  //don't add it to the "body"

          } else if (state == 2) {
            body.append("Question: ");
            state = 3;
            // drop through, add to body.

          } else if (state == 3) {
            body.append("Answer: ");
            // drop through, add to body.

          }

          body.append(StringEscapeUtils.escapeHtml4(co.text()));
          body.append(" <BR/><BR/>");
        }

        // this picks up comments - which have not been accepted as answers.
        content = d1.select(".comment-body");
        for (Element co : content) {
          if (co.text().length() == 0) {
            break;
          }

          body.append("Comment: ");
          body.append(StringEscapeUtils.escapeHtml4(co.text()));
          body.append(" <BR/><BR/>");
        }

        // check if this article is in the KB:
        if (zdkb.alreadyInKB(urlw)) {
          break;
        }


        // not present - so add to KB.
        long id = System.currentTimeMillis();
        KBArticle kb = new KBArticle(id);
        title = KBArticle.cleanUp(title);

        kb.setTitle(KBArticle.cleanUp(title));
        kb.setKeywords(Arrays.asList("plsdeleteit", urlw));

        body.append(kb.showKeywords());
        String footer = kb.createFooter(urlw);
        footer = KBArticle.cleanUp(footer);
        body.append(footer);
        kb.setBody(body.toString());

        // TODO: fix this - hardcoded number.
        // this section number: 360007381273 is the Testing section.
        Section s = new Section();
        s.setId(360007381273L);
        zdkb.postToKB(kb, s);
        System.out.println(urlw);
      }
    }
    zdkb.close();
  }
}
