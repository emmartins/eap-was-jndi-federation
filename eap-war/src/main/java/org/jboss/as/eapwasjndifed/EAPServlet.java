/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.eapwasjndifed;

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The servlet which invokes the remote bean.
 * @author Eduardo Martins
 */
@WebServlet("/EAPServlet")
public class EAPServlet extends HttpServlet {

    private static final String REMOTE_EJB_RELATIVE_JNDI_NAME = "nodes/localhostNode01/servers/server1/ejb/eap-was-jndi-federation-was-ejb-1_0_0-SNAPSHOT_jar/eap-was-jndi-federation-was-ejb-1\\.0\\.0-SNAPSHOT\\.jar/EchoBean#org\\.jboss\\.as\\.eapwasjndifed\\.EchoHome";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.println("<html><head><title>JBoss EAP Servlet</title></head><body>");
        try{
            writer.println("<p>Invoking remote bean obtained through JNDI federated context: "+lookupEcho("OK")+"</p>");
        } catch (Exception e){
            writer.println("<p>Exception: " + e+"</p>");
        }
        writer.println("</body></html>");
        writer.close();
    }

    private String lookupEcho(String msg) throws Exception {
        InitialContext ctx = new InitialContext();
        Object obj = ctx.lookup("java:global/was/" + REMOTE_EJB_RELATIVE_JNDI_NAME);
        EchoHome echoHome = (EchoHome) PortableRemoteObject.narrow(obj, EchoHome.class);
        EchoObject echoObject = echoHome.create();
        try {
            return echoObject.echo(msg);
        } finally {
            echoObject.remove();
        }
    }

}