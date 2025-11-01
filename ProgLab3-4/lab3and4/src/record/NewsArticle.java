package record;

import enums.DailyNewspaperTypes;

record NewsArticle(String title, String content, DailyNewspaperTypes type) {
    @Override
    public String toString() {
        return "Article Title: " + title + "\nContent: " + content + "\nType: " + type;
    }
}