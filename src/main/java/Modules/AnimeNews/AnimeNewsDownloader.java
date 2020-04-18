package Modules.AnimeNews;

import Constants.Language;
import Core.Internet.InternetCache;
import Core.Internet.InternetResponse;
import Modules.PostBundle;
import Core.Tools.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class AnimeNewsDownloader {

    final static Logger LOGGER = LoggerFactory.getLogger(AnimeNewsDownloader.class);

    public static AnimeNewsPost getPost(Locale locale) throws InterruptedException, ExecutionException {
        String downloadUrl;
        if (StringTools.getLanguage(locale) == Language.DE) downloadUrl = "https://www.animenachrichten.de/";
        else downloadUrl = "https://www.animenewsnetwork.com/news/";

        InternetResponse internetResponse = InternetCache.getData(downloadUrl, 60 * 14).get();
        if (!internetResponse.getContent().isPresent()) return null;
        String dataString = internetResponse.getContent().get();

        if (StringTools.getLanguage(locale) == Language.DE) return getPostDE(getCurrentPostStringDE(dataString)[0]);
        else return getPostEN(getCurrentPostStringEN(dataString)[0]);
    }

    public static PostBundle<AnimeNewsPost> getPostTracker(Locale locale, String newestTimeString) throws InterruptedException, ExecutionException {
        String downloadUrl;
        if (StringTools.getLanguage(locale) == Language.DE) downloadUrl = "https://www.animenachrichten.de/";
        else downloadUrl = "https://www.animenewsnetwork.com/news/";

        InternetResponse internetResponse = InternetCache.getData(downloadUrl, 60 * 14).get();
        if (!internetResponse.getContent().isPresent()) return null;
        String dataString = internetResponse.getContent().get();

        ArrayList<AnimeNewsPost> postList = new ArrayList<>();
        String[] postStrings;
        if (StringTools.getLanguage(locale) == Language.DE) postStrings = getCurrentPostStringDE(dataString);
        else postStrings = getCurrentPostStringEN(dataString);

        Instant compareTime;
        try {
            compareTime = newestTimeString == null || newestTimeString.isEmpty() ? new Date(0).toInstant() : Instant.parse(newestTimeString);
        } catch (DateTimeParseException e) {
            LOGGER.error("Could not parse post date", e);
            compareTime = Instant.now();
        }
        Instant newestTime = compareTime;

        for(int i = 0; i < 5; i++) {
            String postString = postStrings[i];

            AnimeNewsPost post;
            try {
                if (StringTools.getLanguage(locale) == Language.DE) post = getPostDE(postString);
                else post = getPostEN(postString);
            } catch (NullPointerException e) {
                LOGGER.error("Could not extract news post", e);
                return null;
            }

            if (post.getInstant().isAfter(compareTime)) {
                if (post.getInstant().isAfter(newestTime)) newestTime = post.getInstant();
                if (i == 0 || newestTimeString != null) postList.add(post);
            }
        }

        Collections.reverse(postList);

        return new PostBundle<>(postList, newestTime.toString());
    }

    private static AnimeNewsPost getPostDE(String data) {
        AnimeNewsPost post = new AnimeNewsPost();

        post.setTitle(StringTools.decryptString(StringTools.extractGroups(data, "title=\"", "\"")[0]));
        post.setDescription(StringTools.decryptString(StringTools.extractGroups(data + "</div>", "<div class=\"td-excerpt\">", "</div>")[0]));
        post.setImage(StringTools.extractGroups(data, "data-lazy-srcset=\"", " ")[0]);
        post.setLink(StringTools.extractGroups(data, "<a href=\"", "\"")[0]);

        if (data.contains("#comments\">")) post.setComments(Integer.parseInt(StringTools.extractGroups(data, "#comments\">", "<")[0]));
        else post.setComments(Integer.parseInt(StringTools.extractGroups(data, "#respond\">", "<")[0]));

        String dateString = StringTools.extractGroups(data, "datetime=\"", "\"")[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+00:00'").withZone(ZoneOffset.UTC);
        Instant instant = formatter.parse(dateString, Instant::from);
        post.setInstant(instant);

        post.setAuthor(StringTools.decryptString(StringTools.extractGroups(data, "class=\"td-post-author-name\">", "</a>")[0].split(">")[1]));
        post.setCategory("");

        return post;
    }

    private static AnimeNewsPost getPostEN(String data) {
        AnimeNewsPost post = new AnimeNewsPost();

        data = data.replace("<cite>", "").replace("</cite>", "").replaceFirst("&amp;from=I.MF\">", "").replaceFirst("<a href=\"", "");

        post.setTitle(StringTools.decryptString(StringTools.extractGroups(data, "&amp;from=I.MF\">", "</a>")[0]));
        post.setDescription(StringTools.decryptString(StringTools.extractGroups(data, "<span class=\"full\">― ", "</span>")[0]));
        post.setImage("https://www.animenewsnetwork.com" + StringTools.extractGroups(data, "data-src=\"", "\">")[0]);
        post.setLink("https://www.animenewsnetwork.com" + StringTools.extractGroups(data, "<a href=\"", "\"")[0]);
        post.setComments(Integer.parseInt(StringTools.extractGroups(StringTools.extractGroups(data, "<div class=\"comments\"><a href=\"", "</a></div>")[0], ">", " ")[0]));
        post.setAuthor("");

        String dateString = StringTools.extractGroups(data, "<time datetime=\"", "\"")[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+00:00'").withZone(ZoneOffset.UTC);
        Instant instant = formatter.parse(dateString, Instant::from);
        post.setInstant(instant);

        post.setCategory(StringTools.decryptString(StringTools.extractGroups(data, "<span class=\"topics\">", "</div>")[0]));

        return post;
    }

    private static String[] getCurrentPostStringDE(String str) {
        return StringTools.extractGroups(str, "class=\"td-block-span12\">", "</div></div></div>");
    }

    private static String[] getCurrentPostStringEN(String str) {
        return StringTools.extractGroups(str, "<div class=\"herald box news\"", "<div class=\"herald box news\"");
    }
}