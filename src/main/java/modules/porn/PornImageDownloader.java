package modules.porn;

import core.internet.HttpResponse;
import core.internet.InternetCache;
import core.utils.InternetUtil;
import core.utils.NSFWUtil;
import core.utils.StringUtil;
import core.utils.TimeUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PornImageDownloader {

    public static Optional<PornImage> getPicture(String domain, String searchTerm, String searchTermExtra, String imageTemplate, boolean animatedOnly, boolean canBeVideo, boolean explicit, ArrayList<String> additionalFilters, ArrayList<String> usedResults) throws IOException, InterruptedException, ExecutionException {
        return getPicture(domain, searchTerm, searchTermExtra, imageTemplate, animatedOnly, canBeVideo, explicit,2, false, additionalFilters, usedResults);
    }

    public static Optional<PornImage> getPicture(String domain, String searchTerm, String searchTermExtra, String imageTemplate, boolean animatedOnly, boolean canBeVideo, boolean explicit, int remaining, boolean softMode, ArrayList<String> additionalFilters, ArrayList<String> usedResults) throws IOException, InterruptedException, ExecutionException {
        while(searchTerm.contains("  ")) searchTerm = searchTerm.replace("  ", " ");
        searchTerm = searchTerm.replace(", ", ",");
        searchTerm = searchTerm.replace("; ", ",");

        String searchTermEncoded = URLEncoder.encode(
                searchTerm
                        .replace(",", " ")
                        .replace(" ", softMode ? "~ " : " ") +
                        (softMode ? "~" : "") +
                        searchTermExtra, "UTF-8"
        );

        String url = "https://"+domain+"/index.php?page=dapi&s=post&q=index&tags=" + searchTermEncoded;
        String data = InternetCache.getDataShortLived(url).get().getContent().get();

        if (!data.contains("count=\"")) return Optional.empty();
        int count = Math.min(200 * 100, Integer.parseInt(StringUtil.extractGroups(data, "count=\"", "\"")[0]));

        if (count == 0) {
            if (!softMode) {
                return getPicture(domain, searchTerm.replace(" ", "_"), searchTermExtra, imageTemplate, animatedOnly, canBeVideo, explicit, remaining, true, additionalFilters, usedResults);
            } else if (remaining > 0) {
                if (searchTerm.contains(" "))
                    return getPicture(domain, searchTerm.replace(" ", "_"), searchTermExtra, imageTemplate, animatedOnly, canBeVideo, explicit, remaining - 1, false, additionalFilters, usedResults);
                else if (searchTerm.contains("_"))
                    return getPicture(domain, searchTerm.replace("_", " "), searchTermExtra, imageTemplate, animatedOnly, canBeVideo, explicit, remaining - 1, false, additionalFilters, usedResults);
            }

            return Optional.empty();
        }

        Random r = new Random();
        int page = r.nextInt(count)/100;
        if (searchTermEncoded.length() == 0) page = 0;

        return getPictureOnPage(domain, searchTermEncoded, page, imageTemplate, animatedOnly, canBeVideo, explicit, additionalFilters, usedResults);
    }

    private static Optional<PornImage> getPictureOnPage(String domain, String searchTerm, int page, String imageTemplate, boolean animatedOnly, boolean canBeVideo, boolean explicit, ArrayList<String> additionalFilters, ArrayList<String> usedResults) throws InterruptedException, ExecutionException {
        String url = "https://" + domain + "/index.php?page=dapi&s=post&q=index&json=1&tags=" + searchTerm + "&pid=" + page;
        HttpResponse httpResponse = InternetCache.getDataShortLived(url).get();

        if (!httpResponse.getContent().isPresent()) {
            return Optional.empty();
        }

        JSONArray data = new JSONArray(httpResponse.getContent().get());

        int count = Math.min(data.length(), 100);
        if (count == 0) {
            return Optional.empty();
        }

        ArrayList<PornImageMeta> pornImages = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            JSONObject postData = data.getJSONObject(i);

            String imageUrl = postData.getString(postData.has("file_url") ? "file_url" : "image");

            long score = 1;
            boolean postIsImage = InternetUtil.urlContainsImage(imageUrl);
            boolean postIsGif = imageUrl.endsWith("gif");

            try {
                score = postData.getInt("score");
            } catch (JSONException e) {
                //Ignore
            }

            boolean isExplicit = postData.getString("rating").startsWith("e");

            if ((postIsImage || canBeVideo) &&
                    (!animatedOnly || postIsGif || !postIsImage) &&
                    score >= 0 &&
                    !NSFWUtil.stringContainsBannedTags(postData.getString("tags"), additionalFilters) &&
                    isExplicit == explicit
            ) {
                pornImages.add(new PornImageMeta(imageUrl, score, i));
            }
        }

        return Optional.ofNullable(PornFilter.filter(domain, searchTerm, pornImages, usedResults))
                .map(pornImageMeta -> getSpecificPictureOnPage(domain, data.getJSONObject(pornImageMeta.getIndex()), imageTemplate));
    }

    private static PornImage getSpecificPictureOnPage(String domain, JSONObject postData, String imageTemplate) {
        String postURL = "https://"+domain+"/index.php?page=post&s=view&id=" + postData.getInt("id");

        Instant instant;
        if (postData.has("created_at")) {
            instant = TimeUtil.parseDateString(postData.getString("created_at"));
        } else instant = Instant.now();

        String fileURL;
        if (postData.has("file_url")) fileURL = postData.getString("file_url");
        else fileURL = imageTemplate.replace("%d", postData.get("directory").toString()).replace("%f", postData.getString("image"));

        if (fileURL.contains("?")) fileURL = fileURL.split("\\?")[0];

        int score = 0;
        try {
            score = postData.getInt("score");
        } catch (JSONException e) {
            //Ignore
        }

        return new PornImage(fileURL, postURL, score, instant, !InternetUtil.urlContainsImage(fileURL) && !fileURL.endsWith("gif"));
    }
}
