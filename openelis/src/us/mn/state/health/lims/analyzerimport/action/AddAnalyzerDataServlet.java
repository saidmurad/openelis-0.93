package us.mn.state.health.lims.analyzerimport.action;

import us.mn.state.health.lims.analyzerimport.analyzerreaders.*;
import us.mn.state.health.lims.common.action.IActionConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import us.mn.state.health.lims.login.dao.LoginDAO;
import us.mn.state.health.lims.login.daoimpl.LoginDAOImpl;
import us.mn.state.health.lims.login.valueholder.Login;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

public class AddAnalyzerDataServlet extends HttpServlet {

    private String systemUserId;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader bufferedReader = request.getReader();
        JSONTokener tokener = new JSONTokener(bufferedReader);

//        final String authorization = request.getHeader("Authorization");
//        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
//            // Authorization: Basic base64credentials
//            String base64Credentials = authorization.substring("Basic".length()).trim();
//            byte[] credDecoded = DatatypeConverter.parseBase64Binary(base64Credentials);
//            //byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
//            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
//            // credentials = username:password
//            final String[] values = credentials.split(":", 2);
//            String user = values[0];
//            String password = values[1];
//            if( !userValid(user, password)){
//                response.getWriter().print("invalid user/password");
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                return;
//            }
//        } else {
//            response.getWriter().print("Authentication is required");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }

        try {
            JSONObject json = new JSONObject(tokener);
            String analyzerName =(String)  json.get("machineName");
            if (analyzerName.toLowerCase().contains("dxh 800")) {
                DxH800SingleParametreReader reader = new DxH800SingleParametreReader();
                reader.insertResult(json);
            } else if (analyzerName.toLowerCase().contains("advia 560")) {
                Advia560Reader reader = new Advia560Reader();
                reader.insertResult(json);
            } else if (analyzerName.toLowerCase().contains("exl 200")) {
                Exl200Reader reader = new Exl200Reader();
                reader.insertResult(json);
            } else if (analyzerName.toLowerCase().contains("abx es 60")) {
                AbxMicrosReader reader = new AbxMicrosReader();
                reader.insertResult(json);
            } else if (analyzerName.toLowerCase().contains("dxi 800")) {
                Dxi800Reader reader = new Dxi800Reader();
                reader.insertResult(json);
            } else if (analyzerName.contains("SysmexXN550")) {
                SysmexXNReader reader = new SysmexXNReader();
                reader.insertResult(json);
            } else if (analyzerName.toLowerCase().contains("cobas c311")) {
                CobasC311JsonReader reader = new CobasC311JsonReader();
                reader.insertResult(json);
            } else if (analyzerName.toLowerCase().contains("dxc 700")) {
                Dxc700Reader reader = new Dxc700Reader();
                reader.insertResult(json);
            } else {
                response.getWriter().print("Unknown analyzer with name: " + analyzerName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean userValid(String user, String password) {
        Login login = new Login();
        login.setLoginName(user);
        login.setPassword(password);

        LoginDAO loginDAO = new LoginDAOImpl();

        login = loginDAO.getValidateLogin(login);



        if( login == null ){
            return false;
        }else{
            SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
            SystemUser systemUser = systemUserDAO.getDataForLoginUser(login.getLoginName());
            systemUserId = systemUser.getId();
        }

        return true;
    }
}