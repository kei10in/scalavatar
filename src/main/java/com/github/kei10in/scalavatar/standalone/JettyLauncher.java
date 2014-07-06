package com.github.kei10in.scalavatar.standalone;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;

public class JettyLauncher {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (System.getenv("PORT") != null)
            port = Integer.parseInt(System.getenv("PORT"));

        URL location = JettyLauncher.class.getProtectionDomain().getCodeSource().getLocation();

        Server server = new Server(port);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
        context.setWar(location.toExternalForm());

        server.setHandler(context);

        server.start();
        server.join();
    }
}
