package controller;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@WebServlet("/MyDerbyClientServlet")
public class MyDerbyClientServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    // Resource injection (DerbyDB)
    @Resource(name="java:global/DerbyPool")
    DataSource ds;
    private ArrayList<Integer> employeeNumbers = new ArrayList<>();
    private ArrayList<String> employeeNames = new ArrayList<>();
    private ArrayList<String> employeeJobs = new ArrayList<>();
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        Connection myPooledConnection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            myPooledConnection = ds.getConnection();
            System.out.println("Got pooled connection to DerbyDB");
            // Important!!! CHANGE database name in GLassFish Server Admin Console Tool on localhost:4848
            // in section JDBC: Connection pools value in DerbyPool to database name you want to work with
            statement = myPooledConnection.createStatement();
            resultset = statement.executeQuery("SELECT EMPNO,ENAME,JOB_TITLE FROM Employee");
            while(resultset.next()){
                int employeeNumber = resultset.getInt("EMPNO");
                String employeeName = resultset.getString("ENAME");
                String jobTitle = resultset.getString("JOB_TITLE");
                employeeNumbers.add(employeeNumber);
                employeeNames.add(employeeName);
                employeeJobs.add(jobTitle);
            }
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Derby Client Page</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>" + getEmployeeInfo(0) + "</p><br/>");
            out.println("<p>" + getEmployeeInfo(1) + "</p><br/>");
            out.println("<p>" + getEmployeeInfo(2) + "</p><br/>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                if (myPooledConnection != null) {
                    myPooledConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (resultset != null) {
                    resultset.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public MyDerbyClientServlet() {
        super();
    }

    private String getEmployeeInfo(int index){
        return employeeNumbers.get(index) + " : " + employeeNames.get(index) + " : " + employeeJobs.get(index);
    }
}

