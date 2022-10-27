package com.fpt.csw_final_client.controller;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.csw_final_client.model.Product;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ProductController {
    private final String REST_API_LIST = "http://localhost:8081/product";
    private final String REST_API_CREATE = "http://localhost:8081/product";
    private final String REST_API_SELL = "http://localhost:8081/product/sell";
    private final String REST_API_GET_BY_ID = "http://localhost:8081/user/";

    private static Client createJerseyRestClient() {
        ClientConfig clientConfig = new ClientConfig();

        // Config logging for client side
        clientConfig.register( //
                new LoggingFeature( //
                        Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), //
                        Level.INFO, //
                        LoggingFeature.Verbosity.PAYLOAD_ANY, //
                        10000));

        return ClientBuilder.newClient(clientConfig);
    }

    @GetMapping
    public String index(Model model){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_LIST);
        List<Product> ls = target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        System.out.println(ls);
        model.addAttribute("lsProduct", ls);
        return "index";
    }

    @GetMapping("saveproduct")
    public String saveUser(){
        return "create-product";
    }

    @PostMapping("saveproduct")
    public String saveUser(@RequestParam String name,
                           @RequestParam double price,
                           @RequestParam int quantity) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);

        String jsonProduct = convertToJson(product);
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_CREATE);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonProduct, MediaType.APPLICATION_JSON));
        return "redirect:/";
    }

    @GetMapping("sellproduct")
    public String sellProduct(Integer id, Model model){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_LIST);
        List<Product> ls = target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        System.out.println(ls);
        model.addAttribute("lsProduct", ls);
        return "sell-product";
    }

    @PostMapping("sellproduct")
    public String sellProduct(Integer id, @RequestParam Integer quantity) {
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_SELL + "?id=" + id + "&&quantity=" + quantity);
        return "redirect:/";
    }

    private static String convertToJson(Product product) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(product);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
