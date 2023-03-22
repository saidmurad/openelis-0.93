/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is OpenELIS code.
 *
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.common.provider.query;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dashboard.dao.OrderListDAO;
import us.mn.state.health.lims.dashboard.daoimpl.OrderListDAOImpl;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class OrderedTestsPopulateProvider extends BaseQueryProvider {
    private static OrderListDAO orderListDAO = new OrderListDAOImpl(false);

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {


        String accessionNumber = (String) request.getParameter("accessionNumber");
        StringBuilder xml = new StringBuilder();

        String result = null;


        if(accessionNumber != null) {
            List<String> tests = orderListDAO.getTestsByAccession(accessionNumber);
            if(tests.size() > 0) {
                result = createTestsResultXML(tests);
            }

        }
        if (result.equals("")) {
            result = StringUtil.getMessageForKey("patient.message.patientNotFound");
            xml.append("empty");
        }
        ajaxServlet.sendData(result, result, request, response);
    }

    private String createTestsResultXML(List<String> stringList) {
        DocumentBuilderFactory dbFactory = null;
        DocumentBuilder dBuilder = null;
        Document doc = null;
        Element rootElement = null;
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();

            // create a new document
            doc = dBuilder.newDocument();

            // create the root element
            rootElement = doc.createElement("patientTests");
            doc.appendChild(rootElement);

            // add child elements to the root element
            Element stringElement1 = doc.createElement("tests");
            Element stringElement2 = doc.createElement("patientName");
            Element stringElement3 = doc.createElement("gender");
            Element stringElement4 = doc.createElement("birth_date");

            stringElement1.appendChild(doc.createTextNode(stringList.get(0)));
            stringElement2.appendChild(doc.createTextNode(stringList.get(1)));
            stringElement3.appendChild(doc.createTextNode(stringList.get(2)));
            stringElement4.appendChild(doc.createTextNode(stringList.get(3)));

            rootElement.appendChild(stringElement1);
            rootElement.appendChild(stringElement2);
            rootElement.appendChild(stringElement3);
            rootElement.appendChild(stringElement4);

            TransformerFactory tf = TransformerFactory.newInstance();

            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString().replace("<patientTests>","").replace("</patientTests>", "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
