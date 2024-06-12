package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.MembersDAO;
import dto.MembersDTO;


@WebServlet("*.members")
public class MembersController extends HttpServlet {

	public static String getSHA512(String input){
		String toReturn = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			toReturn = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 한글깨짐 방지
		request.setCharacterEncoding("utf8"); 
		// 클라이언트로 부터 전송되는 무자열에 대한 인코딩을 utf 처리
		// request에서 값을 꺼내기 전에 처리해야만 함!
		
		MembersDAO dao = MembersDAO.getInstance();
		String cmd = request.getRequestURI();
		
		System.out.println("사용자 요청 : " + cmd);

		try{
			
			// 아이디 중복 확인
			if(cmd.equals("/idcheck.members")) {
				String userId = request.getParameter("userId");
				System.out.println(userId);
				boolean result = dao.tf(userId);
				
				PrintWriter userPwd = response.getWriter();
				userPwd.append(String.valueOf(result));

				System.out.println(result);
				

			}
			// 회원 가입
			else if(cmd.equals("/singup.members")){
				String userId = request.getParameter("userId");
				String userPwd = request.getParameter("userPwd");
				String userSpw = getSHA512(userPwd);
				String userName = request.getParameter("userName");
				String nickName = request.getParameter("nickName");
				String phone = request.getParameter("phone");
				String email = request.getParameter("email");
				String birth_date = request.getParameter("birth_date");
				String gender = request.getParameter("gender");
				Timestamp join_date=new Timestamp(System.currentTimeMillis());
				String adminKey = request.getParameter("adminKey");
				int result = dao.insert(new MembersDTO(0,userId,userPwd,userName,nickName,phone,email,birth_date,gender,adminKey, join_date,0));	
				response.sendRedirect("index.jsp");
			}
			// 로그인 시 아이디 확인
			else if(cmd.equals("/login.members")) {
				String userId = request.getParameter("userId");
				String userPwd = request.getParameter("userPwd");
				String userSpw = getSHA512(userPwd);
				boolean result = dao.login(userId, userSpw);
				
				if(result) {

					HttpSession session = request.getSession();
					session.setAttribute("loginID", userId);

				}
				response.sendRedirect("/index.jsp");

			}
			// 로그아웃
			else if(cmd.equals("/logout.members")){
				HttpSession session = request.getSession();
				session.invalidate();
				response.sendRedirect("/index.jsp");
			}
			// 회원탈퇴
			else if(cmd.equals("/memberout.members")) {
				
				HttpSession session = request.getSession();
				String result = (String)session.getAttribute("loginID");
				dao.deleteById(result);
				response.sendRedirect("/index.jsp");	
			}
			// 내 정보
			else if(cmd.equals("/mypage.members")) {
				HttpSession session = request.getSession();
				String result = (String)session.getAttribute("loginID");
				dao.selectAll(result);
				request.setAttribute("dto", dao.selectAll(result));
				request.getRequestDispatcher("/members/mypage.jsp").forward(request, response);
			}
			// 수정
			else if(cmd.equals("/edit.members")) {
				System.out.println("진입");
				HttpSession session = request.getSession();
				String result = (String)session.getAttribute("loginID");
				String userName = request.getParameter("userName");
				String phone = request.getParameter("phone");
				String email = request.getParameter("email");
				request.setAttribute("dto",dao.edit(result, userName, phone, email));
				request.getRequestDispatcher("/mypage.members").forward(request, response);
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		doGet(request, response);
	}

}
