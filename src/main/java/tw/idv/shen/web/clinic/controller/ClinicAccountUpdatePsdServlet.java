package tw.idv.shen.web.clinic.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tw.idv.shen.core.util.CommonUtil;
import tw.idv.shen.web.clinic.service.ClinicService;

@WebServlet("/clinic/clinicaccountupdatepsd")
public class ClinicAccountUpdatePsdServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClinicService service;
	
	@Override
    public void init() throws ServletException {
		service = CommonUtil.getBean(getServletContext(), ClinicService.class);
    }
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		final HttpSession session = request.getSession();
//		final String username = ((Clinic) session.getAttribute("clinic_id")).getUsername();
//		Clinic clinic = json2Pojo(request, Member.class);
//		member.setUsername(username);
//		writePojo2Json(response, service.edit(member));
	}
}
