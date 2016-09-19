package dao;

import sql.SameUserSQL;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sqlQuery =  "";
		if(SameUserSQL.sameUserQuery.contains("itemcodes"))
			sqlQuery = SameUserSQL.sameUserQuery.replace("itemcodes", "('1','2')");
		
		System.out.println(sqlQuery);
		
	}

}
