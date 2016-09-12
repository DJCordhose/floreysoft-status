package com.floreysoft.status;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// https://cloud.google.com/appengine/docs/java/config/cron
public class TaskServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(TaskServlet.class.getName());
    private TestService testService = new TestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.log(Level.INFO, "Pinging test sample run");
        testService.sampleTests();
    }
}
