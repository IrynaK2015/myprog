package irynak.prog;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProcessServlet extends HttpServlet {
    int answerCounter = 0;

    HashMap<String, Integer> ageStats = new HashMap<>();
    HashMap<String, Integer> levelStats = new HashMap<>();

    static final String ANSWER_TEMPLATE = "<html><head><title>Survey statistics</title>"
        + "</head>"
        + "<p>Thank you for your survey</p>"
        + "<p>Total number of answers: %d</p>"
        + "<p>Age statistics:<ul>%s</ul></p>"
        + "<p>Level statistics: <ul>%s</ul></p>"
        + "</body></html>";

    static final String ERROR_TEMPLATE = "<html><head><title>Validation error</title>"
            + "</head>"
            + "<p>%s</p>"
            + "<p>Please  <a href=\"index.html\">try again</a></p>"
            + "</body></html>";

    static final String LIST_TEMPLATE = "<li>%s : %d</li>";

    enum Level {
        JUNIOR,
        MIDDLE,
        SENIOR
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String firstName = request.getParameter("firstname").trim();
            String lastName = request.getParameter("lastname").trim();
            String age = request.getParameter("age");
            String level = request.getParameter("level");

            validateName(firstName);
            validateName(lastName);
            String validAge = getValidAge(age);
            String validLevel = getValidLevel(level);

            addAnswer2Stats(validAge, validLevel);
            response.getWriter().println(getStatsOutput());
        } catch (IOException $ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(
                String.format(ERROR_TEMPLATE, $ex.getMessage())
            );
        }
    }

    private void validateName(String name) throws IOException {
        if (name.isEmpty()) {
            throw new IOException("Please fill name fields");
        }
    }

    private String getValidAge(String age) throws IOException {
        HashMap<Integer, String> ageMap = new HashMap<>();
        ageMap.put(0, "younger then 18");
        ageMap.put(25, "18 - 25");
        ageMap.put(35, "26 - 35");
        ageMap.put(45, "36 - 45");
        ageMap.put(55, "46 - 55");
        ageMap.put(56, "56 and older");
        int ageIdx = Integer.parseInt(age);
        if (!ageMap.containsKey(ageIdx)) {
            throw new IOException("Please select correct experience value");
        }
        if (ageIdx  == 0) {
            throw new IOException("You're too young");
        }

        return ageMap.get(ageIdx);
    }

    private String getValidLevel(String level) throws IOException {
        for (Level enumLevel : Level.values()) {
            if (enumLevel.name().equalsIgnoreCase(level)) {
                return enumLevel.name();
            }
        }

        throw new IOException("Please select correct  level");
    }

    private void addAnswer2Stats(String age, String level) {
        answerCounter++;

        int ageCounter = ageStats.getOrDefault(age, 0);
        ageStats.put(age, ++ageCounter);
        int levelCounter = levelStats.getOrDefault(age, 0);
        levelStats.put(level, ++levelCounter);
    }

    private String getFormattedMap(HashMap<String, Integer> map) {
        String formatted = "";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            formatted = formatted.concat(
                String.format(LIST_TEMPLATE, entry.getKey(), entry.getValue())
            );
        }
        return formatted;
    }

    private String getStatsOutput() {
        return String.format(
                ANSWER_TEMPLATE,
                answerCounter,
                getFormattedMap(ageStats),
                getFormattedMap(levelStats)
        );
    }
}
