package irynak.prog;

import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;

public class ProcessServlet extends HttpServlet {
    static final String ANSWER_TEMPLATE = "<html><head><title>Your answer</title></head>"
        + "<p>I'm %s %s.<br/>My working experience is %s.<br/>I reached %s level.</p>"
        + "</body></html>";

    enum Level {
        JUNIOR,
        MIDDLE,
        SENIOR
    }



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String workYears = request.getParameter("workyears");
        String level = request.getParameter("level");

        validateNames(firstName, lastName);
        validateWorkYears(workYears);
        validateLevel(level);

        response.getWriter().println(
            String.format(ANSWER_TEMPLATE, firstName, lastName, workYears, level)
        );
    }

    private void validateNames(String firstName, String lastName) throws IOException {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new IOException("Please fill name fields");
        }
    }

    private void validateWorkYears(String workYears) throws IOException {
        HashMap<Integer, String> years = new HashMap<>();
        years.put(1, "up to 1 year");
        years.put(3, "up to 3 years");
        years.put(5, "up to 5 years");
        years.put(10, "up to 10 years");
        years.put(11, "more than 10 years");
        int year = Integer.parseInt(workYears);
        if (!years.containsKey(year)) {
            throw new IOException("Please select correct experience value");
        }
    }

    private void validateLevel(String level) throws IOException {
        for (Level enumLevel : Level.values()) {
            if (enumLevel.name().equalsIgnoreCase(level)) {
                return;
            }
        }

        throw new IOException("Please select correct  level");
    }
}
