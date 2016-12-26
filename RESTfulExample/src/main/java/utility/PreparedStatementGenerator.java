package utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PreparedStatementGenerator {

	public static PreparedStatement createQuery(Connection connection,String sql,List<?> params){
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			if(params !=null){
				for(int i=0;i<params.size();i++){
					preparedStatement.setObject(i+1, params.get(i));
				}
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return preparedStatement;
	}
}
