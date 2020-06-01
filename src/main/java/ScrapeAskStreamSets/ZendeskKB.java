package ScrapeAskStreamSets;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.CustomFieldValue;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.hc.Article;
import org.zendesk.client.v2.model.hc.PermissionGroup;
import org.zendesk.client.v2.model.hc.Section;
import org.zendesk.client.v2.model.hc.UserSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ZendeskKB {

  private static final Logger LOG = LoggerFactory.getLogger(ZendeskKB.class);
  Map<Long, String> customerXref = new HashMap<>();
  private Zendesk zd;
  private Pattern pattern = Pattern.compile("[ \\n\\r\\f\\t]");
   ZendeskKB(String zendeskEmail, String zendeskToken) {
    zd = new Zendesk.Builder("https://streamsets.zendesk.com").setUsername(zendeskEmail).setToken(zendeskToken).build();
  }

  void close() {
    zd.close();
  }

  void postToKB(KBArticle my, Section section) {
    Article a = new Article();
    a.setTitle(my.getTitle());
    a.setBody(my.getBody());
    a.setAuthorId(zd.getAuthenticatedUser().getId());
    a.setLocale("en-us");
    a.setDraft(false);
    a.setSectionId(section.getId());

    // TODO: hard coded.
    a.setPermissionGroupId(247408L);  // maybe should not hard-code this
    // TODO: hard coded.
    a.setUserSegmentId(2438067L);     // maybe should not hard-code this.

    a.setLabelNames(my.getKeywords());
    Article na = zd.createArticle(a, false);
  }

  Iterable<UserSegment> getUserSegment() {
    return zd.getUserSegments();
  }

  Iterable<Section> getSections() {
    return zd.getSections();
  }

  Iterable<PermissionGroup> getPermissions() {
    return zd.getPermissionGroups();
  }

  boolean alreadyInKB(String url) {
     Iterable<Article> articles = zd.getArticlesFromAnyLabels(Collections.singletonList(url));
     for(Article a : articles){
       return true;
     }
     return false;
  }
}

