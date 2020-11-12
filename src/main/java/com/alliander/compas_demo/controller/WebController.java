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

  Logger logger = LoggerFactory.getLogger(WebController.class);

  @GetMapping("/basex")
  public String basexForm(Model model) {
    logger.debug("basexForm: GET");
    model.addAttribute("queryModel", new DatabaseQueryModel());
    return "basex";
  }

  @PostMapping("/basex")
  public String basexSubmit(@ModelAttribute DatabaseQueryModel queryModel, Model model) throws IOException {
    logger.debug("basexSubmit: POST");
    try(BaseXClient session = new BaseXClient("localhost", 1984, "admin", "admin")) {
      logger.debug("basexSubmit: opening the substation database");
      session.execute("open substation");

      String finalQueryResponse = "";

      try (Query query = session.query(queryModel.getQuery())) {
        while(query.more()) {
          finalQueryResponse += query.next();
        }
      }
      queryModel.setResponse(finalQueryResponse);
    }
    model.addAttribute("queryModel", queryModel);
    return "result";
  }

}
