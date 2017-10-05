package stream.custompopupsample;

public class Keyword {

    String k_keyword;
    float k_score;

    public Keyword() {}

    public Keyword(String keyword, float score) {
        k_keyword = keyword;
        k_score = score;
    }

    public String getKeyword() { return k_keyword; }
    public float getScore() { return k_score; }

    public void setKeyword(String keyword) { k_keyword = keyword; }
    public void setScore(float score) { k_score = score; }
}
