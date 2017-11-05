/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.practice.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.context.ResponseStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Prasanna
 */
@WebServlet(name = "GooglePlusServlet", urlPatterns = {"/authenticate"})
public class GooglePlusServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        JsonObject token = null;
        if(request.getRemoteUser() == null){
        
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                
                try{
                    request.login(username, password);
                    token = generateToken("helloHowAreYou");
                
                }catch(ServletException ex){
                       System.out.println(" "+ex.getMessage());
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                }
        }
        
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        if(null!=token){
        
                    response.setContentType(MediaType.APPLICATION_JSON);
                   PrintWriter pw= response.getWriter();
                   pw.print(token.toString());
        }
    }

    
    private JsonObject generateToken(String secret) throws UnsupportedEncodingException {
    
        Instant now = Instant.now();
        LocalDateTime oneHour = LocalDateTime.now(ZoneId.systemDefault()).plusHours(1);
        
         Claims claims = Jwts.claims();
         claims.put("id", UUID.randomUUID().toString());
         claims.put("email", "prasanna.pawar43@gmail.com");
         claims.put("sub","Hello how are you");
         claims.put("iat", Date.from(now));
         claims.put("exp", Date.from(oneHour.atZone(ZoneId.systemDefault()).toInstant()));
         
         Key key = new SecretKeySpec(secret.getBytes("utf-8"), SignatureAlgorithm.HS256.getJcaName());
         
         String jwt = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, key).compact();
         
         return(Json.createObjectBuilder().add("token",jwt).add("token_type", "bearer").build());
         
    }

}
