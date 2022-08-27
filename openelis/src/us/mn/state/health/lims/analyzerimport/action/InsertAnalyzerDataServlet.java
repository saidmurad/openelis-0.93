package us.mn.state.health.lims.analyzerimport.action;

import org.json.JSONArray;

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
import us.mn.state.health.lims.analyzerimport.analyzerreaders.DxH800Reader;
import us.mn.state.health.lims.login.dao.LoginDAO;
import us.mn.state.health.lims.login.daoimpl.LoginDAOImpl;
import us.mn.state.health.lims.login.valueholder.Login;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

public class InsertAnalyzerDataServlet extends HttpServlet {

    private String systemUserId;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader bufferedReader = request.getReader();
        JSONTokener tokener = new JSONTokener(bufferedReader);

        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = DatatypeConverter.parseBase64Binary(base64Credentials);
            //byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String user = values[0];
            String password = values[1];
            if( !userValid(user, password)){
                response.getWriter().print("invalid user/password");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else {
            response.getWriter().print("Authentication is required");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            DxH800Reader dxH800Reader = new DxH800Reader();
            JSONArray jsonArray = new JSONArray(tokener);
            String analyzerName =(String) ((JSONObject) jsonArray.get(0)).get("machineName");
            if (analyzerName.toLowerCase() == "dxh800" ) {
                dxH800Reader.insertResult(jsonArray);
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