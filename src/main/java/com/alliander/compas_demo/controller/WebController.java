package com.alliander.compas_demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.compas_demo.model.DatabaseQueryModel;
import com.alliander.compas_demo.database.BaseXClient;
import com.alliander.compas_demo.database.BaseXClient.Query;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

@Controller
public class WebController {

  Logger logger = LoggerFactory.getLogger(WebController.class);

  /* Holding the database session */
  private BaseXClient databaseSession;

  /**
   * Initial GET for getting a database session
   * @param model
   * @return the final HTML page
   * @throws IOException
   */
  @GetMapping("/query")
  public String getQuery(Model model) throws IOException {

    // Creating a new query model
    model.addAttribute("queryModel", new DatabaseQueryModel());

    // And creating a new database session
    databaseSession = new BaseXClient("localhost", 1984, "admin", "admin");
    
    return "query";
  }

  /**
   * POST for executing query on database
   * @param queryModel the model holding the database request
   * @param model the global model holding the query model
   * @return the final HTML page
   */
  @PostMapping("/query")
  public String postQuery(@ModelAttribute DatabaseQueryModel queryModel, Model model) {

    final String queryToRun = queryModel.getQuery();
    logger.debug("postQuery: executing query: {}", queryToRun);
    String response = "";

    try {
      try (Query query = databaseSession.query(queryToRun)) {
        while(query.more()) {
          response += query.next();
        }
      }
      queryModel.setDatabaseResponse(response);
    } catch (IOException exception) {
      queryModel.setDatabaseResponse(exception.getMessage());
    }

    model.addAttribute("queryModel", queryModel);
    return "query";
  }

  /**
   * POST for executing command on database
   * @param queryModel the model holding the database request
   * @param model the global model holding the query model
   * @return the final HTML page
   */
  @PostMapping("/execute")
  public String postExecute(@ModelAttribute DatabaseQueryModel queryModel, Model model) {

    final String command = queryModel.getCommand();
    logger.debug("postExecute: executing command: {}", command);

    try {
      queryModel.setDatabaseResponse(databaseSession.execute(command));
    } catch (IOException exception) {
      queryModel.setDatabaseResponse(exception.getMessage());
    }

    model.addAttribute("queryModel", queryModel);
    return "query";
  }

  /**
   * Export a SCD file, so it's downloadable
   * 
   * @param response response in which it's downloadable
   * @return the final HTML page
   * @throws IOException
   */
  @GetMapping("/export")
  public String exportScdFile(HttpServletResponse response) throws IOException {

      String finalScdFile = "";
      response.setContentType("application/xml");
      response.setHeader("Content-Disposition", "attachment;filename=open_substation.scd");

      try {
        databaseSession.execute("open substation");
        // Just querying the whole file
        try (Query query = databaseSession.query("/")) {
          while(query.more()) {
            finalScdFile += query.next();
          }
        }
        databaseSession.execute("close");
      } catch (IOException exception) {
        logger.debug(exception.getMessage());
      }

      ServletOutputStream outStream = response.getOutputStream();
      outStream.println(finalScdFile);
      outStream.flush();
      outStream.close();

      return "query";
    }

}
