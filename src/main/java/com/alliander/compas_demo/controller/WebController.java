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

@Controller
public class WebController {

  /* Holding the BaseX session */
  private BaseXClient databaseSession;

  Logger logger = LoggerFactory.getLogger(WebController.class);

  @GetMapping("/query")
  public String getQuery(Model model) throws IOException {

    // Adding a new Query Model to store all the data.
    model.addAttribute("queryModel", new DatabaseQueryModel());

    databaseSession = new BaseXClient("localhost", 1984, "admin", "admin");
    
    return "query";
  }

  @PostMapping("/query")
  public String postQuery(@ModelAttribute DatabaseQueryModel queryModel, Model model) {

    final String queryToRun = queryModel.getQuery();
    String reponse = "";

    logger.debug("postQuery: executing query: {}", queryToRun);
    try {
      try (Query query = databaseSession.query(queryToRun)) {
        while(query.more()) {
          reponse += query.next();
        }
      }
      queryModel.setDatabaseResponse(reponse);
    } catch (IOException exception) {
      queryModel.setDatabaseResponse(exception.getMessage());
    }

    model.addAttribute("queryModel", queryModel);
    return "query";
  }

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

}
