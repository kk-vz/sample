package com.verizon.ves.webservice.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.verizon.ves.dao.EnterpriseOrderDAO;
import com.verizon.ves.restclient.OrderManagementRestClient;
import com.verizon.ves.ui.ContractDetails;
import com.verizon.ves.ui.EnterpriseOrder;



@WebServlet("/EditOrderServlet")
public class EditOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String editOrderGetURL = "http://192.168.1.23:8080/OMTest/rest/profilepull/email";
	private static final String editOrderPutURL = "http://192.168.1.23:8080/OMTest/rest/profilepull/email";

    public EditOrderServlet() {
        super();

    }

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String contractid = request.getParameter("contractid");
		HttpSession session=request.getSession();
		
		String contractidJson = "{\"contractid\":\""+contractid+"\"}";
		String outputJson = new OrderManagementRestClient().callService(contractidJson, editOrderGetURL);
		
		
		System.out.println(outputJson);
		
		
		if(outputJson.equals("null"))
		{
			session.setAttribute("contractidvalidity", "invalid");
			
		}
		else
		{
			session.setAttribute("contractidvalidity", "valid");
			ContractDetails contractdetails = new Gson().fromJson(outputJson, ContractDetails.class); 
			String change = request.getParameter("change");
			String changeJson = "{\"change\":\""+change+"\"}";
			String enterpriseJson = new OrderManagementRestClient().callService(changeJson, editOrderPutURL);
			
			if(enterpriseJson.equals("null"))
			{
				session.setAttribute("ordering", "failed");
			}
			else
			{
				// send email to customer
				session.setAttribute("ordering", "success");
				EnterpriseOrder enterpriseOrder=new Gson().fromJson(enterpriseJson, EnterpriseOrder.class);
				
				boolean status = new EnterpriseOrderDAO().EditOrder( enterpriseOrder.getOrderid(), enterpriseOrder.getContractid(), enterpriseOrder.getCustomerid());  
				session.setAttribute("contractdetails", contractdetails);
				
			}
			
		}

	}

}
