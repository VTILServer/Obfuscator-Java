package icu.Xell.Mainline.obfuscator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.engine.RWTServletContextListener;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final class XellRapServer {
    private XellRapServer() {
    }

    static void start(int port) throws Exception {
        Server server = new Server(new InetSocketAddress("127.0.0.1", port));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Path resourceRoot = Files.createDirectories(Paths.get("target", "rap-context"));
        context.setBaseResource(org.eclipse.jetty.util.resource.Resource.newResource(resourceRoot.toFile()));
        context.setAttribute("resource_root_location", resourceRoot.toAbsolutePath().toString());
        context.setInitParameter(ApplicationConfiguration.CONFIGURATION_PARAM, XellRapApplication.class.getName());
        context.addEventListener(new RWTServletContextListener());

        context.addServlet(new ServletHolder(new RootRedirectServlet()), "/");
        context.addServlet(new ServletHolder(new DefaultServlet()), "/rwt-resources/*");
        ServletHolder rap = new ServletHolder(new RWTServlet());
        context.addServlet(rap, XellRapApplication.ENTRY_POINT_PATH);

        server.setHandler(context);
        server.start();
        System.out.println("Xell RAP UI running at http://127.0.0.1:" + port + XellRapApplication.ENTRY_POINT_PATH);
        server.join();
    }

    private static final class RootRedirectServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.sendRedirect(request.getContextPath() + XellRapApplication.ENTRY_POINT_PATH);
        }
    }
}
