package sql;

public class FaqSql {

	public static String loadAllFaqsQuery = "select * from fapp_faq_master WHERE is_delete = 'N' and is_active='Y'";
	
	public static String saveFaqQuery = "INSERT INTO fapp_faq_master("
	           +" faq_question, faq_answer, is_active, created_by) VALUES (?, ?, ?, ?)";
	
	public static String updateFaqQuery = "UPDATE fapp_faq_master "
			    +" SET  faq_question=?, faq_answer=?, is_active=?, updated_date=current_timestamp, updated_by=?"
			    +" WHERE faq_id=?;";
	
	public static String deleteFaqQuery = "UPDATE fapp_faq_master "
		    +" SET is_delete='Y', updated_date=current_timestamp, updated_by=?"
		    +" WHERE faq_id=?;";

}
