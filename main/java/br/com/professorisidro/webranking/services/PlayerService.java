/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.professorisidro.webranking.services;

import br.com.professorisidro.webranking.model.MyHibernateUtil;
import br.com.professorisidro.webranking.model.Player;
import com.google.gson.Gson;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author isidro
 */
@Path("/players")
public class PlayerService {
    
    private static final int PLAYER_ADD  = 0;
    private static final int PLAYER_ERR  = 1;
    private static final int RANKING_OK  = 2;
    private static final int RANKING_ERR = 3;
    
    @Path("/add")
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response addNewPlayer(@FormParam("name") String name, @FormParam("points") int points){
        try{
            Session session = MyHibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            //
            Player p = new Player();
            p.setName(name);
            p.setPoints(points);
            session.save(p);
            transaction.commit();
            session.close();
            return buildResponse("Inserido", PLAYER_ADD);
        }
        catch(Exception ex){
            return buildResponse(null, PLAYER_ERR);
        }
    }
    
    @GET
    @Path("/ranking")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRanking(){
        System.out.println("Acessado!");
        try{
            
            Session session = MyHibernateUtil.getSessionFactory().openSession();
            ArrayList<Player> list = (ArrayList<Player>)session.createQuery("FROM Player order by points desc limit 10").list();
            Gson gson = new Gson();
            return buildResponse(gson.toJson(list), PLAYER_ADD);
                    
        }
        catch(Exception ex){
            return buildResponse(null, RANKING_ERR);
        }
    }
    
    
    
    public Response buildResponse(String message, int opcode){
        Status status;
                
        switch(opcode){
           case PLAYER_ADD:
           case RANKING_OK:
               status = Response.Status.OK;
               break;
           case PLAYER_ERR:
           case RANKING_ERR:
               status = Response.Status.NOT_FOUND;
               break;
           default:
               status = Response.Status.INTERNAL_SERVER_ERROR;
               break;
        }
        return Response.status(status)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity(message)
                .header("Access-Control-Max-Age", "1209600")
                .build();
    }
}
