package com.aemtraining.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.jcr.Session;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class AdventuresPaginationModel {

    // Constant relative path from page root
    private static final String CAROUSEL_ABS_REL = "jcr:content/root/container/carousel";
    private static final String PN_FILE_REF = "fileReference";

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String parentPage;     // dialog: ./parentPage  (e.g. /content/wknd/us/en/adventures)

    @ValueMapValue
    private Integer pageSize;      // dialog: ./pageSize (default 5)

    @ValueMapValue
    private Boolean deep;          // dialog: ./deep (true/false)

    private int effectivePageSize = 5;
    private int currentPageNo = 1;
    private int totalPages = 1;

    private List<Card> allCards = Collections.emptyList();
    private List<Card> pageCards = Collections.emptyList();
    private List<PageLink> links = Collections.emptyList();

    @PostConstruct
    protected void init() {
        effectivePageSize = (pageSize != null && pageSize > 0) ? pageSize : 5;

        currentPageNo = NumberUtils.toInt(request.getParameter("pageno"), 1);
        if (currentPageNo < 1) currentPageNo = 1;

        if (StringUtils.isBlank(parentPage)) {
            computePaginationAndLinks();
            return;
        }

        ResourceResolver resolver = request.getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            computePaginationAndLinks();
            return;
        }

        Page parent = pageManager.getPage(parentPage);
        if (parent == null) {
            computePaginationAndLinks();
            return;
        }

        // 1) Collect pages
        List<Page> pages = collectPages(parent, Boolean.TRUE.equals(deep));

        // 2) Sort newest first (cq:lastModified else jcr:created)
        pages.sort((a, b) -> {
            Date da = getPageDate(a);
            Date db = getPageDate(b);
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        // 3) Build cards (title/desc/date/image/url)
        List<Card> temp = new ArrayList<>();
        for (Page p : pages) {
            Card c = toCard(p, resolver);
            if (c != null) temp.add(c);
        }
        allCards = Collections.unmodifiableList(temp);

        // 4) Pagination + slice
        computePaginationAndLinks();
        sliceForCurrentPage();
    }

    private List<Page> collectPages(Page parent, boolean deepList) {
        List<Page> result = new ArrayList<>();
        Iterator<Page> it = parent.listChildren(new PageFilter(), deepList);
        while (it.hasNext()) {
            Page child = it.next();
            if (child != null) result.add(child);
        }
        return result;
    }

    private Card toCard(Page p, ResourceResolver resolver) {
        if (p == null) return null;
        // title: prefer jcr:title
        String title = StringUtils.defaultIfBlank(p.getTitle(), p.getName());

        // description from jcr:content (jcr:description)
        String desc = "";
        Resource content = p.getContentResource();
        if (content != null) {
            desc = StringUtils.defaultString(content.getValueMap().get("jcr:description", String.class));
        }

        // date: cq:lastModified else jcr:created
        Date date = getPageDate(p);

        // image: /jcr:content/root/container/carousel -> first item_* -> fileReference
        String image = extractFirstCarouselImage(p.getPath(), resolver);

        String url = p.getPath() + ".html";
        return new Card(title, desc, url, image, date);
    }

    private String extractFirstCarouselImage(String pagePath, ResourceResolver resolver) {
        if (StringUtils.isBlank(pagePath) || resolver == null) return null;

        Resource carousel = resolver.getResource(pagePath + "/" + CAROUSEL_ABS_REL);
        if (carousel == null) return null;

        Iterator<Resource> it = carousel.getChildren().iterator();
        if (!it.hasNext()) return null;

        Resource firstItem = it.next(); // item_...
        return firstItem.getValueMap().get(PN_FILE_REF, String.class);
    }

    private Date getPageDate(Page p) {
        if (p == null) return null;

        // cq:lastModified (Page API uses cq:lastModified internally)
        if (p.getLastModified() != null && p.getLastModified().getTime() != null) {
            return p.getLastModified().getTime();
        }

        // fallback to jcr:created
        Resource content = p.getContentResource();
        if (content != null) {
            return content.getValueMap().get("cq:lastModified", Date.class);
        }
        return null;
    }

    private void computePaginationAndLinks() {
        int totalItems = (allCards != null) ? allCards.size() : 0;

        totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) effectivePageSize));
        if (currentPageNo > totalPages) currentPageNo = totalPages;

        List<PageLink> tempLinks = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            tempLinks.add(new PageLink(i, buildUrlWithPageNo(i)));
        }
        links = Collections.unmodifiableList(tempLinks);
    }

    private void sliceForCurrentPage() {
        if (allCards == null || allCards.isEmpty()) {
            pageCards = Collections.emptyList();
            return;
        }

        int from = (currentPageNo - 1) * effectivePageSize;
        int to = Math.min(from + effectivePageSize, allCards.size());

        if (from < 0 || from >= allCards.size()) {
            pageCards = Collections.emptyList();
            return;
        }

        pageCards = Collections.unmodifiableList(allCards.subList(from, to));
    }

    private String buildUrlWithPageNo(int pageNo) {
        if (pageNo < 1) pageNo = 1;

        String base = request.getRequestURI();
        String qs = request.getQueryString();

        Map<String, List<String>> params = parseQueryString(qs);

        params.remove("pageno");
        params.put("pageno", Collections.singletonList(String.valueOf(pageNo)));

        String newQs = buildQueryString(params);
        return newQs.isEmpty() ? base : base + "?" + newQs;
    }

    private Map<String, List<String>> parseQueryString(String qs) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        if (StringUtils.isBlank(qs)) return map;

        for (String part : qs.split("&")) {
            if (StringUtils.isBlank(part)) continue;

            String[] kv = part.split("=", 2);
            String k = urlDecode(kv[0]);
            String v = kv.length > 1 ? urlDecode(kv[1]) : "";

            map.computeIfAbsent(k, key -> new ArrayList<>()).add(v);
        }
        return map;
    }

    private String buildQueryString(Map<String, List<String>> params) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : params.entrySet()) {
            for (String v : e.getValue()) {
                parts.add(urlEncode(e.getKey()) + "=" + urlEncode(v));
            }
        }
        return String.join("&", parts);
    }

    private String urlEncode(String s) {
        return URLEncoder.encode(StringUtils.defaultString(s), StandardCharsets.UTF_8);
    }

    private String urlDecode(String s) {
        return URLDecoder.decode(StringUtils.defaultString(s), StandardCharsets.UTF_8);
    }

    // ---------- HTL Getters ----------

    public List<Card> getCards() {
        return pageCards != null ? pageCards : Collections.emptyList();
    }

    public List<PageLink> getLinks() {
        return links != null ? links : Collections.emptyList();
    }

    public int getPageno() {
        return currentPageNo;
    }

    public boolean getHasPrev() {
        return currentPageNo > 1;
    }

    public boolean getHasNext() {
        return currentPageNo < totalPages;
    }

    public String getPrevUrl() {
        return buildUrlWithPageNo(currentPageNo - 1);
    }

    public String getNextUrl() {
        return buildUrlWithPageNo(currentPageNo + 1);
    }

    // ---------- POJOs (DTOs) ----------

    public static class Card {
        private final String title;
        private final String description;
        private final String url;
        private final String image;
        private final Date date;

        public Card(String title, String description, String url, String image, Date date) {
            this.title = title;
            this.description = description;
            this.url = url;
            this.image = image;
            this.date = date;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getUrl() { return url; }
        public String getImage() { return image; }
        public Date getDate() { return date; }
    }

    public static class PageLink {
        private final int pageNo;
        private final String url;

        public PageLink(int pageNo, String url) {
            this.pageNo = pageNo;
            this.url = url;
        }

        public int getPageNo() { return pageNo; }
        public String getUrl() { return url; }
    }
}


