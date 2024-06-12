package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import dto.MembersDTO;

public class MembersDAO {
	public static MembersDAO instance;

	public synchronized static MembersDAO getInstance() {  
		if(instance == null) {
			instance = new MembersDAO();
		}
		return instance;
	}
	private Connection getConnection() throws Exception { 
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}

	private MembersDAO() {}


	// 작성
	public int insert(MembersDTO dto)throws Exception{
		String sql = "insert into members values(members_seq, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, null, 0)";
		try (Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, dto.getUserId());
			pstat.setString(2, dto.getUserPwd());
			pstat.setString(3, dto.getUserName());
			pstat.setString(4, dto.getNickName());
			pstat.setString(5, dto.getPhone());
			pstat.setString(6, dto.getEmail());
			pstat.setString(7, dto.getGender());
			pstat.setString(8, dto.getSingout());
			

			int result = pstat.executeUpdate();
			return result;
		}

	}

	public boolean tf(String userId) throws Exception{
		String sql = "select * from members where userId = ?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, userId);
			try(ResultSet rs = pstat.executeQuery();){
				boolean result = rs.next();
				return result;
			}
		}
	}

	public boolean login(String userId,String userPwd)throws Exception{
		String sql = "select * from members where userId =? and userPwd = ?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, userId);
			pstat.setString(2, userPwd);
			try(ResultSet rs = pstat.executeQuery();){
				boolean result =rs.next();
				return result;
			}
		}	
	}

	public int deleteById(String userId)throws Exception{
		String sql = "delete from members where userId=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, userId);
			int result = pstat.executeUpdate();
			return result;
		}
	}
	public MembersDTO selectAll(String result)throws Exception{
		String sql = "Select * from members where userId=? ";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, result);

			try(ResultSet rs = pstat.executeQuery();){
				rs.next(); 
				String userId =rs.getString("userId");
				String userName = rs.getString("userName");
				String nickName = rs.getString("nickName");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				String birth_date = rs.getString("birth_date");
				String gender = rs.getString("gender");
				Timestamp join_date = rs.getTimestamp("join_date");
				return new MembersDTO(0,userId,null,userName,nickName,phone,email,birth_date,gender,gender,join_date,null,0);
			}
		}
	}

	public int edit(String result, String userName, String phone, String email)throws Exception{
		String sql = "update members set userName = ?, phone = ?, email = ? where userId = ?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, userName);
			pstat.setString(2, phone);
			pstat.setString(3, email);
			pstat.setString(4, result);
			int editresult = pstat.executeUpdate();
			return editresult;

		}

	}
}

