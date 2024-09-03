package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TimeController {

    @GetMapping("/getTimeStories")
    public List<Story> getLatestStories() {
        List<Story> stories = new ArrayList<>();
        try {
            // Fetch the HTML content from Time.com
            RestTemplate restTemplate = new RestTemplate();
            String htmlContent = restTemplate.getForObject("https://time.com", String.class);

            // Print the first 2000 characters of the HTML content for inspection
            if (htmlContent != null && !htmlContent.isEmpty()) {
                System.out.println(htmlContent.substring(0, Math.min(htmlContent.length(), 2000)));
                extractStories(htmlContent, stories);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stories;
    }

    private void extractStories(String html, List<Story> stories) {
        // Define possible start and end tags
        String titleStartTag = "<h2>";
        String titleEndTag = "</h2>";
        String linkStartTag = "href=\"";
        String linkEndTag = "\"";

        int titleStartIndex = 0;
        int storiesCount = 0;

        while (storiesCount < 6) {
            titleStartIndex = html.indexOf(titleStartTag, titleStartIndex);
            if (titleStartIndex == -1) break;

            int titleEndIndex = html.indexOf(titleEndTag, titleStartIndex);
            if (titleEndIndex == -1) break;

            // Extract the title
            String title = html.substring(titleStartIndex + titleStartTag.length(), titleEndIndex).trim();

            // Find the link associated with this title
            int linkStartIndex = html.indexOf(linkStartTag, titleEndIndex);
            if (linkStartIndex == -1) break;

            linkStartIndex += linkStartTag.length();
            int linkEndIndex = html.indexOf(linkEndTag, linkStartIndex);
            if (linkEndIndex == -1) break;

            // Extract the link
            String link = html.substring(linkStartIndex, linkEndIndex).trim();

            // Ensure the link is a full URL
            if (!link.startsWith("http")) {
                link = "https://time.com" + link;
            }

            // Print debug information
            System.out.println("Title: " + title);
            System.out.println("Link: " + link);

            // Add the story if within the limit
            stories.add(new Story(title, link));
            storiesCount++;

            // Move to the next potential title
            titleStartIndex = titleEndIndex;
        }
    }


    static class Story {
        private String title;
        private String link;

        public Story(String title, String link) {
            this.title = title;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }
    }
}


