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

@Controller
public class WebController {

  /* Holding the BaseX session */
  private BaseXClient baseXSession;

  Logger logger = LoggerFactory.getLogger(WebController.class);

  @GetMapping("/query")
  public String basexForm(Model model) throws IOException {

    // Adding a new Query Model to store all the data.
    model.addAttribute("queryModel", new DatabaseQueryModel());

    baseXSession = new BaseXClient("localhost", 1984, "admin", "admin");
    baseXSession.execute("open substation");
    
    return "query";
  }

  @PostMapping("/query")
  public String basexSubmit(@ModelAttribute DatabaseQueryModel queryModel, Model model) throws IOException {

    final String queryToRun = queryModel.getQuery();
    String finalQueryResponse = "";

    logger.debug("basexSubmit: executing query: {}", queryToRun);

    try (Query query = baseXSession.query(queryToRun)) {
      while(query.more()) {
        finalQueryResponse += query.next();
      }
    }
    queryModel.setResponse(finalQueryResponse);

    model.addAttribute("queryModel", queryModel);
    return "query";
  }

}
